package com.example.examseatplanner.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Room {
    @Id
    private Integer roomNo;

    private int numRow;
    private int numColumn;

    // Number of seats per bench (fixed to 2 for your design)
    public static final int SEATS_PER_BENCH = 2;

    public Room() {
    }

    public Room(Integer roomNo, int numColumn, int numRow) {
        this.roomNo = roomNo;
        this.numColumn = numColumn;
        this.numRow = numRow;
    }

    public Integer getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(Integer roomNo) {
        this.roomNo = roomNo;
    }

    public int getNumColumn() {
        return numColumn;
    }

    public void setNumColumn(int numColumn) {
        this.numColumn = numColumn;
    }

    public int getNumRow() {
        return numRow;
    }

    public void setNumRow(int numRow) {
        this.numRow = numRow;
    }

    // Dynamically calculated seating capacity based on rows, columns, and seats per bench
    public int getSeatingCapacity() {
        return numRow * numColumn * SEATS_PER_BENCH;
    }

    @Override
    public String toString() {
        return "Room{" +
                "roomNo=" + roomNo +
                ", numRow=" + numRow +
                ", numColumn=" + numColumn +
                ", seatingCapacity=" + getSeatingCapacity() +
                '}';
    }
}
