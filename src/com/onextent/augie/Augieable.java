package com.onextent.augie;

import org.json.JSONObject;

import android.content.Context;

public interface Augieable {
    
	public static final String TAG = "AUGIE";  //for logging
	
    /**
     * an Augieable is an applet / module hybrid.  It extends the
     * features of the host app like a module but has its own config
     * UI.  
     * 
     * An Augieable needs to store its own system-wide state.
     * 
     * Per-instance state is managed by the host application via
     * setState / getSate.
     * 
     * Cameras and features are Augieables.
     * 
     * @author esweeney
     *
     */
    interface EditCallback {
        
        void done();
    }
    
    interface Meta {
        
        String getTitle();
        String getDescription();
        String getCatagory();
    }
  
	String getAugieName();
	
	Meta getMeta();
	
    JSONObject getState() throws AugieableException;
    
    void setState(JSONObject state) throws AugieableException;
    
    void edit(Context context, EditCallback cb) throws AugieableException;
    
    boolean isEditable();
}
