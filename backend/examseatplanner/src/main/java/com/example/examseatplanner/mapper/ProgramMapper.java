package com.example.examseatplanner.mapper;

import com.example.examseatplanner.dto.ProgramRequestDTO;
import com.example.examseatplanner.dto.ProgramResponseDTO;
import com.example.examseatplanner.model.Program;

import java.util.List;

public class ProgramMapper {

    public static Program toEntity(ProgramRequestDTO dto) {
        return new Program(dto.programName(), dto.programCode());
    }

    public static ProgramResponseDTO toDTO(Program program) {
        return new ProgramResponseDTO(
                program.getProgramCode(),
                program.getProgramName()
        );
    }

    public static List<ProgramResponseDTO> toDTOList(List<Program> programs) {
        return programs.stream()
                .map(ProgramMapper::toDTO)
                .toList();
    }
}
