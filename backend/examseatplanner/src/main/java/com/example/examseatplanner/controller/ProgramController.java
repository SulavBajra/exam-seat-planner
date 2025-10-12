package com.example.examseatplanner.controller;

import com.example.examseatplanner.dto.ProgramRequestDTO;
import com.example.examseatplanner.dto.ProgramResponseDTO;
import com.example.examseatplanner.service.ProgramService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/programs")
public class ProgramController {

    private final ProgramService programService;

    public ProgramController(ProgramService programService) {
        this.programService = programService;
    }

    @GetMapping
    public List<ProgramResponseDTO> getAllPrograms() {
        return programService.getAllPrograms();
    }

    @GetMapping("/{programCode}")
    public ResponseEntity<ProgramResponseDTO> getProgramById(@PathVariable Integer programCode) {
        return programService.getProgramById(programCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ProgramResponseDTO createProgram(@RequestBody ProgramRequestDTO programRequestDTO) {
        return programService.saveProgram(programRequestDTO);
    }

    @PutMapping("/{programCode}")
    public ResponseEntity<ProgramResponseDTO> updateProgram(@PathVariable Integer programCode,
                                                            @RequestBody ProgramRequestDTO programRequestDTO) {
        if (programService.getProgramById(programCode).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Create a new DTO with path variable programCode to avoid mismatch
        ProgramRequestDTO dtoWithCorrectCode = new ProgramRequestDTO(
                programRequestDTO.programName(),
                programCode
        );

        ProgramResponseDTO updatedProgram = programService.saveProgram(dtoWithCorrectCode);
        return ResponseEntity.ok(updatedProgram);
    }


    @DeleteMapping("/{programCode}")
    public ResponseEntity<Void> deleteProgram(@PathVariable Integer programCode) {
        if (programService.getProgramById(programCode).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        programService.deleteProgram(programCode);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<ProgramResponseDTO> searchPrograms(@RequestParam String name) {
        return programService.searchProgramsByName(name);
    }

    @GetMapping("/search/code")
    public List<Integer> getProgramCodeByProgramName(@RequestParam String name){
        return  programService.getProgramCodeByProgramName(name);
    }
}
