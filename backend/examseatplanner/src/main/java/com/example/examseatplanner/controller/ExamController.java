package com.example.examseatplanner.controller;

import com.example.examseatplanner.dto.ExamRequestDTO;
import com.example.examseatplanner.dto.ExamResponseDTO;
import com.example.examseatplanner.dto.ProgramSemesterDTO;
import com.example.examseatplanner.mapper.ExamMapper;
import com.example.examseatplanner.model.Exam;
import com.example.examseatplanner.model.Program;
import com.example.examseatplanner.model.Room;
import com.example.examseatplanner.service.ExamService;
import com.example.examseatplanner.service.ProgramService;
import com.example.examseatplanner.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/exams")
public class ExamController {

    private final ExamService examService;
    private final ProgramService programService;
    private final RoomService roomService;  // Inject this for room lookup

    @Autowired
    public ExamController(ExamService examService,
                          ProgramService programService,
                          RoomService roomService) {
        this.examService = examService;
        this.programService = programService;
        this.roomService = roomService;
    }

    @GetMapping
    public List<Exam> getAllExams() {
        return examService.getAllExams();
    }

    @GetMapping("/{examId}")
    public ResponseEntity<Exam> getExamById(@PathVariable Integer examId) {
        Optional<Exam> exam = examService.getExamById(examId);
        return exam.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ExamResponseDTO> createExam(@RequestBody @Valid ExamRequestDTO dto) {
        // ✅ Extract unique program codes
        List<Integer> programCodes = dto.programSemesters().stream()
                .map(ProgramSemesterDTO::programCode)
                .distinct()
                .collect(Collectors.toList());

        List<Program> programs = programService.findAllById(programCodes);
        if (programs.size() != programCodes.size()) {
            return ResponseEntity.badRequest().build();
        }

        List<Room> rooms = roomService.findAllById(dto.roomNumbers());
        if (rooms.size() != dto.roomNumbers().size()) {
            return ResponseEntity.badRequest().build();
        }

        Exam exam = ExamMapper.toEntity(dto, programs, rooms);
        Exam savedExam = examService.saveExam(exam);
        ExamResponseDTO responseDTO = ExamMapper.toDto(savedExam);

        return ResponseEntity.created(URI.create("/api/exams/" + savedExam.getId()))
                .body(responseDTO);
    }

    @PutMapping("/{examId}")
    public ResponseEntity<ExamResponseDTO> updateExam(@PathVariable Integer examId,
                                                      @RequestBody @Valid ExamRequestDTO dto) {
        Optional<Exam> existingExamOpt = examService.getExamById(examId);
        if (existingExamOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // ✅ Extract unique program codes (same as createExam)
        List<Integer> programCodes = dto.programSemesters().stream()
                .map(ProgramSemesterDTO::programCode)
                .distinct()
                .collect(Collectors.toList());

        List<Program> programs = programService.findAllById(programCodes);
        if (programs.size() != programCodes.size()) {
            return ResponseEntity.badRequest().build();
        }

        List<Room> rooms = roomService.findAllById(dto.roomNumbers());
        if (rooms.size() != dto.roomNumbers().size()) {
            return ResponseEntity.badRequest().build();
        }

        Exam updatedExam = ExamMapper.toEntity(dto, programs, rooms);
        updatedExam.setId(examId);

        Exam savedExam = examService.saveExam(updatedExam);
        ExamResponseDTO responseDTO = ExamMapper.toDto(savedExam);

        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{examId}")
    public ResponseEntity<Void> deleteExam(@PathVariable Integer examId) {
        if (examService.getExamById(examId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        examService.deleteExam(examId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/date/{date}")
    public List<Exam> getExamsByDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return examService.getExamsByDate(date);
    }

    @GetMapping("/program/{programCode}")
    public List<Exam> getExamsByProgramCode(@PathVariable Integer programCode) {
        return examService.getExamsByProgramCode(programCode);
    }

    @GetMapping("/date-range")
    public List<Exam> getExamsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return examService.getExamsByDateRange(startDate, endDate);
    }
}
