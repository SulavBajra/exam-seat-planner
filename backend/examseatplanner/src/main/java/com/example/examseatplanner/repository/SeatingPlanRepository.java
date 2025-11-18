package com.example.examseatplanner.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.examseatplanner.model.SeatingPlan;

@Repository
public interface SeatingPlanRepository extends JpaRepository<SeatingPlan, Integer> {

    List<SeatingPlan> findByExamId(Integer examId);

    List<SeatingPlan> findByExamIdAndRoomNo(Integer examId, String roomNo);

    void deleteByExamId(Integer examId);

    Optional<SeatingPlan> findByExamIdAndProgramCodeAndSemesterAndRoll(
        Integer examId,
        String programCode,
        Integer semester,
        Integer roll
    );
}
