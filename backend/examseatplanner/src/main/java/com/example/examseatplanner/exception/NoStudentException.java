package com.example.examseatplanner.exception;

public class NoStudentException extends RuntimeException {

    public NoStudentException(){
        super("Cannot create exam, No students found");
    }

    public NoStudentException(String message){
        super(message);
    }
}
