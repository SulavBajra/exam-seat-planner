package com.example.examseatplanner.service;

import com.example.examseatplanner.model.Subject;
import com.example.examseatplanner.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Service
public class SubjectService {
    private SubjectRepository subjectRepository;

    @Autowired
    public SubjectService(SubjectRepository subjectRepository){
        this.subjectRepository = subjectRepository;
    }

    public Subject insertSubject(Subject subject){
        return subjectRepository.save(subject);
    }

    public Optional<Subject> findBySubjectName(String subject){
        return subjectRepository.findBySubjectName(subject);
    }

}
