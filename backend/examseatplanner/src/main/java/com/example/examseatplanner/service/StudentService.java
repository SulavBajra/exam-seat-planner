package com.example.examseatplanner.service;

import com.example.examseatplanner.model.Student;
import com.example.examseatplanner.repository.ProgramRepository;
import com.example.examseatplanner.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentService {
    private StudentRepository studentRepository;

    private ProgramRepository programRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository, ProgramRepository programRepository){
        this.studentRepository = studentRepository;
        this.programRepository = programRepository;
    }

    public String generateStudentCode(String enrolledYear, int programCode, int roll) {
        return String.format("%04d%02d%02d", Integer.parseInt(enrolledYear), programCode, roll);
    }

    public Student registerStudent(Student student){
        var program = programRepository.findById(student.getProgram().getProgramCode())
                .orElseThrow(() -> new RuntimeException("Program with code "+
                        student.getProgram().getProgramCode() +
                        " Not found"));
        int currentMaxRoll = studentRepository.findMaxRollByProgram(program).orElse(0);
        int newRoll = currentMaxRoll + 1;
        String studentId = generateStudentCode(
                student.getEnrolledYear(),
                student.getProgram().getProgramCode(),
                student.getRoll());
        student.setStudentId(studentId);
        return studentRepository.save(student);

    }

}
