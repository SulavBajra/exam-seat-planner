package com.example.examseatplanner.dto;

import java.util.List;

public record SideSeatingDTO(
        int sideNumber,
        String sideName,
        List<RowSeatingDTO> rows
) {}
