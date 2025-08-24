package com.example.examseatplanner.service;

import com.example.examseatplanner.dto.RoomRequestDTO;
import com.example.examseatplanner.dto.RoomResponseDTO;
import com.example.examseatplanner.mapper.RoomMapper;
import com.example.examseatplanner.model.Room;
import com.example.examseatplanner.model.Seat;
import com.example.examseatplanner.repository.RoomRepository;
import com.example.examseatplanner.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    @Autowired
    public RoomService(RoomRepository roomRepository){
        this.roomRepository = roomRepository;
    }

    public List<RoomResponseDTO> getAllRooms() {
        return RoomMapper.toDTOList(roomRepository.findAll());
    }

    public Optional<RoomResponseDTO> getRoomById(Integer roomNo) {
        return roomRepository.findById(roomNo)
                .map(RoomMapper::toDTO);
    }

    public List<Room> findAllById(Iterable<Integer> ids) {
        return roomRepository.findAllById(ids);
    }

    public RoomResponseDTO saveRoom(RoomRequestDTO dto) {
        if (roomRepository.existsById(dto.roomNo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Room with roomNo " + dto.roomNo() + " already exists.");
        }
        Room room = RoomMapper.toEntity(dto);
        Room saved = roomRepository.save(room);
        return RoomMapper.toDTO(saved);
    }

    public void deleteRoom(Integer roomNo) {
        roomRepository.deleteById(roomNo);
    }

    public List<RoomResponseDTO> getRoomsWithMinCapacity(int minCapacity) {
        List<Room> rooms = roomRepository.findAvailableRoomsWithMinCapacity(minCapacity);
        return RoomMapper.toDTOList(rooms);
    }

    public int getTotalCapacity(List<Room> rooms) {
        return rooms.stream().mapToInt(Room::getSeatingCapacity).sum();
    }
}
