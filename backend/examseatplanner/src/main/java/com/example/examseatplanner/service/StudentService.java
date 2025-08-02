package com.example.examseatplanner.service;

import com.example.examseatplanner.dto.StudentRequestDTO;
import com.example.examseatplanner.dto.StudentResponseDTO;
import com.example.examseatplanner.model.Program;
import com.example.examseatplanner.model.Student;
import com.example.examseatplanner.model.Subject;
import com.example.examseatplanner.repository.ProgramRepository;
import com.example.examseatplanner.repository.StudentRepository;
import com.example.examseatplanner.repository.SubjectRepository;
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
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final ProgramRepository programRepository;
    private final SubjectRepository subjectRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository,
                          ProgramRepository programRepository,
                          SubjectRepository subjectRepository) {
        this.studentRepository = studentRepository;
        this.programRepository = programRepository;
        this.subjectRepository = subjectRepository;
    }

    public Optional<StudentResponseDTO> findByStudentId(String studentId) {
        return studentRepository.findByStudentId(studentId)
                .map(StudentResponseDTO::fromEntity);
    }

    public List<StudentResponseDTO> findAllStudents() {
        List<Student> students = studentRepository.findAll();
        return students.stream()
                .map(StudentResponseDTO::fromEntity)
                .toList();
    }

    public boolean existsByStudentId(String studentId) {
        return studentRepository.existsByStudentId(studentId);
    }

    public String generateStudentCode(String enrolledYear, int programCode, int roll) {
        return String.format("%04d%02d%02d", Integer.parseInt(enrolledYear), programCode, roll);
    }

    public StudentResponseDTO registerFromDTO(StudentRequestDTO dto) {
        Program program = programRepository.findById(dto.programCode())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Program Not Found: " + dto.programCode()));

        String studentId = generateStudentCode(dto.enrolledYear(), program.getProgramCode(), dto.roll());

        if (existsByStudentId(studentId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Student with ID already exists");
        }

        Student student = new Student(program, dto.enrolledYear(), dto.semester(), dto.roll());
        student.setStudentId(studentId);

        Student savedStudent = studentRepository.save(student);
        return StudentResponseDTO.fromEntity(savedStudent);
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
                int programCode = (int) row.getCell(4).getNumericCellValue();

                Program program = programRepository.findById(programCode)
                        .orElseThrow(() -> new RuntimeException("Program not found: " + programCode));

                Student student = new Student();
                student.setStudentId(studentId);
                student.setEnrolledYear(enrolledYear);
                student.setSemester(semester);
                student.setRoll(roll);
                student.setProgram(program);

                studentRepository.save(student);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Excel file: " + e.getMessage());
        }
    }

    // Helper method to get subjects for a student
    public List<Subject> getSubjectsForStudent(Student student) {
        return subjectRepository.findByProgramAndSemester(
                student.getProgram(),
                student.getSemester()
        );
    }
}