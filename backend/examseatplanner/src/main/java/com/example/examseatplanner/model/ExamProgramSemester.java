package com.example.examseatplanner.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "exam_program_semesters")
public class ExamProgramSemester {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "exam_id")
    @JsonBackReference
    private Exam exam;


    @ManyToOne
    @JoinColumn(name = "program_code", nullable = false)
    private Program program;

    @Enumerated(EnumType.STRING)
    @Column(name = "semester", nullable = false)
    private Student.Semester semester;

    public ExamProgramSemester() {}

    public ExamProgramSemester(Exam exam, Program program, Student.Semester semester) {
        this.exam = exam;
        this.program = program;
        this.semester = semester;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student.Semester getSemester() {
        return semester;
    }

    public void setSemester(Student.Semester semester) {
        this.semester = semester;
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ExamProgramSemester that = (ExamProgramSemester) o;
        return Objects.equals(id, that.id) && Objects.equals(exam, that.exam) && Objects.equals(program, that.program) && semester == that.semester;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, exam, program, semester);
    }

    @Override
    public String toString() {
        return "ExamProgramSemester{" +
                "id=" + id +
                ", exam=" + exam +
                ", program=" + program +
                ", semester=" + semester +
                '}';
    }
}

