package com.example.examseatplanner.controller;

import com.example.examseatplanner.model.Program;
import com.example.examseatplanner.service.ProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/program")
public class ProgramController {
    private final ProgramService programService;

    @Autowired
    public ProgramController(ProgramService programService){
        this.programService = programService;
    }

    @PostMapping("/save")
    public Program saveProgram(@RequestBody Program program){
        return programService.saveProgram(program);
    }

}
