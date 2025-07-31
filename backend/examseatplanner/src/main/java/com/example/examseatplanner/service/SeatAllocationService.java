package com.example.examseatplanner.service;

import com.example.examseatplanner.dto.RoomSeatDTO;
import com.example.examseatplanner.dto.SeatAssignmentDTO;
import com.example.examseatplanner.model.*;
import com.example.examseatplanner.repository.SeatAssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SeatAllocationService {

    private final SeatAssignmentRepository seatAssignmentRepository;

    @Autowired
    public SeatAllocationService(SeatAssignmentRepository seatAssignmentRepository) {
        this.seatAssignmentRepository = seatAssignmentRepository;
    }

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

    public List<String> readStudentIdsFromExcel(String filePath) {
        List<String> studentIds = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // Get first sheet
            for (Row row : sheet) {
                Cell cell = row.getCell(0); // Assuming student IDs are in first column
                if (cell != null) {
                    switch (cell.getCellType()) {
                        case STRING:
                            studentIds.add(cell.getStringCellValue());
                            break;
                        case NUMERIC:
                            studentIds.add(String.valueOf((int)cell.getNumericCellValue()));
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return studentIds;
    }

    public void validateCapacity(Exam exam) {
        int totalStudents = exam.getStudents().size();
        int totalSeats = exam.getRooms().stream()
                .mapToInt(room -> room.getNumRow() * room.getNumColumn())
                .sum();

        if (totalStudents > totalSeats) {
            throw new IllegalArgumentException(
                    String.format("Not enough seats: %d students, %d seats available",
                            totalStudents, totalSeats));
        }
    }

    public void allocateSeatsStudentId(Exam exam) {
        List<Student> students = new ArrayList<>(exam.getStudents());
        List<Room> rooms = exam.getRooms();

        // Group students by their subjects (assuming one subject per exam)
        Map<Subject, List<Student>> studentsBySubject = students.stream()
                .collect(Collectors.groupingBy(student ->
                        student.getSubjects().stream()
                                .filter(subject -> subject.equals(exam.getSubject()))
                                .findFirst()
                                .orElse(exam.getSubject())
                ));

        // Create alternating sequence to minimize same-subject adjacency
        List<Student> alternatingStudents = createAlternatingSequence(studentsBySubject);

        int studentIndex = 0;

        for (Room room : rooms) {
            int rows = room.getNumRow();
            int cols = room.getNumColumn();

            // Use snake pattern (left-to-right, then right-to-left alternating)
            for (int r = 0; r < rows && studentIndex < alternatingStudents.size(); r++) {
                if (r % 2 == 0) {
                    // Left to right
                    for (int c = 0; c < cols && studentIndex < alternatingStudents.size(); c++) {
                        assignSeat(alternatingStudents.get(studentIndex), room, exam, r, c);
                        studentIndex++;
                    }
                } else {
                    // Right to left
                    for (int c = cols - 1; c >= 0 && studentIndex < alternatingStudents.size(); c--) {
                        assignSeat(alternatingStudents.get(studentIndex), room, exam, r, c);
                        studentIndex++;
                    }
                }
            }
        }

        if (studentIndex < alternatingStudents.size()) {
            throw new RuntimeException("Not enough seats for all students. Need " +
                    alternatingStudents.size() + " seats, but only " + studentIndex + " available.");
        }
    }

    private List<Student> createAlternatingSequence(Map<Subject, List<Student>> studentsBySubject) {
        List<Student> result = new ArrayList<>();
        List<List<Student>> subjectLists = new ArrayList<>(studentsBySubject.values());

        // Shuffle each subject group
        subjectLists.forEach(Collections::shuffle);

        int maxSize = subjectLists.stream().mapToInt(List::size).max().orElse(0);

        // Round-robin through subjects
        for (int i = 0; i < maxSize; i++) {
            for (List<Student> subjectList : subjectLists) {
                if (i < subjectList.size()) {
                    result.add(subjectList.get(i));
                }
            }
        }

        return result;
    }

    private void assignSeat(Student student, Room room, Exam exam, int row, int col) {
        SeatAssignment assignment = new SeatAssignment();
        assignment.setStudent(student);
        assignment.setRoom(room);
        assignment.setExam(exam);
        assignment.setRow(row);
        assignment.setColumn(col);
        assignment.setSeatNumber(row * room.getNumColumn() + col + 1); // Calculate seat number

        seatAssignmentRepository.save(assignment);
    }

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
                    .toList();

            RoomSeatDTO dto = new RoomSeatDTO(
                    room.getRoomNo(),
                    room.getNumRow(),
                    room.getNumColumn(),
                    seatDTOs
            );

            roomSeatDTOs.add(dto);
        }

        return roomSeatDTOs;
    }

//    public void allocateSeatsStudentId(Exam exam) {
//        List<Student> students = new ArrayList<>(exam.getStudents());
//        List<Room> rooms = exam.getRooms();
//        Collections.shuffle(students); // Shuffle to avoid clustering same-subject students
//
//        int studentIndex = 0;
//
//        for (Room room : rooms) {
//            int rows = room.getNumRow();
//            int cols = room.getNumColumn();
//            SeatAssignment[][] grid = new SeatAssignment[rows][cols];
//
//            for (int r = 0; r < rows && studentIndex < students.size(); r++) {
//                for (int c = 0; c < cols && studentIndex < students.size(); c++) {
//
//                    Student student = students.get(studentIndex);
//
//                    // Check for same subject in adjacent seats
//                    if (!isAdjacentSameSubject(grid, r, c, student)) {
//                        SeatAssignment assignment = new SeatAssignment();
//                        assignment.setRoom(room);
//                        assignment.setExam(exam);
//                        assignment.setStudent(student);
//                        assignment.setRow(r);
//                        assignment.setColumn(c);
//
//                        seatAssignmentRepository.save(assignment);
//                        grid[r][c] = assignment;
//
//                        studentIndex++;
//                    }
//                }
//            }
//
//            // Fallback pass: fill remaining seats ignoring adjacency
//            for (int r = 0; r < rows && studentIndex < students.size(); r++) {
//                for (int c = 0; c < cols && studentIndex < students.size(); c++) {
//                    if (grid[r][c] == null) {
//                        Student student = students.get(studentIndex);
//                        SeatAssignment assignment = new SeatAssignment();
//                        assignment.setRoom(room);
//                        assignment.setExam(exam);
//                        assignment.setStudent(student);
//                        assignment.setRow(r);
//                        assignment.setColumn(c);
//
//                        seatAssignmentRepository.save(assignment);
//                        grid[r][c] = assignment;
//                        studentIndex++;
//                    }
//                }
//            }
//        }
//
//        if (studentIndex < students.size()) {
//            throw new RuntimeException("Not enough seats to assign all students.");
//        }
//    }
//
//    private boolean isAdjacentSameSubject(SeatAssignment[][] grid, int r, int c, Student student) {
//        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // up, down, left, right
//        for (int[] d : directions) {
//            int nr = r + d[0];
//            int nc = c + d[1];
//            if (nr >= 0 && nr < grid.length && nc >= 0 && nc < grid[0].length) {
//                SeatAssignment neighbor = grid[nr][nc];
//                if (neighbor != null) {
//                    Student neighborStudent = neighbor.getStudent();
//                    if (hasSameSubject(student, neighborStudent)) {
//                        return true;
//                    }
//                }
//            }
//        }
//        return false;
//    }
//
//    private boolean hasSameSubject(Student s1, Student s2) {
//        // Assuming only one subject per exam; use subjectCode
//        return s1.getSubjects().stream().anyMatch(s2.getSubjects()::contains);
//    }
//
//    public List<SeatAssignmentDTO> getSeatPlanByExamId(Integer examId) {
//        List<SeatAssignment> assignments = seatAssignmentRepository.findByExamId(examId);
//
//        return assignments.stream()
//                .map(sa -> new SeatAssignmentDTO(
//                        sa.getStudent().getStudentId(),
//                        sa.getRoom().getRoomNo(),
//                        sa.getRow(),
//                        sa.getColumn()
//                ))
//                .toList();
//    }
    //for all conditions
//    public List<SeatAssignment> allocateSeats(Exam exam) {
//        List<Student> students = new ArrayList<>(exam.getStudents());
//        List<Room> rooms = exam.getRooms();
//
//        Collections.shuffle(students);
//
//        List<SeatAssignment> assignments = new ArrayList<>();
//        int studentIndex = 0;
//
//        for (Room room : rooms) {
//            int capacity = room.getSeatingCapacity();
//            for (int seatNo = 1; seatNo <= capacity; seatNo++) {
//                if (studentIndex >= students.size()) break;
//
//                Student student = students.get(studentIndex++);
//                SeatAssignment assignment = new SeatAssignment(null, seatNo, room, student, exam);
//                assignments.add(assignment);
//            }
//        }
//
//        return seatAssignmentRepository.saveAll(assignments);
//    }
}
