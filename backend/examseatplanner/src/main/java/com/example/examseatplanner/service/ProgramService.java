package com.example.examseatplanner.service;

import com.example.examseatplanner.dto.ProgramDTO;
import com.example.examseatplanner.model.Program;
import com.example.examseatplanner.model.Subject;
import com.example.examseatplanner.repository.ProgramRepository;
import com.example.examseatplanner.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//@Service
//public class ProgramService {
//
//    private final ProgramRepository programRepository;
//
//    @Autowired
//    public ProgramService(ProgramRepository programRepository){
//        this.programRepository = programRepository;
//    }
//
//    public Program saveProgram(Program program){
//        return programRepository.save(program);
//    }
//
//    public Optional<Program> findByProgramName(String programName){
//        return programRepository.findByProgramName(programName);
//    }
//
//    public ProgramDTO saveProgram(ProgramDTO dto){
//        List<String> subjects = dto.subjectNames();
//        return new ProgramDTO(dto.programName(),dto.programCode(),subjects);
//    }
//}

@Service
public class ProgramService {

    private final ProgramRepository programRepository;
    private final SubjectRepository subjectRepository;

    @Autowired
    public ProgramService(ProgramRepository programRepository,
                          SubjectRepository subjectRepository) {
        this.programRepository = programRepository;
        this.subjectRepository = subjectRepository;
    }

    public ProgramDTO saveProgram(ProgramDTO dto) {
        // Check if program already exists
        Optional<Program> existingProgram = programRepository.findById(dto.programCode());
        if (existingProgram.isPresent()) {
            throw new RuntimeException("Program with code " + dto.programCode() + " already exists");
        }

        // Create and save program
        Program program = new Program(dto.programName(), dto.programCode());
        Program savedProgram = programRepository.save(program);

        // Create and save subjects
        List<Subject> subjects = new ArrayList<>();
        int subjectCodeCounter = dto.programCode() * 100; // Generate subject codes

        for (String subjectName : dto.subjectNames()) {
            Subject subject = new Subject();
            subject.setSubjectCode(++subjectCodeCounter);
            subject.setSubjectName(subjectName);
            subject.setProgram(savedProgram);
            subject.setDuration(180); // Default 3 hours
            subject.setTimeSlot("09:00-12:00"); // Default time slot
            subjects.add(subject);
        }

        subjectRepository.saveAll(subjects);

        return new ProgramDTO(
                savedProgram.getProgramName(),
                savedProgram.getProgramCode(),
                subjects.stream().map(Subject::getSubjectName).toList()
        );
    }

    public Optional<Program> findByProgramName(String programName) {
        return programRepository.findByProgramName(programName);
    }

    public List<Program> getAllPrograms() {
        return programRepository.findAll();
    }
}
