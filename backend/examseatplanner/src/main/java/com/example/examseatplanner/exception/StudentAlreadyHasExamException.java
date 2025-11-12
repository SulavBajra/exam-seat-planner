package com.example.examseatplanner.exception;

public class StudentAlreadyHasExamException extends RuntimeException{

    public StudentAlreadyHasExamException(){
        super("Students already has exam scheduled");
    }

    public StudentAlreadyHasExamException(String message){
        super(message);
    }
}
