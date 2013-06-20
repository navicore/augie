/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie;

public abstract class AugieableException extends AugieException {

    private static final long serialVersionUID = -442265483287355836L;

    protected AugieableException() {
        super();
    }

    protected AugieableException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    protected AugieableException(String detailMessage) {
        super(detailMessage);
    }

    protected AugieableException(Throwable throwable) {
        super(throwable);
    }
}
