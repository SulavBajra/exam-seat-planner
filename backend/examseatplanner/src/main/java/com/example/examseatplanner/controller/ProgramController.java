package com.example.examseatplanner.controller;

import com.example.examseatplanner.dto.ProgramDTO;
import com.example.examseatplanner.service.ProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/programs")
public class ProgramController {
    private final ProgramService programService;

    @Autowired
    public ProgramController(ProgramService programService){
        this.programService = programService;
    }

    @PostMapping("/save")
    public ResponseEntity<ProgramDTO> saveProgram(@Validated @RequestBody ProgramDTO dto){
        return ResponseEntity.ok(programService.saveProgram(dto));
    }


}
