/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie;

import java.util.Set;

/**
 * An Augiement is an Augmented Reality feature, maybe some local
 * info like weather and tides, maybe some astronomy markers, maybe
 * some face recog, maybe some camera controls, etc...
 * 
 * @author esweeney
 *
 */
public interface Augiement extends Augieable {
	
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
	void onCreate(AugieView av, Set<Augiement> helpers) throws AugiementException;
	
	Set<String> getDependencyNames();
}