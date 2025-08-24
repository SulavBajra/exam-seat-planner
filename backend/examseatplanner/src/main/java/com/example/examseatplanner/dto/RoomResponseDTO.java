package com.example.examseatplanner.dto;

public record RoomResponseDTO(
        Integer roomNo,
        int seatingCapacity,
        int numRow,
        int seatsPerBench,
        int roomColumn
) {}
