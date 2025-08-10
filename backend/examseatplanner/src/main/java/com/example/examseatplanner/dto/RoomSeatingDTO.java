package com.example.examseatplanner.dto;


import java.util.List;

public record RoomSeatingDTO(
        Integer roomNo,
        int numRows,
        int numColumns,
        int seatingCapacity,
        List<SideSeatingDTO> sides
) {}