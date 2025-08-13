package com.example.examseatplanner.dto;

import java.util.List;

public class RoomSeatingDTO {
    private Integer roomNo;
    private int numRow;
    private int numColumn;
    private int seatingCapacity;
    private List<SideSeatingDTO> sides;

    public RoomSeatingDTO(Integer roomNo, int numRow, int numColumn, int seatingCapacity, List<SideSeatingDTO> sides) {
        this.roomNo = roomNo;
        this.numRow = numRow;
        this.numColumn = numColumn;
        this.seatingCapacity = seatingCapacity;
        this.sides = sides;
    }

    public Integer getRoomNo() {
        return roomNo;
    }

    public int getNumRow() {
        return numRow;
    }

    public int getNumColumn() {
        return numColumn;
    }

    public int getSeatingCapacity() {
        return seatingCapacity;
    }

    public List<SideSeatingDTO> getSides() {
        return sides;
    }
}
