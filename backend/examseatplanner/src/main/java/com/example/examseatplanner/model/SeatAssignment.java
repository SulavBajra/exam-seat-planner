package com.example.examseatplanner.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class SeatAssignment {
    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    private Student student;

    @ManyToOne
    private Room room;

    private Integer seatNumber;

    @ManyToOne
    private Exam exam;

    private int row;
    private int column;

    public SeatAssignment() {
    }

    public SeatAssignment(Integer id, int seatNumber, Room room, Student student,Exam exam,int row,int column) {
        this.id = id;
        this.seatNumber = seatNumber;
        this.room = room;
        this.student = student;
        this.exam = exam;
        this.row = row;
        this.column = column;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }
}

