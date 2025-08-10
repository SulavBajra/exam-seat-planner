package com.example.examseatplanner.service;

import com.example.examseatplanner.dto.*;
import com.example.examseatplanner.model.Room;
import com.example.examseatplanner.model.Seat;
import com.example.examseatplanner.model.Student;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SeatAllocationDTOService {
    public SeatAllocationResponseDTO convertToDTO(
            SeatAllocationService.SeatAllocationResult result,
            Map<Room, Seat[][][]> seatingChart) {

        List<RoomSeatingDTO> roomSeating = new ArrayList<>();
        List<String> allocationNotes = new ArrayList<>();

        for (Map.Entry<Room, Seat[][][]> entry : seatingChart.entrySet()) {
            Room room = entry.getKey();
            Seat[][][] seats = entry.getValue();

            RoomSeatingDTO roomDTO = convertRoomSeatingToDTO(room, seats);
            roomSeating.add(roomDTO);

            // Add room-specific notes
            int occupiedSeats = countOccupiedSeats(seats, room);
            allocationNotes.add(String.format("Room %d: %d/%d seats occupied",
                    room.getRoomNo(), occupiedSeats, room.getSeatingCapacity()));
        }

        return new SeatAllocationResponseDTO(
                result.getExam().getId(),
                result.getExam().getDate().toString(),
                result.getTotalStudents(),
                result.getTotalStudents() + result.getRemainingCapacity(),
                result.getRemainingCapacity(),
                roomSeating,
                allocationNotes
        );
    }

    /**
     * Converts room seating to DTO
     */
    private RoomSeatingDTO convertRoomSeatingToDTO(Room room, Seat[][][] seats) {
        List<SideSeatingDTO> sides = new ArrayList<>();
        String[] sideNames = {"Left", "Middle", "Right"};

        for (int sideIndex = 0; sideIndex < 3; sideIndex++) {
            List<RowSeatingDTO> rows = new ArrayList<>();

            for (int rowIndex = 0; rowIndex < room.getNumRow(); rowIndex++) {
                List<SeatDTO> rowSeats = new ArrayList<>();

                for (int seatIndex = 0; seatIndex < room.getNumColumn() * Room.SEATS_PER_BENCH; seatIndex++) {
                    Seat seat = seats[sideIndex][rowIndex][seatIndex];
                    SeatDTO seatDTO = convertSeatToDTO(seat);
                    rowSeats.add(seatDTO);
                }

                rows.add(new RowSeatingDTO(rowIndex, rowSeats));
            }

            sides.add(new SideSeatingDTO(sideIndex, sideNames[sideIndex], rows));
        }

        return new RoomSeatingDTO(
                room.getRoomNo(),
                room.getNumRow(),
                room.getNumColumn(),
                room.getSeatingCapacity(),
                sides
        );
    }

    /**
     * Converts seat to DTO
     */
    /**
     * Converts seat to DTO
     */
    private SeatDTO convertSeatToDTO(Seat seat) {
        if (seat == null) {
            return new SeatDTO(null, 0, 0, null);
        }

        StudentSummaryDTO studentDTO = null;
        if (seat.getAssignedStudent() != null) {
            Student student = seat.getAssignedStudent();
            studentDTO = new StudentSummaryDTO(
                    student.getStudentId(),
                    student.getRoll(),
                    student.getProgram().getProgramCode(),
                    student.getProgram().getProgramName(),
                    student.getSemester().toString()  // Only 4 parameters
            );
        }

        return new SeatDTO(
                seat.getId(),
                seat.getBenchNumber(),
                seat.getSeatPosition(),
                studentDTO
        );
    }

    /**
     * Counts occupied seats in a 3D array
     */
    private int countOccupiedSeats(Seat[][][] seats, Room room) {
        int count = 0;
        for (int side = 0; side < 3; side++) {
            for (int row = 0; row < room.getNumRow(); row++) {
                for (int seatIndex = 0; seatIndex < room.getNumColumn() * Room.SEATS_PER_BENCH; seatIndex++) {
                    if (seats[side][row][seatIndex] != null &&
                            seats[side][row][seatIndex].getAssignedStudent() != null) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    /**
     * Generates a visual representation of the seating arrangement
     */
    public String generateSeatingVisualization(Room room, Seat[][][] seats) {
        StringBuilder viz = new StringBuilder();
        viz.append(String.format("Room %d Seating Layout\n", room.getRoomNo()));
        viz.append("=".repeat(50)).append("\n\n");

        String[] sideNames = {"LEFT", "MIDDLE", "RIGHT"};

        for (int side = 0; side < 3; side++) {
            viz.append(String.format("%s SECTION:\n", sideNames[side]));
            viz.append("-".repeat(30)).append("\n");

            for (int row = 0; row < room.getNumRow(); row++) {
                viz.append(String.format("Row %d: ", row + 1));

                for (int bench = 0; bench < room.getNumColumn(); bench++) {
                    viz.append("[");

                    for (int position = 0; position < Room.SEATS_PER_BENCH; position++) {
                        int seatIndex = bench * Room.SEATS_PER_BENCH + position;
                        Seat seat = seats[side][row][seatIndex];

                        if (seat != null && seat.getAssignedStudent() != null) {
                            Student student = seat.getAssignedStudent();
                            viz.append(String.format("P%d-R%d",
                                    student.getProgram().getProgramCode(),
                                    student.getRoll()));
                        } else {
                            viz.append("Empty");
                        }

                        if (position < Room.SEATS_PER_BENCH - 1) {
                            viz.append("|");
                        }
                    }

                    viz.append("] ");
                }

                viz.append("\n");
            }

            viz.append("\n");
        }

        return viz.toString();
    }

    /**
     * Generates statistics for seat allocation
     */
    public Map<String, Object> generateAllocationStatistics(
            Map<Room, Seat[][][]> seatingChart,
            List<String> violations) {

        int totalRooms = seatingChart.size();
        int totalSeats = 0;
        int occupiedSeats = 0;
        Map<Integer, Integer> programDistribution = new HashMap<>();

        for (Map.Entry<Room, Seat[][][]> entry : seatingChart.entrySet()) {
            Room room = entry.getKey();
            Seat[][][] seats = entry.getValue();

            totalSeats += room.getSeatingCapacity();

            for (int side = 0; side < 3; side++) {
                for (int row = 0; row < room.getNumRow(); row++) {
                    for (int seatIndex = 0; seatIndex < room.getNumColumn() * Room.SEATS_PER_BENCH; seatIndex++) {
                        Seat seat = seats[side][row][seatIndex];
                        if (seat != null && seat.getAssignedStudent() != null) {
                            occupiedSeats++;
                            Integer programCode = seat.getAssignedStudent().getProgram().getProgramCode();
                            programDistribution.put(programCode,
                                    programDistribution.getOrDefault(programCode, 0) + 1);
                        }
                    }
                }
            }
        }

        return Map.of(
                "totalRooms", totalRooms,
                "totalSeats", totalSeats,
                "occupiedSeats", occupiedSeats,
                "availableSeats", totalSeats - occupiedSeats,
                "occupancyRate", totalSeats > 0 ? (double) occupiedSeats / totalSeats * 100 : 0,
                "programDistribution", programDistribution,
                "violationCount", violations.size(),
                "isValidAllocation", violations.isEmpty()
        );
    }
}