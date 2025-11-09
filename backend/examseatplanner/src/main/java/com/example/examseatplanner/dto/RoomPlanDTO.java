package com.example.examseatplanner.dto;

import java.util.List;

public class RoomPlanDTO {
    private String roomNo;
    private List<List<SeatAssignmentDTO>> seats; 

    public RoomPlanDTO(String roomNo, List<List<SeatAssignmentDTO>> seats) {
        this.roomNo = roomNo;
        this.seats = seats;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public List<List<SeatAssignmentDTO>> getSeats() {
        return seats;
    }

    public void setSeats(List<List<SeatAssignmentDTO>> seats) {
        this.seats = seats;
    }


}
