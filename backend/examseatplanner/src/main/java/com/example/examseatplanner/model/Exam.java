package com.example.examseatplanner.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Subject subject;
    private String date;
    private String time;

    @ManyToMany
    private List<Student> students;

    @ManyToMany
    private List<Room> rooms;

    public Exam() {
    }

    public Exam(Long id, List<Room> rooms, List<Student> students, String time, String date, Subject subject) {
        this.id = id;
        this.rooms = rooms;
        this.students = students;
        this.time = time;
        this.date = date;
        this.subject = subject;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return "Exam{" +
                "id=" + id +
                ", subject=" + subject +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", students=" + students +
                ", rooms=" + rooms +
                '}';
    }
}
