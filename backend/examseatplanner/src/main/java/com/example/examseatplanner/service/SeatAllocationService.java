    package com.example.examseatplanner.service;
    
    import com.example.examseatplanner.dto.RoomSeatingDTO;
    import com.example.examseatplanner.dto.RowSeatingDTO;
    import com.example.examseatplanner.dto.SeatDTO;
    import com.example.examseatplanner.dto.SideSeatingDTO;
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
    
        public Map<String, List<SeatDTO>> getSeatAssignmentsByExamId(Integer examId) {
            // Fetch seats assigned for the given examId
            // Note: Make sure your SeatRepository has this method implemented properly
            List<Seat> seats = seatRepository.findByExamId(examId);
    
            Map<String, List<SeatDTO>> assignmentsByRoom = new HashMap<>();
    
            for (Seat seat : seats) {
                if (seat.getRoom() == null) continue; // Defensive null check
    
                String roomNumber = seat.getRoom().getRoomNo()+"";
    
                SeatDTO dto = SeatDTO.fromEntity(seat);
    
                assignmentsByRoom.computeIfAbsent(roomNumber, k -> new ArrayList<>()).add(dto);
            }
    
            return assignmentsByRoom;
        }

        public SeatAllocationResult allocateSeatsByProgramColumns(Exam exam) {
            List<Student> allStudents = studentService.getStudentsForExam(exam);
            int totalStudents = allStudents.size();

            // Total capacity across all rooms
            int totalCapacity = calculateTotal3DCapacity(exam.getRooms());
            if (totalStudents > totalCapacity) {
                throw new RuntimeException("Not enough seats for all students. Required: " +
                        totalStudents + ", Available: " + totalCapacity);
            }

            // Group students by program and sort by roll
            Map<Integer, Queue<Student>> studentsByProgram = new LinkedHashMap<>();
            for (Student s : allStudents) {
                studentsByProgram
                        .computeIfAbsent(s.getProgram().getProgramCode(), k -> new LinkedList<>())
                        .add(s);
            }
            // Sort each program queue by roll ascending
            for (Map.Entry<Integer, Queue<Student>> entry : studentsByProgram.entrySet()) {
                List<Student> sorted = entry.getValue().stream()
                        .sorted(Comparator.comparingInt(Student::getRoll))
                        .toList();
                entry.setValue(new LinkedList<>(sorted));
            }

            List<Integer> programOrder = new ArrayList<>(studentsByProgram.keySet());
            List<SeatAssignment> assignments = new ArrayList<>();

            // Iterate through rooms
            for (Room room : exam.getRooms()) {
                Seat[][][] seats = generate3DSeatArray(room);
                int sides = 3;
                int rows = room.getNumRow();
                int columns = room.getNumColumn();

                boolean studentsLeft = !studentsByProgram.isEmpty();

                outer:
                for (int side = 0; side < sides && studentsLeft; side++) {
                    for (int row = 0; row < rows && studentsLeft; row++) {
                        for (int bench = 0; bench < columns && studentsLeft; bench++) {
                            for (int pos = 0; pos < Room.SEATS_PER_BENCH && studentsLeft; pos++) {
                                // Rotate through programs to alternate students
                                Student studentToAssign = null;
                                Iterator<Integer> programIterator = programOrder.iterator();

                                while (programIterator.hasNext() && studentToAssign == null) {
                                    Integer programCode = programIterator.next();
                                    Queue<Student> queue = studentsByProgram.get(programCode);

                                    if (queue != null && !queue.isEmpty()) {
                                        studentToAssign = queue.poll();
                                        if (queue.isEmpty()) {
                                            studentsByProgram.remove(programCode);
                                            programIterator.remove();
                                        }
                                    }
                                }

                                if (studentToAssign == null) {
                                    studentsLeft = false;
                                    break outer; // No students left to assign
                                }

                                // Assign student to seat
                                Seat seat = seats[side][row][bench * Room.SEATS_PER_BENCH + pos];
                                seat.setAssignedStudent(studentToAssign);
                                seat.setRoom(room);
                                seatRepository.save(seat);
                                assignments.add(new SeatAssignment(seat, studentToAssign));
                            }
                        }
                    }
                }
            }

            return new SeatAllocationResult(assignments, exam, totalStudents, totalCapacity - totalStudents);
        }


        /**
         * Main method to allocate seats for an exam
         * Creates 3D seating arrangement and assigns students with program alternation
         */
        public SeatAllocationResult allocateSeatsForExam(Exam exam) {
            List<Student> allStudents = studentService.getStudentsForExam(exam);
            int totalStudents = allStudents.size();
            int totalCapacity = calculateTotal3DCapacity(exam.getRooms());
    
            if (totalStudents > totalCapacity) {
                throw new RuntimeException("Not enough seats for all students");
            }
    
            List<SeatAssignment> assignments = new ArrayList<>();
            int studentIndex = 0;
    
            for (Room room : exam.getRooms()) {
                Seat[][][] seats = generate3DSeatArray(room);
    
                int sides = 3;
                int rows = room.getNumRow();
                int columns = room.getNumColumn();

                outer:
                for (int side = 0; side < sides; side++) {
                    for (int row = 0; row < rows; row++) {
                        for (int bench = 0; bench < columns; bench++) {
                            for (int pos = 0; pos < Room.SEATS_PER_BENCH; pos++) {
                                if (studentIndex >= totalStudents) {
                                    break outer;
                                }
                                Seat seat = seats[side][row][bench * Room.SEATS_PER_BENCH + pos];
                                Student student = allStudents.get(studentIndex);
                                seat.setAssignedStudent(student);
                                seatRepository.save(seat);
                                assignments.add(new SeatAssignment(seat, student));
                                studentIndex++;
                            }
                        }
                    }
                }
                if (studentIndex >= totalStudents) {
                    break;
                }
            }
    
            return new SeatAllocationResult(assignments, exam, totalStudents, totalCapacity - totalStudents);
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
    
        public List<RoomSeatingDTO> getRoomSeatingInfo(Integer examId) {
            Optional<Exam> examOpt = examService.getExamById(examId);
            if (examOpt.isEmpty()) return Collections.emptyList();
    
            Exam exam = examOpt.get();
            List<RoomSeatingDTO> result = new ArrayList<>();
    
            for (Room room : exam.getRooms()) {
                // Fetch seats for the room
                List<Seat> roomSeats = seatRepository.findByRoomRoomNo(room.getRoomNo());
    
                // Organize seats by side and row
                // Create 3 sides: 0=Left, 1=Middle, 2=Right
                List<SideSeatingDTO> sides = new ArrayList<>();
                String[] sideNames = {"Left", "Middle", "Right"};
    
                for (int side = 0; side < 3; side++) {
                    List<RowSeatingDTO> rows = new ArrayList<>();
    
                    for (int row = 0; row < room.getNumRow(); row++) {
                        // Filter seats for this side and row, sorted by bench and seat position
                        int finalSide = side;
                        int finalRow = row;
                        List<SeatDTO> seatsInRow = roomSeats.stream()
                                .filter(seat -> seat.getSeatSide() == finalSide && seat.getRowNumber() == finalRow)
                                .sorted(Comparator
                                        .comparingInt(Seat::getBenchNumber)
                                        .thenComparingInt(Seat::getSeatPosition))
                                .map(SeatDTO::fromEntity)
                                .collect(Collectors.toList());
    
                        rows.add(new RowSeatingDTO(row, seatsInRow));
                    }
    
                    sides.add(new SideSeatingDTO(side, sideNames[side], rows));
                }
    
                // Now pass sides (List<SideSeatingDTO>) to RoomSeatingDTO
                result.add(new RoomSeatingDTO(
                        room.getRoomNo(),
                        room.getNumRow(),
                        room.getNumColumn(),
                        room.getSeatingCapacity(),
                        sides
                ));
            }
    
            return result;
        }
    
    
    
        /**
         * Creates an optimal student sequence with program alternation
         * This prevents students from the same program sitting adjacent to each other
         */
        private List<Student> createOptimalStudentSequence(List<Student> allStudents, List<Program> programs) {
            // Group students by program and sort by roll
            Map<Integer, Queue<Student>> studentsByProgram = new HashMap<>();
            for (Student student : allStudents) {
                studentsByProgram
                        .computeIfAbsent(student.getProgram().getProgramCode(), k -> new LinkedList<>())
                        .add(student);
            }
            for (Queue<Student> queue : studentsByProgram.values()) {
                List<Student> sorted = queue.stream()
                        .sorted(Comparator.comparingInt(Student::getRoll))
                        .toList();
                queue.clear();
                queue.addAll(sorted);
            }
    
            // Alternate programs
            List<Student> sequence = new ArrayList<>();
            List<Integer> programCodes = new ArrayList<>(studentsByProgram.keySet());
    
            while (!studentsByProgram.isEmpty()) {
                Iterator<Integer> programIterator = programCodes.iterator();
                while (programIterator.hasNext()) {
                    Integer programCode = programIterator.next();
                    Queue<Student> queue = studentsByProgram.get(programCode);
                    if (queue != null && !queue.isEmpty()) {
                        sequence.add(queue.poll());
                        if (queue.isEmpty()) {
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