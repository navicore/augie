package com.onextent.augie.ments.cvface;

import java.util.Set;

import android.os.Build;
import android.support.v4.app.DialogFragment;

import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.CodeableException;
import com.onextent.android.codeable.CodeableName;
import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;

public class CvFaceFinder implements Augiement {
    
    private final AbstractCvFaceFinder ff;
    
    public CvFaceFinder() {
        ff = AbstractCvFaceFinder.getInstance();
    }

    @Override
    public CodeableName getCodeableName() {

        return ff.getCodeableName();
    }

    @Override
    public Code getCode() throws CodeableException {
        return ff.getCode();
    }

    @Override
    public void setCode(Code code) throws CodeableException {
        ff.setCode(code);
    }

    @Override
    public void updateCanvas() {
        ff.updateCanvas();
    }

    @Override
    public void clear() {
        ff.clear();
    }

    @Override
    public void stop() {
        ff.stop();
    }

    @Override
    public void resume() {
        ff.resume();
    }

    @Override
    public void onCreate(AugieScape av, Set<Augiement> helpers)
            throws AugiementException {
        ff.onCreate(av, helpers);
    }

    @Override
    public DialogFragment getUI() {
        return ff.getUI();
    }

    public static final Meta META =
        new Augiement.Meta() {

            @Override
            public Class<? extends Augiement> getAugiementClass() {
    
                return CvFaceFinder.class;
            }

            @Override
            public CodeableName getCodeableName() {
                
                return AbstractCvFaceFinder.AUGIE_NAME;
            }

            @Override
            public String getUIName() {

                return AbstractCvFaceFinder.UI_NAME;
            }

            @Override
            public String getDescription() {
                
                return AbstractCvFaceFinder.DESCRIPTION;
            }

            @Override
            public Set<CodeableName> getDependencyNames() {
                return AbstractCvFaceFinder.deps;
            }

            @Override
            public int getMinSdkVer() {
                return Build.VERSION_CODES.ICE_CREAM_SANDWICH;
            }
        };

    @Override
    public Meta getMeta() {

        return META;
    }
    
    public enum DETECTION_TYPE {JAVA, NATIVE};
    public DETECTION_TYPE getDtype() {
        return ff.getDtype();
    }

    public void setDtype(DETECTION_TYPE dtype) {
        ff.setDtype(dtype);
    }
    
    public int getFaceSizePct() {
        return ff.getFaceSizePct();
    }

    public void setFaceSizePct(int sz) {
        ff.setFaceSizePct(sz);
    }
}
