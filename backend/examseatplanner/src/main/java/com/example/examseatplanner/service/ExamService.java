package com.example.examseatplanner.service;

import com.example.examseatplanner.dto.ExamRequestDTO;
import com.example.examseatplanner.dto.ExamResponseDTO;
import com.example.examseatplanner.model.Exam;
import com.example.examseatplanner.model.Room;
import com.example.examseatplanner.model.Student;
import com.example.examseatplanner.model.Subject;
import com.example.examseatplanner.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ExamService {

    private final ExamRepository examRepository;
    private final RoomRepository roomRepository;
    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;
    private final ImprovedSeatAllocationService seatAllocationService;

    @Autowired
    public ExamService(ExamRepository examRepository,
                       RoomRepository roomRepository,
                       SubjectRepository subjectRepository,
                       StudentRepository studentRepository,
                       ImprovedSeatAllocationService seatAllocationService) {
        this.examRepository = examRepository;
        this.roomRepository = roomRepository;
        this.subjectRepository = subjectRepository;
        this.studentRepository = studentRepository;
        this.seatAllocationService = seatAllocationService;
    }

    public void validateExamSchedule(ExamRequestDTO dto) {
        LocalDate examDate = LocalDate.parse(dto.date());

        // Check for room conflicts on the same date
        List<Exam> conflictingExams = examRepository.findByDate(examDate).stream()
                .filter(exam -> exam.getRooms().stream()
                        .anyMatch(room -> dto.roomNumbers().contains(room.getRoomNo())))
                .toList();

        if (!conflictingExams.isEmpty()) {
            throw new IllegalArgumentException("Room conflict detected for the specified date");
        }

        // Get subject to determine students
        Subject subject = subjectRepository.findBySubjectCode(dto.subjectCode())
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        // Get students automatically based on subject's program and semester
        List<Student> eligibleStudents = studentRepository
                .findByProgramAndSemester(subject.getProgram(), subject.getSemester());

        // Check if rooms have enough capacity
        List<Room> rooms = roomRepository.findAllByRoomNoIn(dto.roomNumbers());
        int totalCapacity = rooms.stream().mapToInt(Room::getSeatingCapacity).sum();

        if (eligibleStudents.size() > totalCapacity) {
            throw new IllegalArgumentException(
                    String.format("Not enough seats: %d students, %d seats available",
                            eligibleStudents.size(), totalCapacity));
        }
    }
    // Add these methods to your ExamService class

    public List<Exam> getExamsByDate(String date) {
        LocalDate examDate = LocalDate.parse(date);
        return examRepository.findByDate(examDate);
    }

    public ExamResponseDTO updateExam(Integer examId, ExamRequestDTO dto) {
        // First validate the updated exam schedule
        validateExamSchedule(dto);

        // Find existing exam
        Exam existingExam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        // Update exam details
        Subject subject = subjectRepository.findBySubjectCode(dto.subjectCode())
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        List<Room> rooms = roomRepository.findAllByRoomNoIn(dto.roomNumbers());
        if (rooms.size() != dto.roomNumbers().size()) {
            throw new RuntimeException("Some rooms not found");
        }

        existingExam.setSubject(subject);
        existingExam.setDate(LocalDate.parse(dto.date()));
        existingExam.setRooms(rooms);

        Exam updatedExam = examRepository.save(existingExam);

        // Re-allocate seats with updated information
        List<Student> students = studentRepository
                .findByProgramAndSemester(subject.getProgram(), subject.getSemester());
        seatAllocationService.allocateSeats(updatedExam, students);

        return ExamResponseDTO.fromEntity(updatedExam);
    }

    public void deleteExam(Integer examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));
        examRepository.delete(exam);
    }

    public void allocateSeatsForExam(Integer examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        // Get students for this exam
        List<Student> students = getStudentsForExam(exam);

        // Allocate seats
        seatAllocationService.allocateSeats(exam, students);
    }

    public ExamResponseDTO createExam(ExamRequestDTO dto) {
        // Validate exam schedule
        validateExamSchedule(dto);

        Subject subject = subjectRepository.findBySubjectCode(dto.subjectCode())
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        // Get students automatically based on subject's program and semester
        List<Student> students = studentRepository
                .findByProgramAndSemester(subject.getProgram(), subject.getSemester());

        List<Room> rooms = roomRepository.findAllByRoomNoIn(dto.roomNumbers());
        if (rooms.size() != dto.roomNumbers().size()) {
            throw new RuntimeException("Some rooms not found");
        }

        Exam exam = new Exam();
        exam.setSubject(subject);
        exam.setDate(LocalDate.parse(dto.date()));
        exam.setRooms(rooms);

        Exam savedExam = examRepository.save(exam);

        // Pass students separately to seat allocation service
        seatAllocationService.allocateSeats(savedExam, students);

        return ExamResponseDTO.fromEntity(savedExam);
    }

    public List<ExamResponseDTO> getAllExams() {
        return examRepository.findAll().stream()
                .map(ExamResponseDTO::fromEntity)
                .toList();
    }

    public ExamResponseDTO getExamById(Integer id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exam not found"));
        return ExamResponseDTO.fromEntity(exam);
    }

    // Helper method to get students for an exam
    public List<Student> getStudentsForExam(Exam exam) {
        Subject subject = exam.getSubject();
        return studentRepository.findByProgramAndSemester(
                subject.getProgram(),
                subject.getSemester()
        );
    }
}