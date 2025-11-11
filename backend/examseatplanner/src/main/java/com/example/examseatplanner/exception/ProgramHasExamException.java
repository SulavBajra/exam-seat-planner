package com.example.examseatplanner.exception;

public class ProgramHasExamException extends RuntimeException {
    public ProgramHasExamException() {
        super("This program is scheduled for an exam.");
    }

    public ProgramHasExamException(String message){
        super(message);
    }
}
