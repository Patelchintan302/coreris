package com.example.coreris.exception_handler;

public class UsernameAlreadyExistsException extends RuntimeException{
    public UsernameAlreadyExistsException(String username){
        super("USername : "+username+" already exists");
    }
}
