package com.example.examseatplanner.repository;

import com.example.examseatplanner.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {

    /**
     * Find rooms with minimum capacity
     */
    @Query("SELECT r FROM Room r WHERE (r.numRow * 3* 2) >= :minCapacity")
    List<Room> findAvailableRoomsWithMinCapacity(@Param("minCapacity") int minCapacity);

    /**
     * Find rooms by number of rows
     */
    List<Room> findByNumRow(int numRow);


    /**
     * Find rooms with exact capacity
     */
    @Query("SELECT r FROM Room r WHERE (r.numRow * 3 * 2) = :capacity")
    List<Room> findByExactCapacity(@Param("capacity") int capacity);

    /**
     * Find rooms ordered by capacity (descending)
     */
    @Query("SELECT r FROM Room r ORDER BY (r.numRow * 3 * 2) DESC")
    List<Room> findAllOrderByCapacityDesc();

    /**
     * Get total capacity of all rooms
     */
    @Query("SELECT SUM(r.numRow * 3 * 2) FROM Room r")
    Long getTotalCapacityAllRooms();


}
