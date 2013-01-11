package com.onextent.android.codeable;

public class CodeableException extends Exception {

    private static final long serialVersionUID = 4412884603129040707L;

    public CodeableException() {
    }

    public CodeableException(String detailMessage) {
        super(detailMessage);
    }

    public CodeableException(Throwable throwable) {
        super(throwable);
    }

    public CodeableException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
