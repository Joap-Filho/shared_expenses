package com.sharedexpenses.app.exception;

public class InviteException extends RuntimeException {
    public InviteException(String message) {
        super(message);
    }
    
    public InviteException(String message, Throwable cause) {
        super(message, cause);
    }
}
