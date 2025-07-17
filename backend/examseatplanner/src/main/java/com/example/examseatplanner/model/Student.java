package com.example.examseatplanner.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "student")
public class Student {
    @Id
    @Column(name = "student_id", unique = true)
    private String studentId;

    private int roll;
    private int semester;
    private String enrolledYear;

    @ManyToOne
    @JoinColumn(name="program_code")
    private Program program;

    @ManyToMany
    @JoinTable(
            name = "student_subjects",
            joinColumns = @JoinColumn(name = "student_student_id", referencedColumnName = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "subjects_subject_code", referencedColumnName = "subject_code")
    )
    private List<Subject> subjects;

    public Student() {}

    public Student(List<Subject> subjects, Program program, String enrolledYear, int semester, int roll) {
        this.subjects = subjects;
        this.program = program;
        this.enrolledYear = enrolledYear;
        this.semester = semester;
        this.roll = roll;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public int getRoll() {
        return roll;
    }

    public void setRoll(int roll) {
        this.roll = roll;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    public String getEnrolledYear() {
        return enrolledYear;
    }

    public void setEnrolledYear(String enrolledYear) {
        this.enrolledYear = enrolledYear;
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentId='" + studentId + '\'' +
                ", roll=" + roll +
                ", semester=" + semester +
                ", enrolledYear='" + enrolledYear + '\'' +
                ", program=" + program +
                ", subjects=" + subjects +
                '}';
    }
}

