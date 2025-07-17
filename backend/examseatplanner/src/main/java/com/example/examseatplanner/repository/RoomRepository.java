package com.example.examseatplanner.repository;

import com.example.examseatplanner.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room,Long> {
    Optional<Room> findByRoomNo(Long id);
}
