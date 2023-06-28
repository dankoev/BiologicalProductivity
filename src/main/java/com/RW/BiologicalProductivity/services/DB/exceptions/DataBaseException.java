package com.RW.BiologicalProductivity.services.DB.exceptions;

public class DataBaseException extends Exception{
    private String message;
    
    public DataBaseException(String message) {
        super(message);
        this.message = message;
    }
    
}
