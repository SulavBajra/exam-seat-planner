package com.example.examseatplanner.controller;

import com.example.examseatplanner.dto.SeatDTO;
import com.example.examseatplanner.model.Seat;
import com.example.examseatplanner.repository.SeatRepository;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    private final SeatRepository seatRepository;

    public SeatController(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    @GetMapping("/assignments")
    public Map<String, List<SeatDTO>> getAssignments() {
        List<Seat> seats = seatRepository.findAll();

        return seats.stream()
                .collect(Collectors.groupingBy(
                        seat -> String.valueOf(seat.getRoom().getRoomNo()),
                        Collectors.mapping(SeatDTO::fromEntity, Collectors.toList())
                ));
    }
}
