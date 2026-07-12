package com.example.coreris.exception_handler;

public class AppoinmentNotFoundException extends RuntimeException{
    public AppoinmentNotFoundException(long id){
        super("Appointment "+id+" not found");
    }
}
