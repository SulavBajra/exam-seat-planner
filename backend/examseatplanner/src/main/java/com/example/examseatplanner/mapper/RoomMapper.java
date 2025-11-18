package com.example.examseatplanner.mapper;

import com.example.examseatplanner.dto.RoomRequestDTO;
import com.example.examseatplanner.dto.RoomResponseDTO;
import com.example.examseatplanner.model.Room;

import java.util.List;

public class RoomMapper {

    public static Room toEntity(RoomRequestDTO dto) {
        return new Room(
                dto.roomNo(),
                dto.numRow(),
                dto.seatsPerBench(),
                dto.roomColumn()
        );
    }

      public static Room toEntity(RoomResponseDTO dto) {
        return new Room(
                dto.roomNo(),
                dto.numRow(),
                dto.seatsPerBench(),
                dto.roomColumn()
        );
    }

    public static RoomResponseDTO toDTO(Room room) {
        return new RoomResponseDTO(
                room.getRoomNo(),
                room.getSeatingCapacity(),
                room.getNumRow(),
                room.getSeatsPerBench(),
                room.getRoomColumn()
        );
    }

    public static List<RoomResponseDTO> toDTOList(List<Room> rooms) {
        return rooms.stream()
                .map(RoomMapper::toDTO)
                .toList();
    }
}
