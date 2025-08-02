package com.example.examseatplanner.model;

import jakarta.persistence.*;

@Entity
@Table(name = "seat_assignment")
public class SeatAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "room_no")
    private Room room;

    private Integer seatNumber;

    @ManyToOne
    @JoinColumn(name = "exam_id")  // This should reference exam_id
    private Exam exam;

    @Column(name = "seat_row")  // Using seat_row to avoid reserved keyword
    private int seatRow;

    @Column(name = "seat_column")  // Using seat_column to avoid reserved keyword
    private int seatColumn;

    public SeatAssignment() {
    }

    public SeatAssignment(Integer id, int seatNumber, Room room, Student student, Exam exam, int seatRow, int seatColumn) {
        this.id = id;
        this.seatNumber = seatNumber;
        this.room = room;
        this.student = student;
        this.exam = exam;
        this.seatRow = seatRow;
        this.seatColumn = seatColumn;
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

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public int getRow() {  // Keep these methods for backward compatibility
        return seatRow;
    }

    public void setRow(int row) {
        this.seatRow = row;
    }

    public int getColumn() {
        return seatColumn;
    }

    public void setColumn(int column) {
        this.seatColumn = column;
    }

    public int getSeatRow() {
        return seatRow;
    }

    public void setSeatRow(int seatRow) {
        this.seatRow = seatRow;
    }

    public int getSeatColumn() {
        return seatColumn;
    }

    public void setSeatColumn(int seatColumn) {
        this.seatColumn = seatColumn;
    }
}