package com.example.examseatplanner.service;

import com.example.examseatplanner.dto.SeatDTO;
import com.example.examseatplanner.model.*;
import com.example.examseatplanner.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SeatAllocationService {

    private final SeatRepository seatRepository;
    private final StudentService studentService;
    private final ExamService examService;

    @Autowired
    public SeatAllocationService(SeatRepository seatRepository,
                                 StudentService studentService,
                                 ExamService examService) {
        this.seatRepository = seatRepository;
        this.studentService = studentService;
        this.examService = examService;
    }

    public Map<String, List<SeatDTO>> getSeatAssignments(Integer examId) {
        Optional<Exam> examOpt = examService.getExamById(examId);
        if (examOpt.isEmpty()) {
            return new HashMap<>();
        }

        Exam exam = examOpt.get();
        Map<String, List<SeatDTO>> result = new HashMap<>();

        for (Room room : exam.getRooms()) {
            List<Seat> roomSeats = seatRepository.findByRoomRoomNo(room.getRoomNo());
            List<SeatDTO> seatDTOs = roomSeats.stream()
                    .filter(seat -> seat.getAssignedStudent() != null) // Only assigned seats
                    .map(SeatDTO::fromEntity)
                    .collect(Collectors.toList());
            result.put(room.getRoomNo().toString(), seatDTOs); // Use roomNo, not roomName
        }

        return result;
    }

    /**
     * Main method to allocate seats for an exam
     * Creates 3D seating arrangement and assigns students with program alternation
     */
    public SeatAllocationResult allocateSeatsForExam(Exam exam) {
        // Get all students from exam programs
        List<Student> allStudents = studentService.getStudentsForExam(exam);


        // Calculate total 3D capacity (3 sides per room)
        int totalStudents = allStudents.size();
        int total3DCapacity = calculateTotal3DCapacity(exam.getRooms());

        if (totalStudents > total3DCapacity) {
            throw new RuntimeException("Not enough seats for all students. Required: " +
                    totalStudents + ", Available: " + total3DCapacity);
        }

        // Generate 3D seat layout and allocate students
        List<SeatAssignment> assignments = new ArrayList<>();
        int studentIndex = 0;

        // Shuffle students by programs to ensure good distribution
        List<Student> shuffledStudents = createOptimalStudentSequence(allStudents, exam.getPrograms());

        for (Room room : exam.getRooms()) {
            // Generate 3D seat array for this room
            Seat[][][] seatArray = generate3DSeatArray(room);

            // Allocate students to seats in this room
            for (int side = 0; side < 3 && studentIndex < shuffledStudents.size(); side++) {
                for (int row = 0; row < room.getNumRow() && studentIndex < shuffledStudents.size(); row++) {
                    for (int bench = 0; bench < room.getNumColumn() && studentIndex < shuffledStudents.size(); bench++) {
                        for (int position = 0; position < Room.SEATS_PER_BENCH && studentIndex < shuffledStudents.size(); position++) {

                            Seat seat = seatArray[side][row][bench * Room.SEATS_PER_BENCH + position];
                            Student student = shuffledStudents.get(studentIndex);

                            // Assign student to seat
                            seat.setAssignedStudent(student);

                            // Save seat assignment
                            Seat savedSeat = seatRepository.save(seat);
                            assignments.add(new SeatAssignment(savedSeat, student));

                            studentIndex++;
                        }
                    }
                }
            }
        }

        return new SeatAllocationResult(assignments, exam, totalStudents, total3DCapacity - totalStudents);
    }

    /**
     * Calculate total 3D capacity across all rooms
     * Each room has 3 sides, so multiply base capacity by 3
     */
    private int calculateTotal3DCapacity(List<Room> rooms) {
        return rooms.stream()
                .mapToInt(room -> room.getSeatingCapacity() * 3) // 3 sides per room
                .sum();
    }

    /**
     * Generates a 3D array of seats for a room
     * Dimensions: [sides][rows][seats_per_row]
     * sides = 3 (Left, Middle, Right)
     * rows = room.getNumRow()
     * seats_per_row = room.getNumColumn() * SEATS_PER_BENCH
     */
    private Seat[][][] generate3DSeatArray(Room room) {
        int sides = 3; // Left (0), Middle (1), Right (2)
        int rows = room.getNumRow();
        int seatsPerRow = room.getNumColumn() * Room.SEATS_PER_BENCH;

        Seat[][][] seatArray = new Seat[sides][rows][seatsPerRow];

        for (int side = 0; side < sides; side++) {
            for (int row = 0; row < rows; row++) {
                for (int seatInRow = 0; seatInRow < seatsPerRow; seatInRow++) {
                    int benchNumber = seatInRow / Room.SEATS_PER_BENCH;
                    int seatPosition = seatInRow % Room.SEATS_PER_BENCH;

                    Seat seat = new Seat();
                    seat.setRowNumber(row);
                    seat.setBenchNumber(benchNumber);
                    seat.setSeatSide(side);
                    seat.setSeatPosition(seatPosition);
                    seat.setRoom(room);
                    seat.setAssignedStudent(null); // Will be assigned later

                    seatArray[side][row][seatInRow] = seat;
                }
            }
        }

        return seatArray;
    }

    /**
     * Creates an optimal student sequence with program alternation
     * This prevents students from the same program sitting adjacent to each other
     */
    private List<Student> createOptimalStudentSequence(List<Student> allStudents, List<Program> programs) {
        // Group students by program
        Map<Integer, List<Student>> studentsByProgram = new HashMap<>();
        for (Student student : allStudents) {
            studentsByProgram.computeIfAbsent(
                    student.getProgram().getProgramCode(),
                    k -> new ArrayList<>()
            ).add(student);
        }

        // Shuffle students within each program
        for (List<Student> programStudents : studentsByProgram.values()) {
            Collections.shuffle(programStudents);
        }

        // Create alternating sequence
        List<Student> sequence = new ArrayList<>();
        List<Integer> programCodes = new ArrayList<>(studentsByProgram.keySet());

        // Continue until all students are assigned
        while (!studentsByProgram.isEmpty()) {
            Iterator<Integer> programIterator = programCodes.iterator();

            while (programIterator.hasNext()) {
                Integer programCode = programIterator.next();
                List<Student> programStudents = studentsByProgram.get(programCode);

                if (programStudents != null && !programStudents.isEmpty()) {
                    sequence.add(programStudents.remove(0));

                    // Remove program if no more students
                    if (programStudents.isEmpty()) {
                        studentsByProgram.remove(programCode);
                        programIterator.remove();
                    }
                }
            }
        }

        return sequence;
    }

    /**
     * Gets the seating chart for a specific exam
     */
    public Map<Room, Seat[][][]> getSeatingChart(Exam exam) {
        Map<Room, Seat[][][]> seatingChart = new HashMap<>();

        for (Room room : exam.getRooms()) {
            List<Seat> roomSeats = seatRepository.findByRoomRoomNo(room.getRoomNo());
            Seat[][][] seatArray = organizeSeatsByPosition(room, roomSeats);
            seatingChart.put(room, seatArray);
        }

        return seatingChart;
    }

    /**
     * Organizes seats from database into 3D array structure
     */
    private Seat[][][] organizeSeatsByPosition(Room room, List<Seat> seats) {
        int sides = 3;
        int rows = room.getNumRow();
        int seatsPerRow = room.getNumColumn() * Room.SEATS_PER_BENCH;

        Seat[][][] seatArray = new Seat[sides][rows][seatsPerRow];

        for (Seat seat : seats) {
            int side = seat.getSeatSide();
            int row = seat.getRowNumber();
            int seatIndex = seat.getBenchNumber() * Room.SEATS_PER_BENCH + seat.getSeatPosition();

            if (side < sides && row < rows && seatIndex < seatsPerRow) {
                seatArray[side][row][seatIndex] = seat;
            }
        }

        return seatArray;
    }

    /**
     * Clears all seat assignments for an exam
     */
    public void clearSeatAssignments(Exam exam) {
        for (Room room : exam.getRooms()) {
            List<Seat> roomSeats = seatRepository.findByRoomRoomNo(room.getRoomNo());
            for (Seat seat : roomSeats) {
                seat.setAssignedStudent(null);
                seatRepository.save(seat);
            }
        }
    }

    /**
     * Validates seat allocation rules
     */
    public List<String> validateSeatAllocation(Exam exam) {
        List<String> violations = new ArrayList<>();
        Map<Room, Seat[][][]> seatingChart = getSeatingChart(exam);

        for (Map.Entry<Room, Seat[][][]> entry : seatingChart.entrySet()) {
            Room room = entry.getKey();
            Seat[][][] seats = entry.getValue();

            // Check for adjacent students from same program
            for (int side = 0; side < 3; side++) {
                for (int row = 0; row < room.getNumRow(); row++) {
                    for (int seatIndex = 0; seatIndex < room.getNumColumn() * Room.SEATS_PER_BENCH - 1; seatIndex++) {
                        Seat currentSeat = seats[side][row][seatIndex];
                        Seat nextSeat = seats[side][row][seatIndex + 1];

                        if (currentSeat != null && nextSeat != null &&
                                currentSeat.getAssignedStudent() != null && nextSeat.getAssignedStudent() != null) {

                            if (currentSeat.getAssignedStudent().getProgram().getProgramCode().equals(
                                    nextSeat.getAssignedStudent().getProgram().getProgramCode())) {

                                violations.add(String.format(
                                        "Room %d: Adjacent students from same program at Side %d, Row %d, Seats %d-%d",
                                        room.getRoomNo(), side, row, seatIndex, seatIndex + 1
                                ));
                            }
                        }
                    }
                }
            }
        }

        return violations;
    }

    // Inner classes for result structures
    public static class SeatAllocationResult {
        private final List<SeatAssignment> assignments;
        private final Exam exam;
        private final int totalStudents;
        private final int remainingCapacity;

        public SeatAllocationResult(List<SeatAssignment> assignments, Exam exam,
                                    int totalStudents, int remainingCapacity) {
            this.assignments = assignments;
            this.exam = exam;
            this.totalStudents = totalStudents;
            this.remainingCapacity = remainingCapacity;
        }

        // Getters
        public List<SeatAssignment> getAssignments() { return assignments; }
        public Exam getExam() { return exam; }
        public int getTotalStudents() { return totalStudents; }
        public int getRemainingCapacity() { return remainingCapacity; }
    }

    public static class SeatAssignment {
        private final Seat seat;
        private final Student student;

        public SeatAssignment(Seat seat, Student student) {
            this.seat = seat;
            this.student = student;
        }

        // Getters
        public Seat getSeat() { return seat; }
        public Student getStudent() { return student; }
    }
}