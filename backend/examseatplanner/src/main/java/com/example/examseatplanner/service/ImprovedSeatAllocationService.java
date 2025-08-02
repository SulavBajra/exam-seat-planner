package com.example.examseatplanner.service;

import com.example.examseatplanner.dto.RoomSeatDTO;
import com.example.examseatplanner.dto.SeatAssignmentDTO;
import com.example.examseatplanner.model.*;
import com.example.examseatplanner.repository.SeatAssignmentRepository;
import com.example.examseatplanner.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ImprovedSeatAllocationService {

    private final SeatAssignmentRepository seatAssignmentRepository;
    private final StudentRepository studentRepository;

    @Autowired
    public ImprovedSeatAllocationService(SeatAssignmentRepository seatAssignmentRepository,
                                         StudentRepository studentRepository) {
        this.seatAssignmentRepository = seatAssignmentRepository;
        this.studentRepository = studentRepository;
    }

    /**
     * Main seat allocation method that accepts exam and students separately
     */
    public void allocateSeats(Exam exam, List<Student> students) {
        List<Room> rooms = exam.getRooms();

        // Validate capacity first
        validateCapacity(students, rooms);

        // Group students by program code for better distribution
        Map<String, List<Student>> studentsByProgram = students.stream()
                .collect(Collectors.groupingBy(student -> extractProgramCode(student.getStudentId())));

        // Create optimized seating sequence
        List<Student> optimizedStudents = createOptimizedSequence(studentsByProgram);

        // Allocate seats using advanced algorithm
        allocateSeatsOptimized(optimizedStudents, rooms, exam);
    }

    /**
     * Overloaded method for backward compatibility - gets students automatically
     */
    public void allocateSeatsStudentId(Exam exam) {
        // Get students based on exam's subject program and semester
        List<Student> students = studentRepository.findByProgramAndSemester(
                exam.getSubject().getProgram(),
                exam.getSubject().getSemester()
        );

        allocateSeats(exam, students);
    }

    /**
     * Extract program code from studentId
     * Format: enrolledYear(4) + programCode(2) + rollNumber(2)
     * Example: "20210101" -> enrolledYear=2021, programCode=01, rollNumber=01
     */
    private String extractProgramCode(String studentId) {
        if (studentId == null || studentId.length() < 6) {
            return "00"; // Default fallback
        }
        return studentId.substring(4, 6); // Extract program code (positions 4-5)
    }

    /**
     * Extract enrolled year from studentId
     */
    private String extractEnrolledYear(String studentId) {
        if (studentId == null || studentId.length() < 4) {
            return "0000"; // Default fallback
        }
        return studentId.substring(0, 4); // Extract year (positions 0-3)
    }

    /**
     * Extract roll number from studentId
     */
    private String extractRollNumber(String studentId) {
        if (studentId == null || studentId.length() < 8) {
            return "00"; // Default fallback
        }
        return studentId.substring(6); // Extract roll number (from position 6)
    }

    /**
     * Calculate risk score between two students based on their studentIds
     * Higher score means higher risk of cheating
     */
    private double calculateCheatingRisk(String studentId1, String studentId2) {
        double riskScore = 0.0;

        String program1 = extractProgramCode(studentId1);
        String program2 = extractProgramCode(studentId2);
        String year1 = extractEnrolledYear(studentId1);
        String year2 = extractEnrolledYear(studentId2);

        // Same program penalty (highest risk)
        if (program1.equals(program2)) {
            riskScore += 0.6;
        }

        // Same year penalty
        if (year1.equals(year2)) {
            riskScore += 0.3;
        }

        // Sequential roll numbers penalty (friends might have sequential rolls)
        try {
            int roll1 = Integer.parseInt(extractRollNumber(studentId1));
            int roll2 = Integer.parseInt(extractRollNumber(studentId2));
            int rollDiff = Math.abs(roll1 - roll2);

            if (rollDiff <= 2 && program1.equals(program2)) {
                riskScore += 0.2; // Additional penalty for close roll numbers in same program
            }
        } catch (NumberFormatException e) {
            // Ignore if roll numbers are not numeric
        }

        return riskScore;
    }

    /**
     * Create an optimized sequence of students to minimize cheating risk
     */
    private List<Student> createOptimizedSequence(Map<String, List<Student>> studentsByProgram) {
        List<Student> result = new ArrayList<>();
        List<List<Student>> programLists = getLists(studentsByProgram);

        // Shuffle programs to randomize which program gets seated first
        Collections.shuffle(programLists);

        // Round-robin distribution across programs
        int maxSize = programLists.stream().mapToInt(List::size).max().orElse(0);

        for (int i = 0; i < maxSize; i++) {
            for (List<Student> programList : programLists) {
                if (i < programList.size()) {
                    result.add(programList.get(i));
                }
            }
        }

        return result;
    }

    private List<List<Student>> getLists(Map<String, List<Student>> studentsByProgram) {
        List<List<Student>> programLists = new ArrayList<>(studentsByProgram.values());

        // Sort each program list by roll number to avoid friends sitting together
        programLists.forEach(programList ->
                programList.sort((s1, s2) -> {
                    try {
                        int roll1 = Integer.parseInt(extractRollNumber(s1.getStudentId()));
                        int roll2 = Integer.parseInt(extractRollNumber(s2.getStudentId()));
                        return Integer.compare(roll1, roll2);
                    } catch (NumberFormatException e) {
                        return s1.getStudentId().compareTo(s2.getStudentId());
                    }
                })
        );
        return programLists;
    }

    /**
     * Advanced seat allocation with constraint checking
     */
    private void allocateSeatsOptimized(List<Student> students, List<Room> rooms, Exam exam) {
        int studentIndex = 0;

        for (Room room : rooms) {
            int rows = room.getNumRow();
            int cols = room.getNumColumn();
            Student[][] seatGrid = new Student[rows][cols];

            // Fill seats using snake pattern with constraint checking
            for (int r = 0; r < rows && studentIndex < students.size(); r++) {
                if (r % 2 == 0) {
                    // Left to right
                    for (int c = 0; c < cols && studentIndex < students.size(); c++) {
                        Student student = findBestStudentForPosition(
                                students, studentIndex, seatGrid, r, c, room);
                        if (student != null) {
                            assignSeat(student, room, exam, r, c);
                            seatGrid[r][c] = student;
                            studentIndex++;
                        }
                    }
                } else {
                    // Right to left
                    for (int c = cols - 1; c >= 0 && studentIndex < students.size(); c--) {
                        Student student = findBestStudentForPosition(
                                students, studentIndex, seatGrid, r, c, room);
                        if (student != null) {
                            assignSeat(student, room, exam, r, c);
                            seatGrid[r][c] = student;
                            studentIndex++;
                        }
                    }
                }
            }
        }

        if (studentIndex < students.size()) {
            throw new RuntimeException("Not enough seats for all students. Need " +
                    students.size() + " seats, but only " + studentIndex + " available.");
        }
    }

    /**
     * Find the best student for a specific position considering adjacent students
     */
    private Student findBestStudentForPosition(List<Student> remainingStudents,
                                               int startIndex,
                                               Student[][] seatGrid,
                                               int row, int col, Room room) {

        if (startIndex >= remainingStudents.size()) {
            return null;
        }

        Student bestStudent = remainingStudents.get(startIndex);
        double lowestRisk = calculatePositionRisk(bestStudent.getStudentId(), seatGrid, row, col);

        // Check next few students to find better placement (look-ahead of 5)
        int lookAhead = Math.min(5, remainingStudents.size() - startIndex);

        for (int i = 1; i < lookAhead; i++) {
            Student candidate = remainingStudents.get(startIndex + i);
            double candidateRisk = calculatePositionRisk(candidate.getStudentId(), seatGrid, row, col);

            if (candidateRisk < lowestRisk) {
                // Swap students in the list for better placement
                Collections.swap(remainingStudents, startIndex, startIndex + i);
                return candidate;
            }
        }

        return bestStudent;
    }

    /**
     * Calculate risk of placing a student at a specific position
     */
    private double calculatePositionRisk(String studentId, Student[][] seatGrid, int row, int col) {
        double totalRisk = 0.0;
        int adjacentCount = 0;

        // Check all adjacent positions (including diagonals for comprehensive checking)
        int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, 1},  // Top row
                {0, -1},           {0, 1},   // Same row
                {1, -1},  {1, 0},  {1, 1}    // Bottom row
        };

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (newRow >= 0 && newRow < seatGrid.length &&
                    newCol >= 0 && newCol < seatGrid[0].length) {

                Student adjacentStudent = seatGrid[newRow][newCol];
                if (adjacentStudent != null) {
                    double risk = calculateCheatingRisk(studentId, adjacentStudent.getStudentId());
                    totalRisk += risk;
                    adjacentCount++;
                }
            }
        }

        return adjacentCount > 0 ? totalRisk / adjacentCount : 0.0;
    }

    /**
     * Assign seat with additional validation
     */
    private void assignSeat(Student student, Room room, Exam exam, int row, int col) {
        SeatAssignment assignment = new SeatAssignment();
        assignment.setStudent(student);
        assignment.setRoom(room);
        assignment.setExam(exam);
        assignment.setRow(row);
        assignment.setColumn(col);
        assignment.setSeatNumber(row * room.getNumColumn() + col + 1);

        seatAssignmentRepository.save(assignment);
    }

    /**
     * Validate that there are enough seats for all students
     */
    public void validateCapacity(List<Student> students, List<Room> rooms) {
        int totalStudents = students.size();
        int totalSeats = rooms.stream()
                .mapToInt(room -> room.getNumRow() * room.getNumColumn())
                .sum();

        if (totalStudents > totalSeats) {
            throw new IllegalArgumentException(
                    String.format("Not enough seats: %d students, %d seats available",
                            totalStudents, totalSeats));
        }
    }

    /**
     * Get seat assignments by exam ID with sorting
     */
    public List<SeatAssignmentDTO> getSeatPlanByExamId(Integer examId) {
        List<SeatAssignment> assignments = seatAssignmentRepository.findByExamId(examId);

        return assignments.stream()
                .map(sa -> new SeatAssignmentDTO(
                        sa.getStudent().getStudentId(),
                        sa.getRoom().getRoomNo(),
                        sa.getRow(),
                        sa.getColumn()
                ))
                .sorted(Comparator.comparing(SeatAssignmentDTO::roomNo)
                        .thenComparing(SeatAssignmentDTO::row)
                        .thenComparing(SeatAssignmentDTO::column))
                .toList();
    }

    /**
     * Get room-wise seat assignments
     */
    public List<RoomSeatDTO> getSeatAssignmentsByExam(Integer examId) {
        List<SeatAssignment> assignments = seatAssignmentRepository.findByExamId(examId);

        Map<Room, List<SeatAssignment>> groupedByRoom = assignments.stream()
                .collect(Collectors.groupingBy(SeatAssignment::getRoom));

        List<RoomSeatDTO> roomSeatDTOs = new ArrayList<>();

        for (Map.Entry<Room, List<SeatAssignment>> entry : groupedByRoom.entrySet()) {
            Room room = entry.getKey();
            List<SeatAssignment> roomAssignments = entry.getValue();

            List<SeatAssignmentDTO> seatDTOs = roomAssignments.stream()
                    .map(sa -> new SeatAssignmentDTO(
                            sa.getStudent().getStudentId(),
                            room.getRoomNo(),
                            sa.getRow(),
                            sa.getColumn()
                    ))
                    .sorted(Comparator.comparing(SeatAssignmentDTO::row)
                            .thenComparing(SeatAssignmentDTO::column))
                    .toList();

            RoomSeatDTO dto = new RoomSeatDTO(
                    room.getRoomNo(),
                    room.getNumRow(),
                    room.getNumColumn(),
                    seatDTOs
            );

            roomSeatDTOs.add(dto);
        }

        return roomSeatDTOs.stream()
                .sorted(Comparator.comparing(RoomSeatDTO::roomNo))
                .toList();
    }

    /**
     * Generate seating statistics for analysis
     */
    public Map<String, Object> generateSeatingStatistics(Integer examId) {
        List<SeatAssignment> assignments = seatAssignmentRepository.findByExamId(examId);
        Map<String, Object> stats = new HashMap<>();

        // Program distribution
        Map<String, Long> programDistribution = assignments.stream()
                .collect(Collectors.groupingBy(
                        sa -> extractProgramCode(sa.getStudent().getStudentId()),
                        Collectors.counting()
                ));

        // Calculate average cheating risk
        double totalRisk = 0.0;
        int riskCalculations = 0;

        for (SeatAssignment assignment : assignments) {
            List<SeatAssignment> adjacentSeats = findAdjacentSeats(assignment, assignments);
            for (SeatAssignment adjacent : adjacentSeats) {
                totalRisk += calculateCheatingRisk(
                        assignment.getStudent().getStudentId(),
                        adjacent.getStudent().getStudentId()
                );
                riskCalculations++;
            }
        }

        double averageRisk = riskCalculations > 0 ? totalRisk / riskCalculations : 0.0;

        stats.put("totalStudents", assignments.size());
        stats.put("programDistribution", programDistribution);
        stats.put("averageCheatingRisk", averageRisk);
        stats.put("riskLevel", averageRisk > 0.4 ? "HIGH" : averageRisk > 0.2 ? "MEDIUM" : "LOW");

        return stats;
    }

    /**
     * Find adjacent seats for risk calculation
     */
    private List<SeatAssignment> findAdjacentSeats(SeatAssignment target, List<SeatAssignment> allAssignments) {
        return allAssignments.stream()
                .filter(sa -> sa.getRoom().getRoomNo().equals(target.getRoom().getRoomNo()))
                .filter(sa -> !sa.getId().equals(target.getId()))
                .filter(sa -> {
                    int rowDiff = Math.abs(sa.getRow() - target.getRow());
                    int colDiff = Math.abs(sa.getColumn() - target.getColumn());
                    return rowDiff <= 1 && colDiff <= 1 && (rowDiff + colDiff) > 0;
                })
                .collect(Collectors.toList());
    }
}