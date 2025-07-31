package com.example.examseatplanner.repository;

import com.example.examseatplanner.model.SeatAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatAssignmentRepository extends JpaRepository<SeatAssignment, Integer> {
    List<SeatAssignment> findByRoomRoomNo(Integer roomNo);
    List<SeatAssignment> findByExamId(Integer examId);
}
