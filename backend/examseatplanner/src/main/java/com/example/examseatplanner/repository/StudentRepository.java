package com.example.examseatplanner.repository;

import com.example.examseatplanner.model.Student;
import com.example.examseatplanner.model.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {

    @Query("""
        SELECT COUNT(s)
        FROM Student s
        WHERE s.program IN (
            SELECT eps.program
            FROM ExamProgramSemester eps
            WHERE eps.exam.id = :examId
        )
        AND s.semester IN (
            SELECT eps.semester
            FROM ExamProgramSemester eps
            WHERE eps.exam.id = :examId
        )
    """)
    Long countStudentsForExam(@Param("examId") Integer examId);

    Optional<Student> findByStudentId(Integer studentId);

    List<Student> findByProgram(Program program);

    @Query("SELECT COUNT(s) FROM Student s WHERE s.program.programCode = :programCode AND s.semester = :semester")
    long countByProgramCodeAndSemester(@Param("programCode") Integer programCode,
                                       @Param("semester") Student.Semester semester);

    @Query("""
        SELECT s FROM Student s
        JOIN ExamProgramSemester eps ON eps.program = s.program AND eps.semester = s.semester
        WHERE eps.exam.id = :examId
    """)
    List<Student> findByExamId(@Param("examId") Integer examId);

    List<Student> findByProgramIn(List<Program> programs);

    List<Student> findBySemester(Student.Semester semester);

    List<Student> findByProgramAndSemester(Program program, Student.Semester semester);

    @Query("SELECT s FROM Student s WHERE s.program.programCode = :programCode AND s.semester = :semester")
    List<Student> findByProgramCodeAndSemester(@Param("programCode") Integer programCode,
                                               @Param("semester") Student.Semester semester);

    boolean existsByRollAndProgramAndSemester(int roll, Program program, Student.Semester semester);

    List<Student> findByRoll(int roll);

    boolean existsByProgram_ProgramCode(Integer programCode);

    Optional<Student> findByProgramAndSemesterAndRoll(Program program, Student.Semester semester, int roll);

     @Query("SELECT s FROM Student s WHERE s.program.programCode = :programCode AND s.semester = :semester AND s.roll = :roll")
    Optional<Student> findByProgramCodeAndSemesterAndRoll(Integer programCode, Student.Semester semester, int roll);


    long countByProgram(Program program);

    long countBySemester(Student.Semester semester);

    List<Student> findByRollBetween(int startRoll, int endRoll);

    @Query("SELECT MAX(s.roll) FROM Student s WHERE s.program = :program AND s.semester = :semester")
    Optional<Integer> findMaxRollByProgramAndSemester(@Param("program") Program program,
                                                      @Param("semester") Student.Semester semester);
}