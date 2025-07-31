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

import java.util.List;

@Service
public class ExamService {

    private final ExamRepository examRepository;
    private final RoomRepository roomRepository;
    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;
    private final SeatAllocationService seatAllocationService;

    @Autowired
    public ExamService(ExamRepository examRepository,
                       RoomRepository roomRepository,
                       SubjectRepository subjectRepository,
                       StudentRepository studentRepository,
                       SeatAllocationService seatAllocationService){
        this.examRepository = examRepository;
        this.roomRepository =  roomRepository;
        this.subjectRepository = subjectRepository;
        this.studentRepository = studentRepository;
        this.seatAllocationService =seatAllocationService;
    }

    // Add this method to ExamService
    public void validateExamSchedule(ExamRequestDTO dto) {
        // Check for room conflicts
        List<Exam> conflictingExams = examRepository.findByDate(dto.date()).stream()
                .filter(exam -> exam.getTime().equals(dto.time()))
                .filter(exam -> exam.getRooms().stream()
                        .anyMatch(room -> dto.roomIds().contains(room.getRoomNo())))
                .toList();

        if (!conflictingExams.isEmpty()) {
            throw new IllegalArgumentException("Room conflict detected for the specified date and time");
        }

        // Check if students have capacity
        List<Room> rooms = roomRepository.findAllByRoomNoIn(dto.roomIds());
        int totalCapacity = rooms.stream().mapToInt(Room::getSeatingCapacity).sum();

        if (dto.studentIds().size() > totalCapacity) {
            throw new IllegalArgumentException(
                    String.format("Not enough seats: %d students, %d seats available",
                            dto.studentIds().size(), totalCapacity));
        }
    }

    // Update createExam method to include validation
    public ExamResponseDTO createExam(ExamRequestDTO dto) {
        // Validate exam schedule
        validateExamSchedule(dto);

        Subject subject = subjectRepository.findBySubjectCode(dto.subjectCode())
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        List<Student> students = studentRepository.findAllById(dto.studentIds());
        if (students.size() != dto.studentIds().size()) {
            throw new RuntimeException("Some students not found");
        }

        List<Room> rooms = roomRepository.findAllByRoomNoIn(dto.roomIds());
        if (rooms.size() != dto.roomIds().size()) {
            throw new RuntimeException("Some rooms not found");
        }

        Exam exam = new Exam();
        exam.setSubject(subject);
        exam.setDate(dto.date());
        exam.setTime(dto.time());
        exam.setStudents(students);
        exam.setRooms(rooms);

        Exam savedExam = examRepository.save(exam);
        seatAllocationService.allocateSeatsStudentId(savedExam);

        return ExamResponseDTO.fromEntity(savedExam);
    }
}
