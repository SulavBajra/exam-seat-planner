package com.example.examseatplanner.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Room {
    @Id
    private Long roomNo;

    private int seatingCapacity;
    private int numRow;
    private int numColumn;

    public Room(Long roomNo, int numColumn, int numRow, int seatingCapacity) {
        this.roomNo = roomNo;
        this.numColumn = numColumn;
        this.numRow = numRow;
        this.seatingCapacity = seatingCapacity;
    }

    public Long getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(Long roomNo) {
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

    public int getSeatingCapacity() {
        return seatingCapacity;
    }

    public void setSeatingCapacity(int seatingCapacity) {
        this.seatingCapacity = seatingCapacity;
    }

    @Override
    public String toString() {
        return "Room{" +
                "roomNo=" + roomNo +
                ", seatingCapacity=" + seatingCapacity +
                ", numRow=" + numRow +
                ", numColumn=" + numColumn +
                '}';
    }
}
