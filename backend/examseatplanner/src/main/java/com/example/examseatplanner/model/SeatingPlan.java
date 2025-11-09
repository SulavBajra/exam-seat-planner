package com.example.examseatplanner.model;

import jakarta.persistence.*;

@Entity
@Table(name = "seating_plan")
public class SeatingPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "exam_id")
    private Exam exam;

    @ManyToOne(optional = false)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(optional = true)
    @JoinColumn(name = "student_id")
    private Student student;

    private Integer rowNumber;
    private Integer columnNumber;

    private String programCode;
    private Integer semester;
    private Integer roll;

    public SeatingPlan() {}

    public SeatingPlan(Exam exam, Room room, Student student, 
                       Integer rowNumber, Integer columnNumber,
                       String programCode, Integer semester, Integer roll) {
        this.exam = exam;
        this.room = room;
        this.student = student;
        this.rowNumber = rowNumber;
        this.columnNumber = columnNumber;
        this.programCode = programCode;
        this.semester = semester;
        this.roll = roll;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }

    public Integer getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(Integer columnNumber) {
        this.columnNumber = columnNumber;
    }

    public String getProgramCode() {
        return programCode;
    }

    public void setProgramCode(String programCode) {
        this.programCode = programCode;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public Integer getRoll() {
        return roll;
    }

    public void setRoll(Integer roll) {
        this.roll = roll;
    }

    @Override
    public String toString() {
        return "SeatingPlan [id=" + id + ", exam=" + exam + ", room=" + room + ", student=" + student + ", rowNumber="
                + rowNumber + ", columnNumber=" + columnNumber + ", programCode=" + programCode + ", semester="
                + semester + ", roll=" + roll + ", getId()=" + getId() + ", getExam()=" + getExam() + ", getRoom()="
                + getRoom() + ", getStudent()=" + getStudent() + ", getRowNumber()=" + getRowNumber()
                + ", getColumnNumber()=" + getColumnNumber() + ", getProgramCode()=" + getProgramCode()
                + ", getSemester()=" + getSemester() + ", getRoll()=" + getRoll() + ", getClass()=" + getClass()
                + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
    }

    
}
