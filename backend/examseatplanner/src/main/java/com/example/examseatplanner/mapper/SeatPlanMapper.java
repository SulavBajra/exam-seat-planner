package com.example.examseatplanner.mapper;

import com.example.examseatplanner.dto.SeatAssignmentDTO;
import com.example.examseatplanner.model.SeatingPlan;

public class SeatPlanMapper {
    public static SeatAssignmentDTO toDTO(SeatingPlan seatingPlan){
        SeatAssignmentDTO seatAssignmentDTO = new SeatAssignmentDTO();
        seatAssignmentDTO.setProgramCode(seatingPlan.getProgramCode());
        seatAssignmentDTO.setSemester(seatingPlan.getSemester());
        seatAssignmentDTO.setRoll(seatingPlan.getRoll());
        seatAssignmentDTO.setRow(seatingPlan.getRowNumber());
        seatAssignmentDTO.setColumn(seatingPlan.getColumnNumber());
        return seatAssignmentDTO;
    }

    public static SeatingPlan toModel(SeatAssignmentDTO dto){
        SeatingPlan sp = new SeatingPlan();
        sp.setProgramCode(dto.getProgramCode());
        sp.setSemester(dto.getSemester());
        sp.setRoll(dto.getRoll());
        sp.setRowNumber(dto.getRow());
        sp.setColumnNumber(dto.getColumn());
        return sp;
    }

}
