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
public interface StudentRepository extends JpaRepository<Student, String> {

    /**
     * Find students by program
     */

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

    /**
     * Find students by multiple programs
     */
    List<Student> findByProgramIn(List<Program> programs);

    /**
     * Find students by semester
     */
    List<Student> findBySemester(Student.Semester semester);

    /**
     * Find students by program and semester
     */
    List<Student> findByProgramAndSemester(Program program, Student.Semester semester);

    /**
     * Find students by program code and semester
     */
    @Query("SELECT s FROM Student s WHERE s.program.programCode = :programCode AND s.semester = :semester")
    List<Student> findByProgramCodeAndSemester(@Param("programCode") Integer programCode,
                                               @Param("semester") Student.Semester semester);

    /**
     * Check if roll number is taken for a program and semester
     */
    boolean existsByRollAndProgramAndSemester(int roll, Program program, Student.Semester semester);

    /**
     * Find students by roll number
     */
    List<Student> findByRoll(int roll);

    /**
     * Find student by program, semester, and roll
     */
    Optional<Student> findByProgramAndSemesterAndRoll(Program program, Student.Semester semester, int roll);

    /**
     * Count students in a program
     */
    long countByProgram(Program program);

    /**
     * Count students in a semester
     */
    long countBySemester(Student.Semester semester);

//    boolean existsByProgramAndSemesterAndRoll(Program program, int semester, int roll);
    /**
     * Find students with roll numbers in range
     */
    List<Student> findByRollBetween(int startRoll, int endRoll);

    /**
     * Get max roll number for a program and semester
     */
    @Query("SELECT MAX(s.roll) FROM Student s WHERE s.program = :program AND s.semester = :semester")
    Optional<Integer> findMaxRollByProgramAndSemester(@Param("program") Program program,
                                                      @Param("semester") Student.Semester semester);
}