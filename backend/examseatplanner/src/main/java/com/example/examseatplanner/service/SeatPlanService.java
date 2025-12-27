package com.example.examseatplanner.service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.examseatplanner.dto.*;
import com.example.examseatplanner.exception.ExceedsRoomCapacityException;
import com.example.examseatplanner.mapper.SeatPlanMapper;
import com.example.examseatplanner.model.*;
import com.example.examseatplanner.repository.*;


@Service
public class SeatPlanService {

    private final ExamDataService examDataService;
    private final SeatingPlanRepository seatingPlanRepository;
    private final ExamRepository examRepository;

    public SeatPlanService(
            ExamDataService examDataService,
            SeatingPlanRepository seatingPlanRepository,
            ExamRepository examRepository){
        this.examDataService = examDataService;
        this.seatingPlanRepository = seatingPlanRepository;
        this.examRepository = examRepository;
    }


    public void generateAndSaveSeatingPlan(Integer examId) {

        ExamDataDTO examDataDTO = examDataService.getExamData(examId);

        List<ProgramResponseDTO> programList = examDataDTO.programs();
        List<StudentDTO> studentList = examDataDTO.students();
        List<RoomResponseDTO> roomList = examDataDTO.rooms();

        Map<Integer, Queue<StudentDTO>> programQueues = new LinkedHashMap<>();
        // Map<Integer, Queue<StudentDTO>> programQueues = programQueuesOriginal.entrySet()
        //                                         .stream()
        //                                         .collect(Collectors.toMap(
        //                                             Map.Entry::getKey,
        //                                             e -> npew LinkedList<>(e.getValue()),
        //                                             (a, b) -> a,
        //                                             LinkedHashMap::new
        //                                         ));
        for (ProgramResponseDTO p : programList) {
            Queue<StudentDTO> q = new LinkedList<>(
                    studentList.stream()
                            .filter(s -> s.programCode().equals(p.programCode()))
                            .toList()
            );
            programQueues.put(p.programCode(), q);
        }

        List<SeatingPlan> saveList = new ArrayList<>();

        for (RoomResponseDTO room : roomList) {
            List<Queue<StudentDTO>> remainingPrograms = new ArrayList<>(programQueues.values());
            int rows = room.numRow();
            int cols = room.roomColumn();
            int seatsPerBench = room.seatsPerBench();
            String roomNo = room.roomNo().toString();

            Queue<StudentDTO>[] seatProgram = new Queue[seatsPerBench];

            for (int seat = 0; seat < seatsPerBench; seat++) {
                if (!remainingPrograms.isEmpty()) {
                    seatProgram[seat] = remainingPrograms.remove(0);
                } else {
                    seatProgram[seat] = new LinkedList<>();
                }
            }

            
            for (int col = 0; col < cols; col++) {
                for (int row = 0; row < rows; row++) {

                    for (int seat = 0; seat < seatsPerBench; seat++) {

                        Queue<StudentDTO> currentQueue = seatProgram[seat];

                        if (currentQueue.isEmpty()) {
                            if (!remainingPrograms.isEmpty()) {
                                currentQueue = remainingPrograms.remove(0);
                                seatProgram[seat] = currentQueue;
                            } else {
                                continue; 
                            }
                        }

                        StudentDTO student = currentQueue.poll();
                        if (student == null) continue;
                            
                        SeatingPlan seatingPlan = new SeatingPlan(
                                null,
                                examId,
                                roomNo,
                                row+1,
                                col+1,
                                student.programCode().toString(),
                                student.semester(),
                                student.roll(),
                                seat+1
                        );

                        saveList.add(seatingPlan);
                    }
                }
            }
        }
        seatingPlanRepository.saveAll(saveList);
    }

    public List<RoomPlanDTO> getSavedSeatingPlanGroupedByRoom(Integer examId) {
        List<SeatingPlan> plans = seatingPlanRepository.findByExamId(examId);

        Map<String, List<SeatingPlan>> grouped = plans.stream()
                .collect(Collectors.groupingBy(sp -> sp.getRoomNo()));

        List<RoomPlanDTO> roomPlans = new ArrayList<>();

        for (Map.Entry<String, List<SeatingPlan>> entry : grouped.entrySet()) {
            String roomNo = entry.getKey();
            List<SeatingPlan> roomSeats = entry.getValue();

            int maxRow = roomSeats.stream().mapToInt(SeatingPlan::getRowNumber).max().orElse(0);
            int maxCol = roomSeats.stream().mapToInt(SeatingPlan::getColumnNumber).max().orElse(0);

            List<List<SeatAssignmentDTO>> grid = new ArrayList<>();
            for (int r = 0; r < maxRow; r++) {
                grid.add(new ArrayList<>(Collections.nCopies(maxCol, null)));
            }

            for (SeatingPlan sp : roomSeats) {
                grid.get(sp.getRowNumber() - 1).set(sp.getColumnNumber() - 1, SeatPlanMapper.toDTO(sp));
            }

            roomPlans.add(new RoomPlanDTO(roomNo, grid));
        }

        return roomPlans;
    }


    public List<SeatingPlan> getSavedSeatingPlan(Integer examId) {
        return seatingPlanRepository.findByExamId(examId);
    }

   public SeatAssignmentDTO searchStudentSeat(LocalDate startDate,LocalDate endDate, String programCode, Integer semester, Integer roll) {
        Exam exam = examRepository.findExamIdByStartDateAndEndDate(startDate, endDate)
                                .orElseThrow(()->new RuntimeException("Exam in that date not found"));
        SeatingPlan seat = seatingPlanRepository
                .findByExamIdAndProgramCodeAndSemesterAndRoll(exam.getId(), programCode, semester, roll)
                .orElseThrow(() -> new RuntimeException("Student seat not found"));

        return SeatPlanMapper.toDTO(seat); 
    }

}
