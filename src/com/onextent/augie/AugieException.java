package com.onextent.augie;

public class AugieException extends java.lang.Exception {

    private static final long serialVersionUID = -828987509753604606L;
    
    protected AugieException() {
        super();
    }

    protected AugieException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    protected AugieException(String detailMessage) {
        super(detailMessage);
    }

    protected AugieException(Throwable throwable) {
        super(throwable);
    }
}
