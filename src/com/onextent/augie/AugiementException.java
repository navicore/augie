package com.onextent.augie;

public class AugiementException extends AugieException {

    private static final long serialVersionUID = -7732087428568992319L;
    
    public AugiementException() {
        super();
    }
    
    public AugiementException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
    
    public AugiementException(Throwable throwable) {
        super(throwable);
    }
    
    public AugiementException(String msg) {
        super(msg);
    }
}
