//package com.example.examseatplanner.repository;
//
//import com.example.examseatplanner.model.Exam;
//import com.example.examseatplanner.model.Program;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//import java.time.LocalDate;
//import java.util.List;
//
//@Repository
//public interface ExamRepository extends JpaRepository<Exam, Integer> {
//    List<Exam> findByDate(LocalDate date);
//
//
//    List<Exam> findByDateBetween(LocalDate startDate, LocalDate endDate);
//
//
//    @Query("SELECT e FROM Exam e JOIN e.rooms r WHERE r.roomNo = :roomNo AND e.date = :date")
//    List<Exam> findByRoomAndDate(@Param("roomNo") Integer roomNo, @Param("date") LocalDate date);
//}

package com.example.examseatplanner.repository;

import com.example.examseatplanner.model.Exam;
import com.example.examseatplanner.model.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Integer> {

    /**
     * Find exams by date
     */
    List<Exam> findByDate(LocalDate date);

    /**
     * Find exams between date range
     */
    List<Exam> findByDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Find exams that contain a specific program
     */
    List<Exam> findByProgramsContaining(Program program);

    /**
     * Find exams by program code
     */
    @Query("SELECT e FROM Exam e JOIN e.programs p WHERE p.programCode = :programCode")
    List<Exam> findByPrograms_ProgramCode(@Param("programCode") Integer programCode);

    /**
     * Find exams using a specific room on a specific date
     */
    @Query("SELECT e FROM Exam e JOIN e.rooms r WHERE r.roomNo = :roomNo AND e.date = :date")
    List<Exam> findByRoomAndDate(@Param("roomNo") Integer roomNo, @Param("date") LocalDate date);

    /**
     * Find exams by multiple program codes
     */
    @Query("SELECT DISTINCT e FROM Exam e JOIN e.programs p WHERE p.programCode IN :programCodes")
    List<Exam> findByProgramCodes(@Param("programCodes") List<Integer> programCodes);

    /**
     * Find exams using specific rooms
     */
    @Query("SELECT DISTINCT e FROM Exam e JOIN e.rooms r WHERE r.roomNo IN :roomNumbers")
    List<Exam> findByRoomNumbers(@Param("roomNumbers") List<Integer> roomNumbers);

    /**
     * Find upcoming exams (from today onwards)
     */
    @Query("SELECT e FROM Exam e WHERE e.date >= CURRENT_DATE ORDER BY e.date")
    List<Exam> findUpcomingExams();

    /**
     * Find past exams
     */
    @Query("SELECT e FROM Exam e WHERE e.date < CURRENT_DATE ORDER BY e.date DESC")
    List<Exam> findPastExams();

    /**
     * Check if room is available for a date (no conflicting exams)
     */
    @Query("SELECT COUNT(e) FROM Exam e JOIN e.rooms r WHERE r.roomNo = :roomNo AND e.date = :date")
    long countConflictingExams(@Param("roomNo") Integer roomNo, @Param("date") LocalDate date);

    List<Exam> findByProgramsIn(List<Program> programs);
    /**
     * Get exams for a specific date range and program
     */
    @Query("SELECT e FROM Exam e JOIN e.programs p WHERE e.date BETWEEN :startDate AND :endDate AND p.programCode = :programCode")
    List<Exam> findByDateRangeAndProgram(@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate,
                                         @Param("programCode") Integer programCode);
}