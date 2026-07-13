package com.example.coreris.exception_handler;

public class AppointmentNotFoundException extends RuntimeException {
    public AppointmentNotFoundException(long id) {
        super("Appointment " + id + " not found");
    }
}
