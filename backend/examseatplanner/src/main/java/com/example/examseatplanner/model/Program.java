package com.example.examseatplanner.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Program {

    @Id
    private Integer programCode;

    private String programName;

    @OneToMany
    @JoinColumn(name = "program_code", referencedColumnName = "programCode")
    private List<Subject> subjects;

    public Program() {
    }

    public Program(String programName, Integer programCode) {
        this.programName = programName;
        this.programCode = programCode;
    }

    public Integer getProgramCode() {
        return programCode;
    }

    public void setProgramCode(Integer programCode) {
        this.programCode = programCode;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    @Override
    public String toString() {
        return "Program{" +
                "programCode=" + programCode +
                ", programName='" + programName + '\'' +
                '}';
    }
}
