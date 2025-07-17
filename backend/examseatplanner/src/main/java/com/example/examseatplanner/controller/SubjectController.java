package com.example.examseatplanner.controller;

import com.example.examseatplanner.model.Subject;
import com.example.examseatplanner.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/subject")
public class SubjectController {

    private SubjectService subjectService;

    @Autowired
    public SubjectController(SubjectService subjectService){
        this.subjectService = subjectService;
    }

    @PostMapping("/add")
    public Subject addSubject(@RequestBody Subject subject){
        return subjectService.insertSubject(subject);
    }

}
