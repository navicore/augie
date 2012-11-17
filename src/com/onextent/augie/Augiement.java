/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie;

/**
 * An Augiement is an Augmented Reality feature, maybe some local
 * info like weather and tides, maybe some astronomy markers, maybe
 * some face recog, maybe some camera features, etc...
 * 
 * @author esweeney
 *
 */
public interface Augiement {

	public static final String TAG = "OETEST";  //for logging
	
	//the view calls this when bmp has been rebuilt from scratch
	//and we need each feature to redraw
	void updateBmp();

	//called if module is registered with a feature like a shaker or a timer
	void clear();
	
	void stop();
    
	void resume();
	
	//todo:
	//String getName();
	//List<String> listHelperFeatures()
	//void setContext(Context activity); throws AugieFeatureException;
	//void setHelperFeatures(List<AugieFeature>) throws AugieFeatureException;
	//void init() throws AugieFeatureException;
}
