package com.example.examseatplanner.model;

import jakarta.persistence.*;

@Entity
public class Program {

    @Id
    private Integer programCode;

    private String programName;


    // Constructors
    public Program() {}

    public Program(String programName, Integer programCode) {
        this.programName = programName;
        this.programCode = programCode;
    }

    // Getters and Setters
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

    @Override
    public String toString() {
        return "Program{" +
                "programCode=" + programCode +
                ", programName='" + programName + '\'' +
                '}';
    }
}
