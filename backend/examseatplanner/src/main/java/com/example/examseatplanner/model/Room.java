package com.example.examseatplanner.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Room {
    @Id
    private Integer roomNo;

    private int seatingCapacity;
    private int numRow;
    private int numColumn;

    public Room(Integer roomNo, int numColumn, int numRow, int seatingCapacity) {
        this.roomNo = roomNo;
        this.numColumn = numColumn;
        this.numRow = numRow;
        this.seatingCapacity = seatingCapacity;
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
