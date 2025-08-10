package com.example.examseatplanner.mapper;

import com.example.examseatplanner.dto.StudentRequestDTO;
import com.example.examseatplanner.dto.StudentResponseDTO;
import com.example.examseatplanner.model.Program;
import com.example.examseatplanner.model.Student;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StudentMapper {

    public Student toEntity(StudentRequestDTO dto, Program program) {
        return new Student(
                program,
                Student.Semester.values()[dto.semester() - 1],
                dto.roll()
        );
    }

    public StudentResponseDTO toDTO(Student student) {
        return new StudentResponseDTO(
                student.getStudentId(),
                student.getSemester().ordinal() + 1,
                student.getRoll(),
                student.getProgram().getProgramName(),
                student.getProgram().getProgramCode()
        );
    }

    public List<StudentResponseDTO> toDTOList(List<Student> students) {
        return students.stream().map(this::toDTO).toList();
    }
}
