package com.example.examseatplanner.service;

import com.example.examseatplanner.dto.StudentRequestDTO;
import com.example.examseatplanner.dto.StudentResponseDTO;
import com.example.examseatplanner.model.Program;
import com.example.examseatplanner.model.Student;
import com.example.examseatplanner.model.Subject;
import com.example.examseatplanner.repository.ProgramRepository;
import com.example.examseatplanner.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final SubjectService subjectService;
    private final ProgramService programService;

    @Autowired
    public StudentService(StudentRepository studentRepository,
                          SubjectService subjectService,
                          ProgramService programService) {
        this.studentRepository = studentRepository;
        this.subjectService = subjectService;
        this.programService = programService;
    }

    public boolean existsByStudentId(String studentId) {
        return studentRepository.existsByStudentId(studentId);
    }

    public String generateStudentCode(String enrolledYear, int programCode, int roll) {
        return String.format("%04d%02d%02d", Integer.parseInt(enrolledYear), programCode, roll);
    }

    public StudentResponseDTO registerFromDTO(StudentRequestDTO dto) {
        Program program = programService.findByProgramName(dto.program())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Program Not Found"));

        List<Subject> subjects = dto.subjects().stream()
                .map(name -> subjectService.findBySubjectName(name)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject not found: " + name)))
                .toList();

        String studentId = generateStudentCode(dto.enrollYear(), program.getProgramCode(), dto.roll());

        if (existsByStudentId(studentId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Student with ID already exists");
        }

        Student student = new Student(subjects, program, dto.enrollYear(), dto.semester(), dto.roll());
        student.setStudentId(studentId);

        Student savedStudent = studentRepository.save(student);

        return new StudentResponseDTO(
                savedStudent.getStudentId(),
                savedStudent.getEnrolledYear(),
                savedStudent.getSemester(),
                savedStudent.getRoll(),
                savedStudent.getProgram().getProgramName(),
                savedStudent.getSubjects().stream().map(Subject::getSubjectName).toList()
        );
    }
}
