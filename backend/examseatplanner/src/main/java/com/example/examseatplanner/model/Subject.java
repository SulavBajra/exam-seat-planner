package com.example.examseatplanner.model;

import jakarta.persistence.*;

@Entity
public class Subject {

    @Id
    @Column(name = "subject_code")
    private Integer subjectCode;

    private String subjectName;
    private int semester;

    @ManyToOne
    @JoinColumn(name = "program_code")
    private Program program;

    public Subject() {
    }

    public Subject(Integer subjectCode, String subjectName, int semester) {
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.semester = semester;
    }

    public Integer getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(Integer subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
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

    @Override
    public String toString() {
        return "Subject{" +
                "subjectCode=" + subjectCode +
                ", subjectName='" + subjectName + '\'' +
                ", semester=" + semester +
                ", program=" + (program != null ? program.getProgramName() : "null") +
                '}';
    }
}