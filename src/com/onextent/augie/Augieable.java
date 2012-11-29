package com.onextent.augie;

import com.onextent.augie.data.Codable;
import android.content.Context;

public interface Augieable extends Codable {
    
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
 
	Meta getMeta();
	
    void edit(Context context, EditCallback cb) throws AugieableException;
    
    boolean isEditable();
}
