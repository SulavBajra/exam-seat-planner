//package com.example.examseatplanner.service;
//
//import com.example.examseatplanner.model.*;
//import com.example.examseatplanner.repository.SeatRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//
//@Service
//public class SeatAllocationService {
//
//    private final SeatRepository seatRepository;
//
//    @Autowired
//    public SeatAllocationService(SeatRepository seatRepository) {
//        this.seatRepository = seatRepository;
//    }
//
//    public void allocateSeats(Room room, Map<Program, List<Student>> programStudentMap) {
//        int numRows = room.getNumRow();        // e.g., 2 rows
//        int numColumns = room.getNumColumn();  // e.g., 3 benches
//
//        List<Program> programs = programStudentMap.keySet().stream().toList();
//
//        // Program 0 → Left (seatSide=0), 1 → Middle (1), 2 → Right (2)
//        for (int programIndex = 0; programIndex < Math.min(programs.size(), 3); programIndex++) {
//            Program program = programs.get(programIndex);
//            List<Student> students = programStudentMap.get(program);
//
//            int seatSide = programIndex; // 0 = Left, 1 = Middle, 2 = Right
//
//            int studentIndex = 0;
//
//            // Iterate over benches and rows to assign students sequentially
//            outerLoop:
//            for (int bench = 0; bench < numColumns; bench++) {
//                for (int row = 0; row < numRows; row++) {
//                    if (studentIndex >= students.size()) {
//                        break outerLoop;  // All students assigned
//                    }
//
//                    Student student = students.get(studentIndex++);
//
//                    Seat seat = new Seat();
//                    seat.setRoom(room);
//                    seat.setBenchNumber(bench);     // bench number (0-based)
//                    seat.setRowNumber(row);          // row number (0-based)
//                    seat.setSeatSide(seatSide);      // 0=Left,1=Middle,2=Right
//                    seat.setSeatPosition(row);       // 0 or 1 (row index)
//                    seat.setAssignedStudent(student);
//
//                    seatRepository.save(seat);
//                }
//            }
//
//            // If some students remain unassigned
//            if (studentIndex < students.size()) {
//                throw new RuntimeException("Not enough seats to allocate all students of program " + program.getProgramName());
//            }
//        }
//    }
//
//}
