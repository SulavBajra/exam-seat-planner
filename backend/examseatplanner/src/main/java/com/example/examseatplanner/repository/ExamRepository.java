package com.example.examseatplanner.repository;

import com.example.examseatplanner.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam,Long> {
    List<Exam> findByDate(String date);
}
