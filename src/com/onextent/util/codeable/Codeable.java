package com.onextent.util.codeable;

import com.onextent.augie.Augiement;

public interface Codeable {
    
    static final String TAG = Augiement.TAG;
    static final String CODEABLE_NAME_KEY = "codable_name";
    
    CodeableName getCodeableName();
    
    Code getCode() throws CodeableException;

    void setCode(Code code) throws CodeableException;
}
