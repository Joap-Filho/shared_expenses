package com.sharedexpenses.app.exception;

public class UserAlreadyMemberException extends RuntimeException {
    public UserAlreadyMemberException(String message) {
        super(message);
    }
}
