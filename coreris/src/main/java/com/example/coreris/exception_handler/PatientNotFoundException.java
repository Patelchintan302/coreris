package com.example.coreris.exception_handler;

public class PatientNotFoundException extends RuntimeException{
    public PatientNotFoundException(Long id) {
        super("Patient with id: " + id + " not found");
    }
    public PatientNotFoundException(String message){
        super(message);
    }
}
