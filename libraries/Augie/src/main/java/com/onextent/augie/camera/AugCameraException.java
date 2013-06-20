/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.camera;

import com.onextent.augie.AugieableException;

public class AugCameraException extends AugieableException {

    private static final long serialVersionUID = 1826487587520709511L;

    public AugCameraException() {
        super();
    }

    public AugCameraException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public AugCameraException(Throwable throwable) {
        super(throwable);
    }

    public AugCameraException(String msg) {
        super(msg);
    }
}
