package com.example.examseatplanner.service;

import com.example.examseatplanner.dto.ExamRequestDTO;
import com.example.examseatplanner.dto.ExamResponseDTO;
import com.example.examseatplanner.dto.ProgramSemesterDTO;
import com.example.examseatplanner.exception.ExceedsRoomCapacityException;
import com.example.examseatplanner.exception.NoStudentException;
import com.example.examseatplanner.exception.StudentAlreadyHasExamException;
import com.example.examseatplanner.mapper.ExamMapper;
import com.example.examseatplanner.model.Exam;
import com.example.examseatplanner.model.Program;
import com.example.examseatplanner.model.Room;
import com.example.examseatplanner.model.Student;
import com.example.examseatplanner.repository.ExamRepository;
import com.example.examseatplanner.repository.ProgramRepository;
import com.example.examseatplanner.repository.RoomRepository;
import com.example.examseatplanner.repository.SeatingPlanRepository;
import com.example.examseatplanner.repository.StudentRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExamService {

    private final ExamRepository examRepository;
    private final ProgramRepository programRepository;
    private final RoomRepository roomRepository;
    private final StudentRepository studentRepository;
    private final SeatingPlanRepository seatingPlanRepository;

    public ExamService(ExamRepository examRepository,
                       ProgramRepository programRepository,
                       RoomRepository roomRepository,
                       StudentRepository studentRepository,
                       SeatingPlanRepository seatingPlanRepository) {
        this.examRepository = examRepository;
        this.programRepository = programRepository;
        this.roomRepository = roomRepository;
        this.studentRepository = studentRepository;
        this.seatingPlanRepository = seatingPlanRepository;
    }

    public List<ExamResponseDTO> getAllExams() {
        return examRepository.findAll().stream().map(ExamMapper::toDto).toList();
    }

  public List<Integer> getBookedRoomsByDate(LocalDate startDate, LocalDate endDate) {
    return examRepository.findBookedRoomNumbersByDateRange(startDate, endDate);
  }

    // public List<Integer> getBookedRoomsByDate(LocalDate startDate, LocalDate endDate) {
    //     return examRepository.findAll().stream()
    //             .filter(exam -> exam.getStartDate().equals(startDate))
    //             .flatMap(exam -> exam.getRooms().stream())
    //             .map(Room::getRoomNo)
    //             .distinct()
    //             .collect(Collectors.toList());
    // }

    public Optional<Exam> getExamEntityById(Integer examId) {
        return examRepository.findById(examId);
    }


    public List<Program> getProgramByExamId(Integer examId) {
        Optional<Exam> examOpt = getExamEntityById(examId);
        if (examOpt.isPresent()) {
            Exam exam = examOpt.get();
            return exam.getPrograms();
        }
        return new ArrayList<>();
    }

   public boolean isRoomBooked(Integer roomNo, LocalDate startDate, LocalDate endDate) {
        return examRepository.isRoomOccupied(roomNo, startDate, endDate);
    }


    public Optional<ExamResponseDTO> getExamById(Integer examId) {
        return examRepository.findById(examId)
                .map(ExamMapper::toDto);
    }

    public Long getTotalStudentsForExam(Integer examId) {
        return studentRepository.countStudentsForExam(examId);
    }

    public boolean hasStudentExamConflict(List<ProgramSemesterDTO> programSemesters, LocalDate startDate, LocalDate endDate) {
    for (ProgramSemesterDTO ps : programSemesters) {
        Student.Semester semesterEnum = intToSemester(ps.semester());
        boolean conflict = examRepository.findExamsForProgramSemesterBetweenDates(
                ps.programCode(),
                semesterEnum,
                startDate,
                endDate
        ).size() > 0;

        if (conflict) return true;
    }
    return false;
}


    public void validateRoomCapacity(ExamRequestDTO request) {
        int totalStudents = request.programSemesters().stream()
                .mapToInt(ps -> {
                    Student.Semester semesterEnum = intToSemester(ps.semester());
                    return (int) studentRepository.countByProgramCodeAndSemester(
                            ps.programCode(),
                            semesterEnum);
                })
                .sum();

        List<Room> rooms = roomRepository.findAllById(request.roomNumbers());


        int totalRoomCapacity = rooms.stream()
                .mapToInt(Room::getSeatingCapacity)
                .sum();

        if (totalRoomCapacity < totalStudents) {
            Map<String, Integer> studentCounts = request.programSemesters().stream()
                    .collect(Collectors.toMap(
                            ps -> "Program " + ps.programCode() + " Semester " + ps.semester(),
                            ps -> {
                                Student.Semester semesterEnum = intToSemester(ps.semester());
                                return (int) studentRepository.countByProgramCodeAndSemester(
                                        ps.programCode(), semesterEnum);
                            }
                    ));
            throw new ExceedsRoomCapacityException("The number of students is more than the capacity of room"+
            "Room Capacity: "+totalRoomCapacity+
            "Student Count: "+studentCounts);
        }
    }

    public ExamResponseDTO createExamFromDto(ExamRequestDTO dto) {
        List<Integer> programCodes = dto.programSemesters().stream()
                .map(ProgramSemesterDTO::programCode)
                .distinct()
                .toList();
        validateRoomCapacity(dto);

        List<Program> programs = programRepository.findAllById(programCodes);
        if (programs.size() != programCodes.size()) {
            throw new IllegalArgumentException("One or more program codes are invalid");
        }

        List<Room> rooms = roomRepository.findAllById(dto.roomNumbers());
        if (rooms.size() != dto.roomNumbers().size()) {
            throw new IllegalArgumentException("One or more room numbers are invalid");
        }

        long totalStudents = 0;
        for (ProgramSemesterDTO ps : dto.programSemesters()) {
            int semesterInt = ps.semester();
            Student.Semester semesterEnum = intToSemester(semesterInt);

            totalStudents += studentRepository.countByProgramCodeAndSemester(ps.programCode(), semesterEnum);
        }

        if (hasStudentExamConflict(dto.programSemesters(),LocalDate.parse(dto.startDate()),LocalDate.parse(dto.endDate()))) {
            throw new StudentAlreadyHasExamException("One or more students already have an exam scheduled in this date range"+dto.startDate()+dto.endDate());
        }

        if (totalStudents == 0) {
            throw new NoStudentException("No students found");
        }

        int totalCapacity = rooms.stream()
                .mapToInt(Room::getSeatingCapacity)
                .sum();

        if (totalStudents > totalCapacity) {
            throw new ExceedsRoomCapacityException("Room capacity not enough");
        }



        Exam exam = ExamMapper.toEntity(dto, programs, rooms);
        Exam savedExam = examRepository.save(exam);
        return ExamMapper.toDto(savedExam);
    }
    

    public Student.Semester intToSemester(int sem) {
        Student.Semester[] semesters = Student.Semester.values();
        if (sem < 1 || sem > semesters.length) {
            throw new IllegalArgumentException("Invalid semester number: " + sem);
        }
        return semesters[sem - 1];
    }

    public ExamResponseDTO updateExamFromDto(Integer examId, ExamRequestDTO dto) {
        Optional<Exam> existingExamOpt = examRepository.findById(examId);
        if (existingExamOpt.isEmpty()) {
            throw new NoSuchElementException("Exam not found");
        }

        List<Integer> programCodes = dto.programSemesters().stream()
                .map(ProgramSemesterDTO::programCode)
                .distinct()
                .toList();

        List<Program> programs = programRepository.findAllById(programCodes);
        if (programs.size() != programCodes.size()) {
            throw new IllegalArgumentException("One or more program codes are invalid");
        }

        List<Room> rooms = roomRepository.findAllById(dto.roomNumbers());
        if (rooms.size() != dto.roomNumbers().size()) {
            throw new IllegalArgumentException("One or more room numbers are invalid");
        }

        Exam exam = ExamMapper.toEntity(dto, programs, rooms);
        exam.setId(examId);

        Exam savedExam = examRepository.save(exam);
        return ExamMapper.toDto(savedExam);
    }

    @Transactional
    public boolean deleteExam(Integer examId) {
        if (!examRepository.existsById(examId)) {
            return false;
        }
        seatingPlanRepository.deleteByExamId(examId);
        examRepository.deleteById(examId);
        return true;
    }

    public List<Exam> getExamsByDateRange(LocalDate startDate, LocalDate endDate) {
        return examRepository.findOverlappingExams(startDate, endDate);
    }

    public List<Exam> getExamsByProgramCode(Integer programCode) {
        return examRepository.findByProgramCode(programCode);
    }
}
