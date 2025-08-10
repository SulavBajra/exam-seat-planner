package com.example.examseatplanner.dto;

import java.util.List;

public record RowSeatingDTO(
        int rowNumber,
        List<SeatDTO> seats
) {}