package com.example.examseatplanner.repository;

import com.example.examseatplanner.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamRepository extends JpaRepository<Exam,Integer> {
    List<Exam> findByDate(String date);
    Optional<Exam> findByTime(String time);
}
