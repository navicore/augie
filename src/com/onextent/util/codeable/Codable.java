package com.onextent.util.codeable;

import com.onextent.augie.AugieName;
import com.onextent.augie.Augiement;

public interface Codable {
    
    static final String TAG = Augiement.TAG;
    
    AugieName getAugieName();
    
    Code getCode();

    void setCode(Code code);
}
