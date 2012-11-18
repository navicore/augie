/**
 * copyright Ed Sweeney, 2012, all rights reserved
 */
package com.onextent.augie;

import java.util.Set;

/**
 * An Augiement is an Augmented Reality feature, maybe some local
 * info like weather and tides, maybe some astronomy markers, maybe
 * some face recog, maybe some camera features, etc...
 * 
 * @author esweeney
 *
 */
public interface Augiement {

	public static final String TAG = "AUGIE";  //for logging
	
	//the view calls this when bmp has been rebuilt from scratch
	//and we need each feature to redraw
	void updateCanvas();

	//called if module is registered with a feature like a shaker or a timer
	void clear();
	
	//called if activity is suspended
	void stop();
    
	//called if activity is resumed
	void resume();
	
	//key by which dependency manager orders calls.  see listDependencyAugiementNames
	String getAugieName();
	
	//called after default no arg constructor
	void onCreate(AugieView av, Set<Augiement> helpers) throws AugiementException;
	
	Set<String> getDependencyNames();
	
	//todo:
	// first list dependencies by name
	// then implement setAugiements for each with 'if instanceof'
	// check for nulls after the for () loop
	
	//THEN implement a depencency reg.
	//
	//  3 registries:  ACTIVE, WAITING, INACTIVE
	//  REG
	//  reg puts modules whose deps have keys in active reg (a tree map?) in active reg
	//  then checks waiting reg to see which of the modules waiting can now reg
	//  else adds it to the waiting reg
	//  UNREG
	//  removes module and any modules that depend on it and puts 'em in inactive reg
}
