package com.onextent.augie.ments.myface;

import java.util.Set;

import android.os.Build;
import android.support.v4.app.DialogFragment;

import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.CodeableException;
import com.onextent.android.codeable.CodeableName;
import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;

public class FaceFinder implements Augiement {
    
    private final AbstractFaceFinder ff;
    
    public FaceFinder() {
        ff = AbstractFaceFinder.getInstance();
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
    
                return FaceFinder.class;
            }

            @Override
            public CodeableName getCodeableName() {
                
                return AbstractFaceFinder.AUGIE_NAME;
            }

            @Override
            public String getUIName() {

                return AbstractFaceFinder.UI_NAME;
            }

            @Override
            public String getDescription() {
                
                return AbstractFaceFinder.DESCRIPTION;
            }

            @Override
            public Set<CodeableName> getDependencyNames() {
                return AbstractFaceFinder.deps;
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
}
