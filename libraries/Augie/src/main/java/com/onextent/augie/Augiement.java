/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie;

import java.util.Set;

import android.app.DialogFragment;

import com.onextent.android.codeable.Codeable;
import com.onextent.android.codeable.CodeableName;

/**
 * An Augiement is an Augmented Reality feature, maybe some local
 * info like weather and tides, maybe some astronomy markers, maybe
 * some face recog, maybe some camera controls, etc...
 * 
 * @author esweeney
 *
 */
public interface Augiement extends Codeable {
    
	//the view calls this when bmp has been rebuilt from scratch
	//and we need each feature to redraw
	void updateCanvas();

	//called if module is registered with a feature like a shaker or a timer
	void clear();
	
	//called if activity is suspended
	void stop();
    
	//called if activity is resumed
	void resume();
	
	//called after default no arg constructor
	void onCreate(AugieScape av, Set<Augiement> helpers) throws AugiementException;
	
    DialogFragment getUI();
    
    Meta getMeta();
    
    interface Meta {
        
        Class<? extends Augiement> getAugiementClass();
        CodeableName getCodeableName();
        String getUIName();
        String getDescription();
        Set<CodeableName> getDependencyNames();
        int getMinSdkVer();
    }
}
