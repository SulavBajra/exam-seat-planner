package com.example.examseatplanner.repository;

import com.example.examseatplanner.dto.ExamResponseDTO;
import com.example.examseatplanner.model.Exam;
import com.example.examseatplanner.model.Program;
import com.example.examseatplanner.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Integer> {

    List<Exam> findByDate(LocalDate date);
    List<Exam> findByDateBetween(LocalDate startDate, LocalDate endDate);

    // Query using programSemesters join (correct approach for your model)
    @Query("SELECT DISTINCT e FROM Exam e JOIN e.programSemesters eps WHERE eps.program = :program")
    List<Exam> findByProgram(@Param("program") Program program);

    // Query by program code
    @Query("SELECT DISTINCT e FROM Exam e JOIN e.programSemesters eps WHERE eps.program.programCode = :programCode")
    List<Exam> findByProgramCode(@Param("programCode") Integer programCode);

    // Query by room and date
    @Query("SELECT e FROM Exam e JOIN e.rooms r WHERE r.roomNo = :roomNo AND e.date = :date")
    List<Exam> findByRoomAndDate(@Param("roomNo") Integer roomNo, @Param("date") LocalDate date);

    // Query by multiple program codes
    @Query("SELECT DISTINCT e FROM Exam e JOIN e.programSemesters eps WHERE eps.program.programCode IN :programCodes")
    List<Exam> findByProgramCodes(@Param("programCodes") List<Integer> programCodes);

    // Query by room numbers
    @Query("SELECT DISTINCT e FROM Exam e JOIN e.rooms r WHERE r.roomNo IN :roomNumbers")
    List<Exam> findByRoomNumbers(@Param("roomNumbers") List<Integer> roomNumbers);

    // Upcoming exams
    @Query("SELECT e FROM Exam e WHERE e.date >= CURRENT_DATE ORDER BY e.date")
    List<Exam> findUpcomingExams();

    // Past exams
    @Query("SELECT e FROM Exam e WHERE e.date < CURRENT_DATE ORDER BY e.date DESC")
    List<Exam> findPastExams();

    // Room availability check
    @Query("SELECT COUNT(e) > 0 FROM Exam e JOIN e.rooms r WHERE r.roomNo = :roomNo AND e.date = :date")
    boolean isRoomOccupied(@Param("roomNo") Integer roomNo, @Param("date") LocalDate date);

    // Date range and program query
    @Query("SELECT e FROM Exam e JOIN e.programSemesters eps WHERE e.date BETWEEN :startDate AND :endDate AND eps.program.programCode = :programCode")
    List<Exam> findByDateRangeAndProgram(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("programCode") Integer programCode
    );

    @Query("SELECT r.roomNo FROM Exam e JOIN e.rooms r WHERE e.id = :examId")
    List<Integer> findRoomNumbersByExamId(@Param("examId") Integer examId);


}