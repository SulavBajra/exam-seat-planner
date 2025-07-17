package com.example.examseatplanner.service;

import com.example.examseatplanner.model.Program;
import com.example.examseatplanner.repository.ProgramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}
