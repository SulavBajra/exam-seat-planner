package com.example.examseatplanner.service;

import com.example.examseatplanner.dto.SubjectDTO;
import com.example.examseatplanner.model.Program;
import com.example.examseatplanner.model.Subject;
import com.example.examseatplanner.repository.ProgramRepository;
import com.example.examseatplanner.repository.SubjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final ProgramRepository programRepository;

    public SubjectService(SubjectRepository subjectRepository, ProgramRepository programRepository) {
        this.subjectRepository = subjectRepository;
        this.programRepository = programRepository;
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public Optional<Subject> findBySubjectName(String subjectName) {
        return subjectRepository.findBySubjectName(subjectName);
    }

    public Optional<Subject> getSubjectByCode(Integer subjectCode) {
        return subjectRepository.findBySubjectCode(subjectCode);
    }

    public List<Subject> getSubjectsByProgramCode(Integer programCode) {
        return subjectRepository.findByProgramProgramCode(programCode);
    }

    public Subject createSubject(SubjectDTO subjectDTO) {
        Program program = programRepository.findById(subjectDTO.programCode())
                .orElseThrow(() -> new RuntimeException("Program not found with code: " + subjectDTO.programCode()));

        Subject subject = new Subject();
        subject.setSubjectCode(subjectDTO.subjectCode());
        subject.setSubjectName(subjectDTO.subjectName());
        subject.setSemester(subjectDTO.semester());
        subject.setProgram(program);

        return subjectRepository.save(subject);
    }

    public void deleteSubject(Integer subjectCode) {
        subjectRepository.deleteBySubjectCode(subjectCode);
    }
}