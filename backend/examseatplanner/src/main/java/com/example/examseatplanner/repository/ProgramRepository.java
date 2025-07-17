package com.example.examseatplanner.repository;

import com.example.examseatplanner.model.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProgramRepository extends JpaRepository <Program, Integer>{
    String findByProgramCode(Integer programCode);
    Optional<Program> findByProgramName(String programName);
}
