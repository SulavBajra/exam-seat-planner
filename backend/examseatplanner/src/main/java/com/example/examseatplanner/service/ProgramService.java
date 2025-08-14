package com.example.examseatplanner.service;

import com.example.examseatplanner.dto.ProgramRequestDTO;
import com.example.examseatplanner.dto.ProgramResponseDTO;
import com.example.examseatplanner.mapper.ProgramMapper;
import com.example.examseatplanner.model.Program;
import com.example.examseatplanner.repository.ProgramRepository;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ProgramService {

    private final ProgramRepository programRepository;

    @Autowired
    public ProgramService(ProgramRepository programRepository) {
        this.programRepository = programRepository;
    }

    public List<ProgramResponseDTO> getAllPrograms() {
        List<Program> programs = programRepository.findAll();
        return ProgramMapper.toDTOList(programs);
    }

    public Optional<ProgramResponseDTO> getProgramById(Integer programCode) {
        return programRepository.findById(programCode)
                .map(ProgramMapper::toDTO);
    }

    public List<Program> findAllById(Iterable<Integer> ids) {
        return programRepository.findAllById(ids);
    }

    public ProgramResponseDTO saveProgram(ProgramRequestDTO dto) {
        Program program = ProgramMapper.toEntity(dto);
        Program saved = programRepository.save(program);
        return ProgramMapper.toDTO(saved);
    }

    public void deleteProgram(Integer programCode) {
        programRepository.deleteById(programCode);
    }

    public List<ProgramResponseDTO> searchProgramsByName(String name) {
        List<Program> programs = programRepository.findByProgramNameContainingIgnoreCase(name);
        return ProgramMapper.toDTOList(programs);
    }



    public List<Integer> getProgramCodeByProgramName(String name) {
        if (StringUtils.isBlank(name)) {
            return Collections.emptyList();
        }

        List<ProgramResponseDTO> programs = searchProgramsByName(name);
        if (programs == null) {
            return Collections.emptyList();
        }

        return programs.stream()
                .map(ProgramResponseDTO::programCode)
                .filter(Objects::nonNull)
                .toList();
    }

}
