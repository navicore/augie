package com.onextent.augie.camera;

import com.onextent.augie.AugieException;

public class AugCameraException extends AugieException {

    private static final long serialVersionUID = 8034238771379599164L;
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
