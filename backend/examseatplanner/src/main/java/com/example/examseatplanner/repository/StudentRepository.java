package com.example.examseatplanner.repository;

import com.example.examseatplanner.model.Program;
import com.example.examseatplanner.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
    @Query("SELECT MAX(s.roll) FROM Student s WHERE s.program = :program")
    Optional<Integer> findMaxRollByProgram(@Param("program") Program program);

    List<Student> findBySemester(int semester);

    Optional<Student> findByRoll(int roll);

    Optional<Student> findByStudentId(String studentId);

    Boolean existsByStudentId(String studentId);

    List<Student> findByProgramAndSemester(Program program, int semester);

    List<Student> findByProgramProgramCodeAndSemester(Integer programCode, int semester);
}