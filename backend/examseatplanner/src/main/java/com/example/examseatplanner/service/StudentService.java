package com.example.examseatplanner.service;

import com.example.examseatplanner.dto.StudentRequestDTO;
import com.example.examseatplanner.dto.StudentResponseDTO;
import com.example.examseatplanner.model.Program;
import com.example.examseatplanner.model.Student;
import com.example.examseatplanner.model.Subject;
import com.example.examseatplanner.repository.StudentRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
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

    public void importFromExcel(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header

                String studentId = row.getCell(0).getStringCellValue();
                String enrolledYear = row.getCell(1).getStringCellValue();
                int semester = (int) row.getCell(2).getNumericCellValue();
                int roll = (int) row.getCell(3).getNumericCellValue();
                String programName = row.getCell(4).getStringCellValue();

                // You may need to look up Program and Subject from DB by name
                Student student = new Student();
                student.setStudentId(studentId);
                student.setEnrolledYear(enrolledYear);
                student.setSemester(semester);
                student.setRoll(roll);
                // student.setProgram(...) // lookup or create

                studentRepository.save(student);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Excel file: " + e.getMessage());
        }
    }

}
