package com.example.examseatplanner.repository;

import com.example.examseatplanner.model.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Integer> {

    /**
     * Find program by program code
     */
    Optional<Program> findByProgramCode(Integer programCode);

    /**
     * Find programs by name (case-insensitive partial match)
     */
    List<Program> findByProgramNameContainingIgnoreCase(String name);

    /**
     *
     * Find programs by exact name
     */
    Optional<Program> findByProgramName(String programName);

    /**
     * Check if program code exists
     */
    boolean existsByProgramCode(Integer programCode);

    /**
     * Find all programs ordered by program code
     */
    List<Program> findAllByOrderByProgramCode();


}