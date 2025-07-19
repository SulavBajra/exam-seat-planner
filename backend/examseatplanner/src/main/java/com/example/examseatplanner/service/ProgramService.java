package com.example.examseatplanner.service;

import com.example.examseatplanner.dto.ProgramDTO;
import com.example.examseatplanner.model.Program;
import com.example.examseatplanner.model.Subject;
import com.example.examseatplanner.repository.ProgramRepository;
import com.example.examseatplanner.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProgramService {

    private final ProgramRepository programRepository;

    @Autowired
    public ProgramService(ProgramRepository programRepository){
        this.programRepository = programRepository;
    }

    public Program saveProgram(Program program){
        return programRepository.save(program);
    }

    public Optional<Program> findByProgramName(String programName){
        return programRepository.findByProgramName(programName);
    }

    public ProgramDTO saveProgram(ProgramDTO dto){
        List<String> subjects = dto.subjectNames();
        return new ProgramDTO(dto.programName(),dto.programCode(),subjects);
    }


}
