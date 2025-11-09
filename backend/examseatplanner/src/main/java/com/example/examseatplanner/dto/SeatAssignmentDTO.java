package com.example.examseatplanner.dto;

public class SeatAssignmentDTO {
    private String programCode;
    private int semester;
    private int roll;

    // optional
    private int row;
    private int column;

    public SeatAssignmentDTO(){}

    public SeatAssignmentDTO(String programCode, int semester, int roll, int row, int column) {
        this.programCode = programCode;
        this.semester = semester;
        this.roll = roll;
        this.row = row;
        this.column = column;
    }

    public String getProgramCode() {
        return programCode;
    }

    public void setProgramCode(String programCode) {
        this.programCode = programCode;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public int getRoll() {
        return roll;
    }

    public void setRoll(int roll) {
        this.roll = roll;
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
