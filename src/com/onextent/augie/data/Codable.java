package com.onextent.augie.data;

/**
 * I really really did not want a data object relying on another impl but 
 * don't have time to redo the old DataObject lib
 * 
 * TODO: make a sane xpathy data object lib with default impl backed by json
 * 
 */
import org.json.JSONObject;

import com.onextent.augie.AugieName;
import com.onextent.augie.Augiement;

public interface Codable {
    
    static final String TAG = Augiement.TAG;
    
    AugieName getAugieName();
    
    JSONObject getCode();

    void setCode(JSONObject code);
}
