package com.example.examseatplanner.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ControllerAdvice
public class GlobalExceptionHandler {

    public static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex, WebRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("message", ex.getMessage());
        errorDetails.put("details", request.getDescription(false));

        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex, WebRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("message", ex.getMessage());
        errorDetails.put("details", request.getDescription(false));

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StudentAlreadyExistException.class)
    public ResponseEntity<Map<String, String>> handleStudentAlreadyExistException(
        StudentAlreadyExistException ex){
             logger.warn("Student already exists {}",ex.getMessage());
             Map<String,String> errors = new HashMap<>();
             errors.put("message","Student already exists");
             return ResponseEntity.badRequest().body(errors);
        }

    @ExceptionHandler(ProgramHasExamException.class)
    public ResponseEntity<Map<String,String>> handleProgramHasExamException(
        ProgramHasExamException ex){
            logger.warn("Program has exam {}",ex.getMessage());
            Map<String,String> errors = new HashMap<>();
            errors.put("message", "This program is scheduled for exam");
            return ResponseEntity.badRequest().body(errors);
        }

    @ExceptionHandler(ProgramHasStudentsException.class)
    public ResponseEntity<Map<String,String>> handleProgramHasStudentsException(
        ProgramHasStudentsException ex){
            logger.warn("Program has student {}",ex.getMessage());
            Map<String,String> errors = new HashMap<>();
            errors.put("message", "This program has students for exam");
            return ResponseEntity.badRequest().body(errors);
        }

    @ExceptionHandler(NoStudentException.class)
    public ResponseEntity<Map<String,String>> handleNoStudentException(
        NoStudentException ex){
            logger.warn("There are no students {}",ex.getMessage());
            Map<String,String> errors = new HashMap<>();
            errors.put("message", "There are no students for exam");
            return ResponseEntity.badRequest().body(errors);
        }
    
    @ExceptionHandler(ExceedsRoomCapacityException.class)
    public ResponseEntity<Map<String,String>> handleExceedsRoomCapacityException(
        ExceedsRoomCapacityException ex){
            logger.warn("Students exceeds the capacity of room {}",ex.getMessage());
            Map<String,String> errors = new HashMap<>();
            errors.put("message", "There are more students than room capacity");
            return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(StudentAlreadyHasExamException.class)
    public ResponseEntity<Map<String,String>> handleStudentAlreadyHasExamException(
        StudentAlreadyHasExamException ex){
            logger.warn("Students already has exam {}",ex.getMessage());
            Map<String,String> errors = new HashMap<>();
            errors.put("message", "Student already has exam in this date range");
            return ResponseEntity.badRequest().body(errors);
    }

}