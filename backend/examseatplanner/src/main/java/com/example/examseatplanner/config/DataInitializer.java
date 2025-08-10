package com.example.examseatplanner.config;

import com.example.examseatplanner.model.*;
import com.example.examseatplanner.repository.*;
import com.example.examseatplanner.service.SeatAllocationService;
import com.example.examseatplanner.service.SeatAllocationDTOService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoomRepository roomRepository;
    private final ProgramRepository programRepository;
    private final StudentRepository studentRepository;
    private final ExamRepository examRepository;
    private final SeatAllocationService seatAllocationService;
    private final SeatAllocationDTOService dtoService;
    private final SeatRepository seatRepository;

    public DataInitializer(
            RoomRepository roomRepository,
            ProgramRepository programRepository,
            StudentRepository studentRepository,
            ExamRepository examRepository,
            SeatAllocationService seatAllocationService,
            SeatAllocationDTOService dtoService,
            SeatRepository seatRepository) {
        this.roomRepository = roomRepository;
        this.programRepository = programRepository;
        this.studentRepository = studentRepository;
        this.examRepository = examRepository;
        this.seatAllocationService = seatAllocationService;
        this.dtoService = dtoService;
        this.seatRepository = seatRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🚀 Starting Data Initialization with 3D Seat Allocation...");

        // 1. Create Rooms and explain capacity
        Room room101 = createRoom(101, 2, 3); // 2 rows, 3 columns
        Room room102 = createRoom(102, 3, 2); // 3 rows, 2 columns
        Room room103 = createRoom(103, 1, 4); // 1 row, 4 columns

        List<Room> rooms = roomRepository.saveAll(List.of(room101, room102, room103));

        System.out.println("🏢 Room Capacity Breakdown:");
        for (Room room : rooms) {
            int baseCapacity = room.getSeatingCapacity(); // rows * columns * 2 seats per bench
            int total3DCapacity = baseCapacity * 3; // 3 sides (Left, Middle, Right)
            System.out.printf("   Room %d: %d rows × %d cols × 2 seats × 3 sides = %d total seats%n",
                    room.getRoomNo(), room.getNumRow(), room.getNumColumn(), total3DCapacity);
        }

        int totalSystemCapacity = rooms.stream()
                .mapToInt(room -> room.getSeatingCapacity() * 3)
                .sum();
        System.out.printf("✅ Created %d rooms with total 3D capacity: %d seats%n",
                rooms.size(), totalSystemCapacity);

        // 2. Create Programs
        Program bim = new Program("Bachelor in Information Management", 1001);
        Program bba = new Program("Bachelor in Business Administration", 1002);
        Program bscs = new Program("Bachelor in Computer Science", 1003);
        Program bit = new Program("Bachelor in Information Technology", 1004);

        List<Program> programs = programRepository.saveAll(List.of(bim, bba, bscs, bit));
        System.out.printf("✅ Created %d programs%n", programs.size());

        // 3. Create Students (reduce numbers to fit capacity)
        List<Student> allStudents = new ArrayList<>();

        // BIM students (6 students)
        allStudents.addAll(createStudents(bim, 6, Student.Semester.THIRD));

        // BBA students (5 students)
        allStudents.addAll(createStudents(bba, 5, Student.Semester.THIRD));

        // BSCS students (4 students)
        allStudents.addAll(createStudents(bscs, 4, Student.Semester.THIRD));

        // BIT students (3 students)
        allStudents.addAll(createStudents(bit, 3, Student.Semester.THIRD));

        studentRepository.saveAll(allStudents);
        System.out.printf("✅ Created %d students across all programs%n", allStudents.size());

        // Calculate and display capacity clearly
        int selectedRoomCapacity = room101.getSeatingCapacity() * 3; // 3 sides
        System.out.printf("📊 Selected room 3D capacity: %d seats (base %d × 3 sides)%n",
                selectedRoomCapacity, room101.getSeatingCapacity());
        System.out.printf("👥 Students to allocate: %d%n", allStudents.size());

        if (allStudents.size() > selectedRoomCapacity) {
            System.out.printf("⚠️  Need to reduce students or add more rooms!%n");
            System.out.printf("   Capacity needed: %d, Available: %d%n", allStudents.size(), selectedRoomCapacity);
        }

        // 4. Create Exam
        Exam exam = new Exam();
        exam.setDate(LocalDate.now().plusDays(7));
        exam.setPrograms(programs);
        exam.setRooms(Arrays.asList(room101)); // Use just room101 for this small exam

        exam = examRepository.save(exam);
        System.out.printf("✅ Created exam for date: %s%n", exam.getDate());
        System.out.printf("🏢 Using room %d with capacity: %d × 3 sides = %d total seats%n",
                room101.getRoomNo(), room101.getSeatingCapacity(), room101.getSeatingCapacity() * 3);

        // 5. Perform 3D Seat Allocation
        try {
            System.out.println("\n🎯 Starting 3D Seat Allocation...");

            SeatAllocationService.SeatAllocationResult result =
                    seatAllocationService.allocateSeatsForExam(exam);

            System.out.printf("✅ Allocated %d students to seats%n", result.getAssignments().size());
            System.out.printf("📊 Remaining capacity: %d seats%n", result.getRemainingCapacity());

            // 6. Display Results
            displayAllocationResults(exam);

            // 7. Validate Allocation
            validateAndDisplayResults(exam);

        } catch (RuntimeException e) {
            System.err.println("❌ Seat allocation failed: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n🎉 Data initialization completed successfully!");
    }

    private Room createRoom(Integer roomNo, int numRows, int numColumns) {
        Room room = new Room();
        room.setRoomNo(roomNo);
        room.setNumRow(numRows);
        room.setNumColumn(numColumns);
        return room;
    }

    private List<Student> createStudents(Program program, int count, Student.Semester semester) {
        List<Student> students = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Student student = new Student();
            student.setProgram(program);
            student.setRoll(i);
            student.setSemester(semester);
            students.add(student);
        }
        return students;
    }

    private void displayAllocationResults(Exam exam) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("📋 SEAT ALLOCATION RESULTS");
        System.out.println("=".repeat(60));

        Map<Room, Seat[][][]> seatingChart = seatAllocationService.getSeatingChart(exam);

        for (Map.Entry<Room, Seat[][][]> entry : seatingChart.entrySet()) {
            Room room = entry.getKey();
            Seat[][][] seats = entry.getValue();

            System.out.printf("\n🏢 ROOM %d Layout (%d rows × %d columns)%n",
                    room.getRoomNo(), room.getNumRow(), room.getNumColumn());
            System.out.println("-".repeat(50));

            displayRoomLayout(room, seats);
        }
    }

    private void displayRoomLayout(Room room, Seat[][][] seats) {
        String[] sideNames = {"LEFT", "MIDDLE", "RIGHT"};

        for (int side = 0; side < 3; side++) {
            System.out.printf("\n%s SECTION:%n", sideNames[side]);
            System.out.println("┌" + "─".repeat(48) + "┐");

            for (int row = 0; row < room.getNumRow(); row++) {
                System.out.printf("│ Row %d: ", row + 1);

                for (int bench = 0; bench < room.getNumColumn(); bench++) {
                    System.out.print("[");

                    for (int position = 0; position < Room.SEATS_PER_BENCH; position++) {
                        int seatIndex = bench * Room.SEATS_PER_BENCH + position;
                        Seat seat = seats[side][row][seatIndex];

                        if (seat != null && seat.getAssignedStudent() != null) {
                            Student student = seat.getAssignedStudent();
                            System.out.printf("%s%02d",
                                    getProgramAbbreviation(student.getProgram().getProgramName()),
                                    student.getRoll());
                        } else {
                            System.out.print("----");
                        }

                        if (position < Room.SEATS_PER_BENCH - 1) {
                            System.out.print("|");
                        }
                    }

                    System.out.print("] ");
                }

                System.out.printf("%n");
            }

            System.out.println("└" + "─".repeat(48) + "┘");
        }
    }

    private String getProgramAbbreviation(String programName) {
        if (programName.contains("Information Management")) return "BIM";
        if (programName.contains("Business Administration")) return "BBA";
        if (programName.contains("Computer Science")) return "BSC";
        if (programName.contains("Information Technology")) return "BIT";
        return "UNK";
    }

    private void validateAndDisplayResults(Exam exam) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("✅ VALIDATION RESULTS");
        System.out.println("=".repeat(60));

        List<String> violations = seatAllocationService.validateSeatAllocation(exam);

        if (violations.isEmpty()) {
            System.out.println("🎉 PERFECT! No rule violations found.");
            System.out.println("✅ Students from different programs are properly separated.");
        } else {
            System.out.printf("⚠️  Found %d violations:%n", violations.size());
            violations.forEach(violation -> System.out.println("  ❌ " + violation));
        }

        // Display statistics
        Map<Room, Seat[][][]> seatingChart = seatAllocationService.getSeatingChart(exam);
        Map<String, Object> statistics = dtoService.generateAllocationStatistics(seatingChart, violations);

        System.out.println("\n📊 ALLOCATION STATISTICS:");
        System.out.println("-".repeat(30));
        statistics.forEach((key, value) -> {
            String displayKey = formatStatKey(key);
            if (value instanceof Double) {
                System.out.printf("%-20s: %.2f%n", displayKey, value);
            } else {
                System.out.printf("%-20s: %s%n", displayKey, value);
            }
        });
    }

    private String formatStatKey(String key) {
        return switch (key) {
            case "totalRooms" -> "Total Rooms";
            case "totalSeats" -> "Total Seats";
            case "occupiedSeats" -> "Occupied Seats";
            case "availableSeats" -> "Available Seats";
            case "occupancyRate" -> "Occupancy Rate (%)";
            case "programDistribution" -> "Program Distribution";
            case "violationCount" -> "Violations";
            case "isValidAllocation" -> "Valid Allocation";
            default -> key;
        };
    }

    // Helper method to display program distribution
    private void displayProgramDistribution(Map<Integer, Integer> distribution) {
        System.out.println("\n👥 PROGRAM DISTRIBUTION:");
        distribution.forEach((programCode, count) -> {
            System.out.printf("  Program %d: %d students%n", programCode, count);
        });
    }
}