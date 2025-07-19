package com.example.examseatplanner.service;

import com.example.examseatplanner.model.Subject;
import com.example.examseatplanner.repository.SubjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public Optional<Subject> findBySubjectName(String subjectName){
        return subjectRepository.findBySubjectName(subjectName);
    }


    public Optional<Subject> getSubjectByCode(Integer subjectCode) {
        return subjectRepository.findBySubjectCode(subjectCode);
    }

    public List<Subject> getSubjectsByProgramCode(Integer programCode) {
        return subjectRepository.findByProgramProgramCode(programCode);
    }

    public Subject saveSubject(Subject subject) {
        return subjectRepository.save(subject);
    }

    public void deleteSubject(Integer subjectCode) {
        subjectRepository.deleteBySubjectCode(subjectCode);
    }
}
