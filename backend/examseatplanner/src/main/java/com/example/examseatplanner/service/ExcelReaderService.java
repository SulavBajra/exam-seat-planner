package com.example.examseatplanner.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelReaderService {

    public List<String> readStudentIdsFromExcel(MultipartFile file) throws IOException {
        List<String> studentIds = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            boolean firstRow = true;

            for (Row row : sheet) {
                // Skip header row
                if (firstRow) {
                    firstRow = false;
                    continue;
                }

                Cell cell = row.getCell(0); // First column (A)
                if (cell != null) {
                    String studentId = getCellValueAsString(cell);
                    if (studentId != null && !studentId.isEmpty()) {
                        studentIds.add(studentId);
                    }
                }
            }
        }
        return studentIds;
    }

    private String getCellValueAsString(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                // Handle both integer and decimal numbers
                double num = cell.getNumericCellValue();
                return num == (long) num ? String.valueOf((long) num) : String.valueOf(num);
            case FORMULA:
                return getCellValueAsString(
                        cell.getSheet().getWorkbook()
                                .getCreationHelper()
                                .createFormulaEvaluator()
                                .evaluateInCell(cell)
                );
            default:
                return null;
        }
    }
}