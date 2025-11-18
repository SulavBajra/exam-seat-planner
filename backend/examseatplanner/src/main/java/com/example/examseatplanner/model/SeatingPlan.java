package com.example.examseatplanner.model;


import jakarta.persistence.*;

@Entity
@Table(name = "seating_plan")
public class SeatingPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "exam_id")
    private Integer examId;

    @Column(name = "room_id")
    private String roomNo;

    private Integer rowNumber;
    private Integer columnNumber;
    private Integer seatIndex;
    private String programCode;
    private Integer semester;
    private Integer roll;

    public SeatingPlan() {}

    public SeatingPlan(Integer id, Integer examId, String roomNo, Integer rowNumber, Integer columnNumber,
            String programCode, Integer semester, Integer roll,Integer seatIndex) {
        this.id = id;
        this.examId = examId;
        this.roomNo = roomNo;
        this.rowNumber = rowNumber;
        this.columnNumber = columnNumber;
        this.programCode = programCode;
        this.semester = semester;
        this.roll = roll;
        this.seatIndex = seatIndex;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getExamId() {
        return examId;
    }

    public void setExamId(Integer examId) {
        this.examId = examId;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
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

    public Integer getSeatIndex(){
        return seatIndex;
    }

    public void setSeatIndex(Integer seatIndex){
        this.seatIndex = seatIndex;
    }

    @Override
    public String toString() {
        return "SeatingPlan [id=" + id + ", exam=" + examId + ", room=" + roomNo + ", rowNumber=" + rowNumber
                + ", columnNumber=" + columnNumber + ", programCode=" + programCode + ", semester=" + semester
                + ", roll=" + roll + ", seatIndex="+ seatIndex+"]";
    }
}
