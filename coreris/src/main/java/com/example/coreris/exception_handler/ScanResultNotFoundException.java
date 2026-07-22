package com.example.coreris.exception_handler;

import com.example.coreris.entity.ScanResult;

public class ScanResultNotFoundException extends RuntimeException{
    public ScanResultNotFoundException(String message) {
        super(message);
    }
    public ScanResultNotFoundException(long id) {
        super("Scan Result not found with id: " + id);
    }
}
