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


    @Query("SELECT DISTINCT r.roomNo FROM Exam e JOIN e.rooms r WHERE " +
        "(e.startDate <= :endDate AND e.endDate >= :startDate)")
    List<Integer> findBookedRoomNumbersByDateRange(@Param("startDate") LocalDate startDate, 
                                                @Param("endDate") LocalDate endDate);



    @Query("SELECT DISTINCT e FROM Exam e JOIN e.programSemesters eps WHERE eps.program = :program")
    List<Exam> findByProgram(@Param("program") Program program);

    @Query("SELECT DISTINCT e FROM Exam e JOIN e.programSemesters eps WHERE eps.program.programCode = :programCode")
    List<Exam> findByProgramCode(@Param("programCode") Integer programCode);

    @Query("SELECT DISTINCT e FROM Exam e JOIN e.programSemesters eps WHERE eps.program.programCode IN :programCodes")
    List<Exam> findByProgramCodes(@Param("programCodes") List<Integer> programCodes);

    @Query("SELECT DISTINCT e FROM Exam e JOIN e.rooms r WHERE r.roomNo IN :roomNumbers")
    List<Exam> findByRoomNumbers(@Param("roomNumbers") List<Integer> roomNumbers);

     @Query("SELECT e FROM Exam e WHERE (e.startDate <= :endDate AND e.endDate >= :startDate)")
    List<Exam> findOverlappingExams(@Param("startDate") LocalDate startDate, 
                                   @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(e) > 0 FROM Exam e JOIN e.rooms r WHERE r.roomNo = :roomNo AND " +
           "(e.startDate <= :endDate AND e.endDate >= :startDate)")
    boolean isRoomOccupied(@Param("roomNo") Integer roomNo, 
                          @Param("startDate") LocalDate startDate,
                          @Param("endDate") LocalDate endDate);

    @Query("SELECT r.roomNo FROM Exam e JOIN e.rooms r WHERE e.id = :examId")
    List<Integer> findRoomNumbersByExamId(@Param("examId") Integer examId);
    
    @Query("""
        SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END
        FROM Student s
        WHERE s.program.programCode = :programCode
    """)
    boolean existsByProgramCodeCustom(@Param("programCode") Integer programCode);

    
}