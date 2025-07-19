package com.example.examseatplanner.dto;

import java.util.List;

public record ProgramDTO(String programName, Integer programCode, List<String> subjectNames) {}
