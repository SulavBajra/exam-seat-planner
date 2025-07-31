package com.example.examseatplanner.controller;


import com.example.examseatplanner.service.ExcelReaderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ExcelUploadController {

    private final ExcelReaderService excelReaderService;

    public ExcelUploadController(ExcelReaderService excelReaderService){
        this.excelReaderService = excelReaderService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty() || !file.getOriginalFilename().endsWith(".xlsx")) {
                return ResponseEntity.badRequest().body("Please upload a valid .xlsx file");
            }

            List<String> studentIds = excelReaderService.readStudentIdsFromExcel(file);
            return ResponseEntity.ok("Successfully processed " + studentIds.size() + " student IDs");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error processing file: " + e.getMessage());
        }
    }
}
