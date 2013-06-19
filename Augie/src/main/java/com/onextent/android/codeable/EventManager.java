/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.android.codeable;

public interface EventManager {
	
	public void fire(Code code) ;
	
	public void listen(CodeableName name, CodeableHandler handler);
	
	public void unlisten(CodeableName name, CodeableHandler handler);
}
