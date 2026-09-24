package com.ashutosh.WorkSphere.exception;

public class InactiveResourceException extends RuntimeException{
    public InactiveResourceException(String message){
        super(message);
    }
}
