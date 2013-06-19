/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie;

public class AugiementException extends AugieableException {

    private static final long serialVersionUID = -6017682270983713439L;

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
