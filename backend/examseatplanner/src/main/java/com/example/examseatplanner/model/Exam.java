package com.example.examseatplanner.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Table(name = "exam")
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exam_id")
    private Integer id;

//    @Enumerated(EnumType.STRING)
//    @Column(name = "semester")
//    private Student.Semester semester;

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ExamProgramSemester> programSemesters = new ArrayList<>();

    @Column(name = "exam_date")
    private LocalDate date;

    @ManyToMany
    @JoinTable(
            name = "exam_rooms",

            joinColumns = @JoinColumn(name = "exam_id"),
            inverseJoinColumns = @JoinColumn(name = "room_no")
    )
    private List<Room> rooms;

    public Exam(){
    }

    public Exam(Integer id,
                List<ExamProgramSemester> programSemesters,
                LocalDate date,
                List<Room> rooms) {
        this.id = id;
        this.programSemesters = programSemesters;
        this.date = date;
        this.rooms = rooms;
    }


    public List<Program> getPrograms() {
        return programSemesters.stream()
                .map(ExamProgramSemester::getProgram)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Student.Semester> getSemesters() {
        return programSemesters.stream()
                .map(ExamProgramSemester::getSemester)
                .distinct()
                .collect(Collectors.toList());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<ExamProgramSemester> getProgramSemesters() {
        return programSemesters;
    }

    public void setProgramSemesters(List<ExamProgramSemester> programSemesters) {
        this.programSemesters = programSemesters;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Exam exam = (Exam) o;
        return Objects.equals(id, exam.id) && Objects.equals(programSemesters, exam.programSemesters) && Objects.equals(date, exam.date) && Objects.equals(rooms, exam.rooms);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, programSemesters, date, rooms);
    }

    @Override
    public String toString() {
        return "Exam{" +
                "id=" + id +
                ", programSemesters=" + programSemesters +
                ", date=" + date +
                ", rooms=" + rooms +
                '}';
    }
}
