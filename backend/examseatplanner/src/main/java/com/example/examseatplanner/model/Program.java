package com.example.examseatplanner.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Program {
    @Id
    private Integer programCode;

    private String programName;

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

    @Override
    public String toString() {
        return "Program{" +
                "programCode=" + programCode +
                ", programName='" + programName + '\'' +
                '}';
    }
}
