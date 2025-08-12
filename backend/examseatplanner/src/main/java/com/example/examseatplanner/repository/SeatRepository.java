package com.example.examseatplanner.repository;

import com.example.examseatplanner.model.Seat;
import com.example.examseatplanner.model.Room;
import com.example.examseatplanner.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    /**
     * Find all seats in a specific room
     */
    List<Seat> findByRoomRoomNo(Integer roomNo);

    /**
     * Find all seats assigned to a specific student
     */
    List<Seat> findByAssignedStudent(Student student);

    /**
     * Find seat by room, row, bench, and position
     */
    Optional<Seat> findByRoomAndRowNumberAndBenchNumberAndSeatPosition(
            Room room, int rowNumber, int benchNumber, int seatPosition);

    /**
     * Find all occupied seats in a room
     */
    @Query("SELECT s FROM Seat s WHERE s.room.roomNo = :roomNo AND s.assignedStudent IS NOT NULL")
    List<Seat> findOccupiedSeatsByRoom(@Param("roomNo") Integer roomNo);

    /**
     * Find all empty seats in a room
     */
    @Query("SELECT s FROM Seat s WHERE s.room.roomNo = :roomNo AND s.assignedStudent IS NULL")
    List<Seat> findEmptySeatsByRoom(@Param("roomNo") Integer roomNo);

    /**
     * Count occupied seats in a room
     */
    @Query("SELECT COUNT(s) FROM Seat s WHERE s.room.roomNo = :roomNo AND s.assignedStudent IS NOT NULL")
    long countOccupiedSeatsByRoom(@Param("roomNo") Integer roomNo);

    /**
     * Count empty seats in a room
     */
    @Query("SELECT COUNT(s) FROM Seat s WHERE s.room.roomNo = :roomNo AND s.assignedStudent IS NULL")
    long countEmptySeatsByRoom(@Param("roomNo") Integer roomNo);

    /**
     * Find seats by room and side
     */
    List<Seat> findByRoomRoomNoAndSeatSide(Integer roomNo, int seatSide);

    /**
     * Find seats by room, side, and row
     */
    List<Seat> findByRoomRoomNoAndSeatSideAndRowNumber(Integer roomNo, int seatSide, int rowNumber);

    /**
     * Delete all seats for a specific room
     */
    void deleteByRoomRoomNo(Integer roomNo);

    /**
     * Find seats assigned to students from a specific program
     */
    @Query("SELECT s FROM Seat s WHERE s.assignedStudent.program.programCode = :programCode AND s.assignedStudent IS NOT NULL")
    List<Seat> findSeatsByProgramCode(@Param("programCode") Integer programCode);

    /**
     * Find adjacent seats (for validation purposes)
     */
    @Query("SELECT s FROM Seat s WHERE s.room.roomNo = :roomNo AND s.seatSide = :seatSide AND s.rowNumber = :rowNumber " +
            "AND (s.benchNumber * 2 + s.seatPosition) BETWEEN :startSeatIndex AND :endSeatIndex ORDER BY s.benchNumber, s.seatPosition")
    List<Seat> findAdjacentSeats(@Param("roomNo") Integer roomNo, @Param("seatSide") int seatSide,
                                 @Param("rowNumber") int rowNumber, @Param("startSeatIndex") int startSeatIndex,
                                 @Param("endSeatIndex") int endSeatIndex);

    /**
     * Clear all seat assignments in a room
     */
    @Query("UPDATE Seat s SET s.assignedStudent = NULL WHERE s.room.roomNo = :roomNo")
    void clearSeatAssignmentsByRoom(@Param("roomNo") Integer roomNo);

    /**
     * Clear all seat assignments for a specific student
     */
    @Query("UPDATE Seat s SET s.assignedStudent = NULL WHERE s.assignedStudent.studentId = :studentId")
    void clearSeatAssignmentsByStudent(@Param("studentId") Integer studentId);

    /**
     * Find seats with validation issues (for debugging)
     */
    @Query("SELECT s FROM Seat s WHERE s.room IS NULL OR s.rowNumber < 0 OR s.benchNumber < 0 OR s.seatPosition < 0 OR s.seatSide < 0 OR s.seatSide > 2")
    List<Seat> findSeatsWithIssues();

    /**
     * Get seating statistics for a room
     */
    @Query("SELECT " +
            "COUNT(s) as totalSeats, " +
            "COUNT(s.assignedStudent) as occupiedSeats, " +
            "COUNT(CASE WHEN s.assignedStudent IS NULL THEN 1 END) as emptySeats " +
            "FROM Seat s WHERE s.room.roomNo = :roomNo")
    Object[] getSeatingStatsByRoom(@Param("roomNo") Integer roomNo);
}