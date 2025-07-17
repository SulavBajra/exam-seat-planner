package com.example.examseatplanner.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Subject {
    @Id
    @Column(name="subject_code")
    private Integer subjectCode;

    private String subjectName;
    private int duration;
    private String timeSlot;

    public Subject() {
    }

    public Subject(Integer subjectCode, String subjectName, int duration, String timeSlot) {
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.duration = duration;
        this.timeSlot = timeSlot;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "subjectCode=" + subjectCode +
                ", subjectName='" + subjectName + '\'' +
                ", duration=" + duration +
                ", timeSlot='" + timeSlot + '\'' +
                '}';
    }
}
