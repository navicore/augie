/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.ments;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.graphics.Point;
import android.app.DialogFragment;
import android.view.MotionEvent;
import android.view.View;

import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.CodeableException;
import com.onextent.android.codeable.CodeableName;
import com.onextent.android.codeable.JSONCoder;
import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.AugiementName;
import com.onextent.augie.marker.AugLine;
import com.onextent.augie.marker.AugScrible;
import com.onextent.augie.marker.MarkerFactory;

public class Draw extends DrawBase {
    
    private static String DESCRIPTION = "Captures screen touches for processing by other augiements.";
    public static final CodeableName AUGIE_NAME = new AugiementName("AUGIE/FEATURES/DRAW");
    public static final String UI_NAME = "Draw";
	
	int prevX;
	int prevY;
    List<AugScrible> scribles;
    AugScrible currentScrible;
    private boolean etchaEnabled;

    @Override
    public void onCreate(AugieScape av, Set<Augiement> helpers) throws AugiementException {
    
        super.onCreate(av, helpers);
	    scribles = new ArrayList<AugScrible>();
	    currentScrible = null;
	    prevX = -1;
    }
	
	public AugScrible getCurrentScrible() {
	    return currentScrible;
	}
	
	public void undoCurrentScrible() {
	    if (currentScrible == null) return;
	    currentScrible.clear();
	    augieScape.reset();
	}
	
	public void undoLastScrible() {
		int last = scribles.size();
		int lastidx = last - 1;
		if (last > 0) {
			scribles.remove(lastidx);
			augieScape.reset();
		}
	}

	private void scrible(int lx, int ly, int x, int y) {
		Point p1 = new Point(lx, ly);
		Point p2 = new Point(x, y);
		AugLine l = MarkerFactory.createLine(p1, p2);
		currentScrible.add(l);
		//if (etchaEnabled)
		//    augieScape.getCanvas().drawLine(lx, ly, x, y, augieScape.getPaint());
	}
	
    @Override
	public boolean onTouch(View v, MotionEvent event) {
	    int action = event.getAction();
	    int x = (int) event.getX();
	    int y = (int) event.getY();
	    switch (action) {
	    case MotionEvent.ACTION_UP:
	        scrible(prevX, prevY, x, y);
	    	prevX = -1;

	    	break;
	    case MotionEvent.ACTION_DOWN:
	    	
	    	currentScrible = MarkerFactory.createScrible(augieScape);
	    	scribles.add(currentScrible);
	    
	        prevX = (int) event.getX();
	        prevY = (int) event.getY();
	    	break;
	    case MotionEvent.ACTION_MOVE:
	    	if (prevX != -1) {
	    		scrible(prevX, prevY, x, y);
	    	}
	        prevX = (int) event.getX();
	        prevY = (int) event.getY();
	    	break;
	    default:
	    	return false;
	    }
	    return true;
    }
    
    @Override
	public void clear() {
    	scribles.clear();
	}

    @Override
	public void updateCanvas() {

        if (prevX == -1) {
            if (currentScrible != null) currentScrible.clear();
            scribles.clear();
            return;
        }
        
		if (etchaEnabled)
    	for (AugScrible s : scribles) {
    		for (AugLine l : s) {
    			augieScape.getCanvas().drawLine(l.getP1().x, l.getP1().y, l.getP2().x, l.getP2().y, augieScape.getPaint());
    		}
    	}
	}
   
    public static final Augiement.Meta META =
        new Augiement.Meta() {

            @Override
            public Class<? extends Augiement> getAugiementClass() {
    
                return Draw.class;
            }

            @Override
            public int getMinSdkVer() {
                return 0;
            }
            @Override
            public CodeableName getCodeableName() {
                
                return AUGIE_NAME;
            }

            @Override
            public String getUIName() {

                return UI_NAME;
            }

            @Override
            public String getDescription() {

                return DESCRIPTION;
            }

            @Override
            public Set<CodeableName> getDependencyNames() {
                return null;
            }
        };

    @Override
    public CodeableName getCodeableName() {
        return AUGIE_NAME;
    }

    private static final String ETCHA_KEY = "etchaEnabled";
    @Override
    public Code getCode() throws CodeableException {
        Code code = JSONCoder.newCode();
        code.put(AUGIE_NAME);
        code.put(ETCHA_KEY, etchaEnabled);
        return code;
    }

    @Override
    public void setCode(Code code) throws CodeableException {
        if (code != null) {
            if (code.has(ETCHA_KEY)) {
                etchaEnabled = code.getBoolean(ETCHA_KEY);
            }
        }
    }

    @Override
	public void stop() {
		//noop
	}
	
	@Override
	public void resume() {
		//noop
	}

    @Override
    public DialogFragment getUI() {
    
        return new DrawDialog();
    }

    @Override
    public Meta getMeta() {

        return META;
    }
    
	public boolean isEtchaEnabled() {
        return etchaEnabled;
    }

    public void setEtchaEnabled(boolean etchaEnabled) {
        this.etchaEnabled = etchaEnabled;
    }
}
