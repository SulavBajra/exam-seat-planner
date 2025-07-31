package com.example.examseatplanner.repository;

import com.example.examseatplanner.model.Room;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room,Integer> {
    Optional<Room> findByRoomNo(Integer roomNo);

    List<Room> findAllByRoomNoIn(@NotEmpty(message = "Room list must not be empty") List<@NotNull(message = "Room ID cannot be null") Integer> roomNo);
}
