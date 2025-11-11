package com.example.examseatplanner.exception;

public class ProgramHasStudentsException extends RuntimeException{

    public ProgramHasStudentsException(){
        super("This program has students");
    }

    public ProgramHasStudentsException(String message){
        super(message);
    }
}
