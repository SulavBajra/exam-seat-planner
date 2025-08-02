package com.example.examseatplanner.repository;

import com.example.examseatplanner.model.Program;
import com.example.examseatplanner.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {
    Optional<Subject> findBySubjectName(String subjectName);

    List<Subject> findByProgramProgramCode(Integer programCode);

    Optional<Subject> findBySubjectCode(Integer subjectCode);

    void deleteBySubjectCode(Integer subjectCode);

    List<Subject> findByProgramAndSemester(Program program, int semester);

    List<Subject> findByProgramProgramCodeAndSemester(Integer programCode, int semester);
}