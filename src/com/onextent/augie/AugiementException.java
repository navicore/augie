package com.onextent.augie;

public class AugiementException extends Exception {

    private static final long serialVersionUID = -7732087428568992319L;
    
    public AugiementException(String msg) {
        super(msg);
    }
    public AugiementException(String msg, Error error) {
        super(msg, error);
    }
}
