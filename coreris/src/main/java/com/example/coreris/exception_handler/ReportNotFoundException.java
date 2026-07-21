package com.example.coreris.exception_handler;

public class ReportNotFoundException extends RuntimeException {
    public ReportNotFoundException(String message) {
        super(message);
    }
    public ReportNotFoundException(long id) {
        super("Report not found with id: " + id);
    }
}
