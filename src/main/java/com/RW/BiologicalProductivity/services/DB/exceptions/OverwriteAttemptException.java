package com.RW.BiologicalProductivity.services.DB.exceptions;

public class OverwriteAttemptException extends DataBaseException{
    
    public OverwriteAttemptException(String message) {
        super(message);
    }
}
