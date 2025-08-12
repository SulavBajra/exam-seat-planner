package com.example.examseatplanner.service;

import com.example.examseatplanner.dto.StudentRequestDTO;
import com.example.examseatplanner.dto.StudentResponseDTO;
import com.example.examseatplanner.mapper.StudentMapper;
import com.example.examseatplanner.model.Exam;
import com.example.examseatplanner.model.ExamProgramSemester;
import com.example.examseatplanner.model.Student;
import com.example.examseatplanner.model.Program;
import com.example.examseatplanner.repository.ProgramRepository;
import com.example.examseatplanner.repository.StudentRepository;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final ProgramRepository programRepository;
    private final StudentMapper studentMapper;

    @Autowired
    public StudentService(StudentRepository studentRepository,
                          ProgramRepository programRepository,
                          StudentMapper studentMapper) {
        this.studentRepository = studentRepository;
        this.programRepository = programRepository;
        this.studentMapper = studentMapper;
    }

    public StudentResponseDTO createStudent(StudentRequestDTO dto) {
        Program program = programRepository.findByProgramCode(dto.programCode())
                .orElseThrow(() -> new RuntimeException("Program not found"));

        Student student = studentMapper.toEntity(dto, program);
        Student savedStudent = studentRepository.save(student);
        return studentMapper.toDTO(savedStudent);
    }

    public List<StudentResponseDTO> getAllStudents() {
        List<Student> students = studentRepository.findAll();
        return studentMapper.toDTOList(students);
    }

    public List<Student> getStudentsForExam(Exam exam) {
        List<Student> allStudents = new ArrayList<>();

        for (ExamProgramSemester eps : exam.getProgramSemesters()) {
            List<Student> students = studentRepository
                    .findByProgramAndSemester(eps.getProgram(), eps.getSemester());
            allStudents.addAll(students);
        }

        return allStudents;
    }

    public Optional<Student> getStudentById(String studentId) {
        return studentRepository.findById(studentId);
    }

    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    public void deleteStudent(String studentId) {
        studentRepository.deleteById(studentId);
    }

    public List<Student> getStudentsByPrograms(List<Program> programs) {
        return studentRepository.findByProgramIn(programs);
    }

    public List<Student> getStudentsBySemester(int semester) {
        Student.Semester semesterEnum = toSemesterEnum(semester);
        return studentRepository.findBySemester(semesterEnum);
    }

    public boolean isRollNumberTaken(int roll, Program program, int semester) {
        Student.Semester semesterEnum = toSemesterEnum(semester);
        return studentRepository.existsByRollAndProgramAndSemester(roll, program, semesterEnum);
    }

    public Student.Semester toSemesterEnum(int semester) {
        if (semester < 1 || semester > 8) {
            throw new IllegalArgumentException("Semester must be between 1 and 8");
        }
        return Student.Semester.values()[semester - 1];
    }

    public List<Student> getStudentsByProgramCodeAndSemester(Integer programCode, int semester) {
        Student.Semester semesterEnum = toSemesterEnum(semester);
        return studentRepository.findByProgramCodeAndSemester(programCode, semesterEnum);
    }


    @Transactional
    public void importStudentsFromExcel(MultipartFile file) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Skip header
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    String programName = row.getCell(0).getStringCellValue().trim();
                    int semester = (int) row.getCell(1).getNumericCellValue();
                    int roll = (int) row.getCell(2).getNumericCellValue();

                    Program program = programRepository.findByProgramName(programName)
                            .orElseThrow(() -> new RuntimeException("Program not found: " + programName));

                    Student.Semester semesterEnum = Student.Semester.values()[semester - 1];

                    boolean exists = studentRepository.existsByRollAndProgramAndSemester(roll, program, semesterEnum);
                    if (exists) {
                        continue;  // skip duplicates
                    }

                    StudentRequestDTO dto = new StudentRequestDTO(program.getProgramCode(), semester, roll);
                    createStudent(dto);  // saves student immediately

                } catch (Exception e) {
                    System.err.printf("Error importing row %d: %s%n", i + 1, e.getMessage());
                    // Decide whether to continue or abort import on error
                }
            }
        }
    }



    public StudentResponseDTO convertToDTO(Student student) {
        return studentMapper.toDTO(student);
    }

    public List<StudentResponseDTO> convertToDTOList(List<Student> students) {
        return studentMapper.toDTOList(students);
    }

    public StudentResponseDTO updateStudent(String studentId, StudentRequestDTO dto) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Program program = programRepository.findByProgramCode(dto.programCode())
                .orElseThrow(() -> new RuntimeException("Program not found"));

        student.setProgram(program);
        student.setSemester(Student.Semester.values()[dto.semester() - 1]);
        student.setRoll(dto.roll());

        Student updated = studentRepository.save(student);
        return studentMapper.toDTO(updated);
    }

    public boolean validateStudentForSeatAssignment(Student student) {
        return student != null &&
                student.getStudentId() != null &&
                student.getProgram() != null;
    }


    public List<Student> createBulkStudents(Program program, String enrolledYear, int semester, int numberOfStudents) {
        List<Student> students = new java.util.ArrayList<>();
        int startingRoll = getNextAvailableRoll(program, semester);

        for (int i = 0; i < numberOfStudents; i++) {
            int roll = startingRoll + i;
            Student.Semester semesterEnum = Student.Semester.values()[semester - 1];
            Student student = new Student(program, semesterEnum, roll);
            students.add(saveStudent(student));
        }

        return students;
    }

    private int getNextAvailableRoll(Program program, int semester) {
        Student.Semester semesterEnum = toSemesterEnum(semester);
        List<Student> students = studentRepository.findByProgramAndSemester(program, semesterEnum);

        if (students.isEmpty()) {
            return 1;
        }

        return students.stream()
                .mapToInt(Student::getRoll)
                .max()
                .orElse(0) + 1;
    }
}
