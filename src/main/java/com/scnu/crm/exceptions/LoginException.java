package com.scnu.crm.exceptions;

public class LoginException extends RuntimeException{
    public LoginException() {
        super();
    }

    public LoginException(String message) {
        super(message);
    }
}
