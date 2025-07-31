package com.example.examseatplanner.service;

import com.example.examseatplanner.model.Exam;
import com.example.examseatplanner.model.Room;
import com.example.examseatplanner.repository.ExamRepository;
import com.example.examseatplanner.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final ExamRepository examRepository;

    @Autowired
    public RoomService(RoomRepository roomRepository, ExamRepository examRepository) {
        this.roomRepository = roomRepository;
        this.examRepository = examRepository;
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Optional<Room> getRoomByNo(Integer roomNo) {
        return roomRepository.findByRoomNo(roomNo);
    }

    public Room saveRoom(Room room) {
        // Calculate seating capacity based on rows and columns
        room.setSeatingCapacity(room.getNumRow() * room.getNumColumn());
        return roomRepository.save(room);
    }

    public void deleteRoom(Integer roomNo) {
        if (!roomRepository.existsById(roomNo)) {
            throw new RuntimeException("Room not found with number: " + roomNo);
        }
        roomRepository.deleteById(roomNo);
    }

    public List<Room> getAvailableRooms(String date, String time) {
        // Get all rooms
        List<Room> allRooms = roomRepository.findAll();

        // Get exams scheduled for the given date and time
        List<Exam> scheduledExams = examRepository.findByDate(date).stream()
                .filter(exam -> exam.getTime().equals(time))
                .toList();

        // Get room numbers that are already booked
        Set<Integer> bookedRoomNos = scheduledExams.stream()
                .flatMap(exam -> exam.getRooms().stream())
                .map(Room::getRoomNo)
                .collect(Collectors.toSet());

        // Return available rooms
        return allRooms.stream()
                .filter(room -> !bookedRoomNos.contains(room.getRoomNo()))
                .toList();
    }

    public int getTotalCapacity(List<Integer> roomNos) {
        return roomRepository.findAllByRoomNoIn(roomNos).stream()
                .mapToInt(Room::getSeatingCapacity)
                .sum();
    }
}