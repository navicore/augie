package com.onextent.util.codeable;

import com.onextent.augie.Augiement;

public interface Codable {
    
    static final String TAG = Augiement.TAG;
    
    CodeableName getCodeableName();
    
    Code getCode();

    void setCode(Code code);
}
