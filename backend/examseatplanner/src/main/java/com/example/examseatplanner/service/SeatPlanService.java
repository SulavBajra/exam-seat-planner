package com.example.examseatplanner.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.springframework.stereotype.Service;

import com.example.examseatplanner.dto.ExamDataDTO;
import com.example.examseatplanner.dto.ProgramResponseDTO;
import com.example.examseatplanner.dto.RoomPlanDTO;
import com.example.examseatplanner.dto.RoomResponseDTO;
import com.example.examseatplanner.dto.SeatAssignmentDTO;
import com.example.examseatplanner.dto.StudentDTO;
import com.example.examseatplanner.model.Exam;
import com.example.examseatplanner.model.Room;
import com.example.examseatplanner.model.SeatingPlan;
import com.example.examseatplanner.model.Student;
import com.example.examseatplanner.repository.ExamRepository;
import com.example.examseatplanner.repository.RoomRepository;
import com.example.examseatplanner.repository.SeatingPlanRepository;
import com.example.examseatplanner.repository.StudentRepository;

import jakarta.transaction.Transactional;

@Service
public class SeatPlanService {

    private final ExamDataService examDataService;
    private final ExamRepository examRepository;
    private final RoomRepository roomRepository;
    private final StudentRepository studentRepository;
    private final SeatingPlanRepository seatingPlanRepository;
    private final StudentService studentService;

    public SeatPlanService(ExamDataService examDataService,
    ExamRepository examRepository,
    RoomRepository roomRepository,
    StudentRepository studentRepository,
    SeatingPlanRepository seatingPlanRepository,
    StudentService studentService){
        this.examDataService = examDataService;
        this.examRepository = examRepository;
        this.roomRepository = roomRepository;
        this.studentRepository = studentRepository;
        this.seatingPlanRepository = seatingPlanRepository;
        this.studentService = studentService;
    }

    @Transactional
    public List<RoomPlanDTO> generateAndSaveSeatingPlan(Integer examId) {
        ExamDataDTO examData = examDataService.getExamData(examId);
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        // Clear previous plan if exists
        seatingPlanRepository.deleteById(examId);

        List<ProgramResponseDTO> programs = examData.programs();
        List<RoomResponseDTO> rooms = examData.rooms();
        List<StudentDTO> students = examData.students();

        // Group students by program
        Map<String, Queue<StudentDTO>> programQueues = new HashMap<>();
        for (ProgramResponseDTO p : programs) {
            List<StudentDTO> group = students.stream()
                    .filter(s -> s.programCode().equals(p.programCode()))
                    .toList();
            programQueues.put(p.programCode().toString(), new LinkedList<>(group));
        }

        List<RoomPlanDTO> roomPlans = new ArrayList<>();

        for (RoomResponseDTO roomDTO : rooms) {
            Room room = roomRepository.findByRoomNo(roomDTO.roomNo())
                    .orElseThrow(() -> new RuntimeException("Room not found: " + roomDTO.roomNo()));

            int numCols = roomDTO.roomColumn() * roomDTO.seatsPerBench();
            int numRows = roomDTO.numRow();

            List<List<SeatAssignmentDTO>> grid = new ArrayList<>();

            for (int row = 0; row < numRows; row++) {
                List<SeatAssignmentDTO> rowSeats = new ArrayList<>();

                for (int col = 0; col < numCols; col++) {
                    StudentDTO chosen = null;

                    for (ProgramResponseDTO program : programs) {
                        Queue<StudentDTO> queue = programQueues.get(program.programCode().toString());
                        if (queue == null || queue.isEmpty()) continue;

                        SeatAssignmentDTO leftNeighbor = (col > 0 && !rowSeats.isEmpty())
                                ? rowSeats.get(col - 1)
                                : null;
                        if (leftNeighbor == null ||
                            !leftNeighbor.getProgramCode().equals(program.programCode().toString())) {
                            chosen = queue.poll();
                            break;
                        }
                    }

                    SeatAssignmentDTO seat = null;
                    if (chosen != null) {
                        seat = new SeatAssignmentDTO(
                                chosen.programCode().toString(),
                                chosen.semester(),
                                chosen.roll(),
                                row + 1,
                                col + 1
                        );

                        Student.Semester semesterEnum = studentService.toSemesterEnum(chosen.semester());

                        // Fetch the actual student entity (optional but preferred)
                        Student studentEntity = studentRepository
                                .findByProgramCodeAndSemesterAndRoll(
                                        chosen.programCode(), 
                                        semesterEnum, 
                                        chosen.roll())
                                .orElse(null);

                        seatingPlanRepository.save(new SeatingPlan(
                                exam, room, studentEntity,
                                Integer.valueOf(row + 1), 
                                Integer.valueOf(col + 1),
                                chosen.programCode().toString(), 
                                Integer.valueOf(chosen.semester()),
                                Integer.valueOf(chosen.roll())
                        ));
                    }

                    rowSeats.add(seat);
                }
                grid.add(rowSeats);
            }

            roomPlans.add(new RoomPlanDTO(roomDTO.roomNo().toString(), grid));
        }

        return roomPlans;
    }

    public List<SeatingPlan> getSavedSeatingPlan(Integer examId){
        return seatingPlanRepository.findByExamId(examId);
    }

    public SeatingPlan searchStudentSeat(Integer examId, String programCode, Integer semester, Integer roll) {
        return seatingPlanRepository.findByExamIdAndProgramCodeAndSemesterAndRoll(
                examId, programCode, semester, roll
        ).orElseThrow(() -> new RuntimeException("Student seat not found"));
    }

}

