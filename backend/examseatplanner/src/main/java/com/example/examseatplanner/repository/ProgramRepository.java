package com.example.examseatplanner.repository;

import com.example.examseatplanner.model.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Integer> {

   
    Optional<Program> findByProgramCode(Integer programCode);

    List<Program> findByProgramNameContainingIgnoreCase(String name);

    Optional<Program> findByProgramName(String programName);

    boolean existsByProgramCode(Integer programCode);

    List<Program> findAllByOrderByProgramCode();


}