package com.users.management.exception;

public class EmailAlreadyUsedException extends Exception {

    public EmailAlreadyUsedException(String message) {
        super(message);
    }
}
