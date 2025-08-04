package com.example.examseatplanner.service;

import com.example.examseatplanner.dto.ProgramDTO;
import com.example.examseatplanner.model.Program;
import com.example.examseatplanner.repository.ProgramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProgramService {

    private final ProgramRepository programRepository;

    @Autowired
    public ProgramService(ProgramRepository programRepository) {
        this.programRepository = programRepository;
    }

    public List<ProgramDTO> getAllPrograms() {
        List<Program> programs = programRepository.findAll();
        return programs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProgramDTO saveProgram(ProgramDTO dto) {
        // Check if program already exists
        if (programRepository.existsById(dto.programCode())) {
            throw new RuntimeException("Program with code " + dto.programCode() + " already exists");
        }

        Program program = new Program(dto.programName(), dto.programCode());
        Program savedProgram = programRepository.save(program);

        return convertToDTO(savedProgram);
    }

    public Optional<Program> findByProgramName(String programName) {
        return programRepository.findByProgramName(programName);
    }

    public Optional<Program> findByProgramCode(Integer programCode) {
        return programRepository.findById(programCode);
    }

    private ProgramDTO convertToDTO(Program program) {
        return new ProgramDTO(
                program.getProgramName(),
                program.getProgramCode()
        );
    }
}