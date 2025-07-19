package com.example.examseatplanner.controller;

import com.example.examseatplanner.dto.ProgramDTO;
import com.example.examseatplanner.model.Program;
import com.example.examseatplanner.model.Subject;
import com.example.examseatplanner.service.ProgramService;
import com.example.examseatplanner.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/programs")
public class ProgramController {
    private final ProgramService programService;
    private final SubjectService subjectService;

    @Autowired
    public ProgramController(ProgramService programService,SubjectService subjectService){
        this.programService = programService;
        this.subjectService = subjectService;
    }

    @PostMapping("/save")
    public Program saveProgram(@RequestBody ProgramDTO dto){
        List<Subject> subjects = dto.subjectNames().stream()
                .map(name -> subjectService.findBySubjectName(name)
                        .orElseThrow(() -> new RuntimeException("Subject not found: " + name)))
                .toList();

        Program program = new Program(dto.programName(), dto.programCode());
        program.setSubjects(subjects);

        return programService.saveProgram(program);
    }


}
