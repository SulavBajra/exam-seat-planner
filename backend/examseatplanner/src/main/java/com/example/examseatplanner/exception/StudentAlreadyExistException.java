package com.example.examseatplanner.exception;

public class StudentAlreadyExistException extends RuntimeException {
    public StudentAlreadyExistException(String message){
        super(message);
    }
}
