package com.example.examseatplanner.dto;

import java.util.List;

/// data transfer object , handles JSON input
public record StudentRequestDTO(int roll,
                                int semester,
                                String enrollYear,
                                String program,
                                List<String> subjects) {
}
