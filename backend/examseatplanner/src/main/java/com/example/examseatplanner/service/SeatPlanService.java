package com.example.examseatplanner.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.examseatplanner.dto.*;
import com.example.examseatplanner.mapper.SeatPlanMapper;
import com.example.examseatplanner.model.*;
import com.example.examseatplanner.repository.*;

import jakarta.transaction.Transactional;

@Service
public class SeatPlanService {

    private final ExamDataService examDataService;
    private final SeatingPlanRepository seatingPlanRepository;

    public SeatPlanService(
            ExamDataService examDataService,
            SeatingPlanRepository seatingPlanRepository){
        this.examDataService = examDataService;
        this.seatingPlanRepository = seatingPlanRepository;
    }


   @Transactional
    public void generateAndSaveSeatingPlan(Integer examId) {

        ExamDataDTO examDataDTO = examDataService.getExamData(examId);

        List<ProgramResponseDTO> programList = examDataDTO.programs();
        List<StudentDTO> studentList = examDataDTO.students();
        List<RoomResponseDTO> roomList = examDataDTO.rooms();

        Map<Integer, Queue<StudentDTO>> programQueuesOriginal = new LinkedHashMap<>();
        Map<Integer, Queue<StudentDTO>> programQueues = programQueuesOriginal.entrySet()
                                                .stream()
                                                .collect(Collectors.toMap(
                                                    Map.Entry::getKey,
                                                    e -> new LinkedList<>(e.getValue()),
                                                    (a, b) -> a,
                                                    LinkedHashMap::new
                                                ));


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

            @SuppressWarnings("unchecked")
            Queue<StudentDTO>[] laneProgram = new Queue[seatsPerBench];

            for (int lane = 0; lane < seatsPerBench; lane++) {
                if (!remainingPrograms.isEmpty()) {
                    laneProgram[lane] = remainingPrograms.remove(0);
                } else {
                    laneProgram[lane] = new LinkedList<>();
                }
            }

            for (int col = 0; col < cols; col++) {
                for (int row = 0; row < rows; row++) {

                    for (int lane = 0; lane < seatsPerBench; lane++) {

                        Queue<StudentDTO> currentQueue = laneProgram[lane];

                        if (currentQueue.isEmpty()) {
                            if (!remainingPrograms.isEmpty()) {
                                currentQueue = remainingPrograms.remove(0);
                                laneProgram[lane] = currentQueue;
                            } else {
                                continue; 
                            }
                        }

                        StudentDTO student = currentQueue.poll();
                        if (student == null) continue;
                            System.out.println(
                            "Assign => examId=" + examId +
                            " room=" + roomNo +
                            " row=" + row +
                            " col=" + col +
                            " lane=" + lane +
                            " program=" + student.programCode() +
                            " roll=" + student.roll()
                        );

                        SeatingPlan seat = new SeatingPlan(
                                null,
                                examId,
                                roomNo,
                                row+1,
                                col+1,
                                student.programCode().toString(),
                                student.semester(),
                                student.roll(),
                                lane+1
                        );

                        saveList.add(seat);
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

   public SeatAssignmentDTO searchStudentSeat(Integer examId, String programCode, Integer semester, Integer roll) {
        SeatingPlan seat = seatingPlanRepository
                .findByExamIdAndProgramCodeAndSemesterAndRoll(examId, programCode, semester, roll)
                .orElseThrow(() -> new RuntimeException("Student seat not found"));

        return SeatPlanMapper.toDTO(seat); 
    }

}
