package com.example.examseatplanner.mapper;

import com.example.examseatplanner.dto.SeatAssignmentDTO;
import com.example.examseatplanner.model.SeatingPlan;

public class SeatPlanMapper {

    public static SeatAssignmentDTO toDTO(SeatingPlan seatingPlan) {
        if (seatingPlan == null) return null;

        SeatAssignmentDTO dto = new SeatAssignmentDTO();
        dto.setExamId(seatingPlan.getExamId());

        dto.setProgramCode(seatingPlan.getProgramCode());
        dto.setSemester(seatingPlan.getSemester());
        dto.setRoll(seatingPlan.getRoll());
        dto.setRowNumber(seatingPlan.getRowNumber());
        dto.setColumnNumber(seatingPlan.getColumnNumber());

        if (seatingPlan.getRoomNo() != null)
        dto.setRoomNo(seatingPlan.getRoomNo());
        return dto;
    }

    public static SeatingPlan toModel(SeatAssignmentDTO dto) {
        if (dto == null) return null;
        SeatingPlan seatingPlan = new SeatingPlan();
        seatingPlan.setExamId(dto.getExamId());
        seatingPlan.setRoomNo(dto.getRoomNo());
        seatingPlan.setProgramCode(dto.getProgramCode());
        seatingPlan.setSemester(dto.getSemester());
        seatingPlan.setRoll(dto.getRoll());
        seatingPlan.setRowNumber(dto.getRowNumber());
        seatingPlan.setColumnNumber(dto.getColumnNumber());

        return seatingPlan;
    }
}
