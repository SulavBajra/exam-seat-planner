package com.example.examseatplanner.exception;

public class ExceedsRoomCapacityException extends RuntimeException {

    public ExceedsRoomCapacityException(){
        super("The capacity of rooms is not enogh");
    }

    public ExceedsRoomCapacityException(String message){
        super(message);
    }
}
