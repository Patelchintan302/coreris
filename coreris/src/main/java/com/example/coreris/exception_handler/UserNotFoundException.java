package com.example.coreris.exception_handler;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(Long id) {
        super("User with id: " + id + " not found");
    }
}
