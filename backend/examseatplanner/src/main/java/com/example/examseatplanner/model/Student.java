package com.example.examseatplanner.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id", unique = true, nullable = false)
    private Integer studentId;

    private int roll;


    @ManyToOne
    @JoinColumn(name = "program_code")
    private Program program;

    @Enumerated(EnumType.STRING)
    private Semester semester;

    public enum Semester {
        FIRST, SECOND, THIRD, FOURTH, FIFTH, SIXTH, SEVENTH, EIGHTH
    }

    public Student() {}

    public Student(Program program,Semester semester, int roll) {
        this.program = program;
        this.semester = semester;
        this.roll = roll;
    }


    public Integer getStudentId() {
        return studentId;
    }

    public int getRoll() {
        return roll;
    }

    public void setRoll(int roll) {
        this.roll = roll;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student other = (Student) o;
        return Objects.equals(studentId, other.studentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId);
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentId=" + studentId +
                ", roll=" + roll +
                ", program=" + program +
                ", semester=" + semester +
                '}';
    }
}
