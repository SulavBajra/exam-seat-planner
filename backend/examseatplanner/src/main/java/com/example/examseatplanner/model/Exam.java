package com.example.examseatplanner.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Entity
@Table(name = "exam")
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exam_id")
    private Integer id;

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ExamProgramSemester> programSemesters = new ArrayList<>();

    @Column(name = "start_date")
    @NotNull(message = "Start date cannot be empty")
    private LocalDate startDate;

    @Column(name = "end_date")
    @NotNull(message = "End date cannot be empty")
    private LocalDate endDate; 

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
                LocalDate startDate,
                LocalDate endDate,
                List<Room> rooms) {
        this.id = id;
        this.programSemesters = programSemesters;
        this.startDate = startDate;
        this.endDate = endDate;
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

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate()
    {
        return this.endDate;
    }

    public void setEndDate(LocalDate endDate){
        this.endDate = endDate;
    }

    public List<ExamProgramSemester> getProgramSemesters() {
        return programSemesters;
    }

    public void setProgramSemesters(List<ExamProgramSemester> programSemesters) {
        this.programSemesters = programSemesters;
    }

    @Override
    public String toString() {
        return "Exam [id=" + id + ", programSemesters=" + programSemesters + ", startDate=" + startDate + ", endDate="
                + endDate + ", rooms=" + rooms + "]";
    }

    
}
