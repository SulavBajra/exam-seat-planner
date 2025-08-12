package com.example.examseatplanner.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rowNumber;       // row index (0-based)
    private int benchNumber;     // column index (0-based, 0 to 2)
    private int seatSide;      // 0 = Left, 1 = Middle, 2 = Right
    private int seatPosition;  // 0 or 1 (per side)

    @ManyToOne
    @JoinColumn(name = "room_no")
    @JsonBackReference
    private Room room;


    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student assignedStudent;

    public Seat() {}

    public Seat(int rowNumber, int benchNumber, int seatPosition, Room room, Student assignedStudent) {
        this.rowNumber = rowNumber;
        this.benchNumber = benchNumber;
        this.seatPosition = seatPosition;
        this.room = room;
        this.assignedStudent = assignedStudent;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public int getBenchNumber() {
        return benchNumber;
    }

    public void setBenchNumber(int benchNumber) {
        this.benchNumber = benchNumber;
    }

    public int getSeatPosition() {
        return seatPosition;
    }

    public void setSeatPosition(int seatPosition) {
        this.seatPosition = seatPosition;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Student getAssignedStudent() {
        return assignedStudent;
    }

    public void setAssignedStudent(Student assignedStudent) {
        this.assignedStudent = assignedStudent;
    }

    public int getSeatSide() {
        return seatSide;
    }

    public void setSeatSide(int seatSide) {
        this.seatSide = seatSide;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
