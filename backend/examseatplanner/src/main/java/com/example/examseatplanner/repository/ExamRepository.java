package com.example.examseatplanner.repository;

import com.example.examseatplanner.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Integer> {
    List<Exam> findByDate(LocalDate date);

    List<Exam> findBySubjectSubjectCode(Integer subjectCode);

    List<Exam> findByDateBetween(LocalDate startDate, LocalDate endDate);
}