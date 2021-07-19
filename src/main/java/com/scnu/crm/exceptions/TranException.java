package com.scnu.crm.exceptions;

public class TranException extends RuntimeException{
    public TranException() {
        super();
    }

    public TranException(String message) {
        super(message);
    }
}
