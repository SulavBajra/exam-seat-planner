package com.example.examseatplanner.dto;

public class SeatAssignmentDTO{
    private String programCode;
    private Integer semester;
    private Integer roll;
    private Integer rowNumber;
    private Integer columnNumber;
    private String roomNo;
    private Integer examId;

    public SeatAssignmentDTO(){}

    public SeatAssignmentDTO(String programCode, Integer semester, Integer roll, Integer rowNumber,
            Integer columnNumber, String roomNo) {
        this.programCode = programCode;
        this.semester = semester;
        this.roll = roll;
        this.rowNumber = rowNumber;
        this.columnNumber = columnNumber;
        this.roomNo = roomNo;
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
    public String getRoomNo() {
        return roomNo;
    }
    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }
    public Integer getExamId() { return examId; }
    public void setExamId(Integer examId) { this.examId = examId; }
}
