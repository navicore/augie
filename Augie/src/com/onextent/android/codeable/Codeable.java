package com.onextent.android.codeable;

public interface Codeable {
    
    public final String TAG = "AUGIE";
    static final String CODEABLE_NAME_KEY   = "codable_name";
    static final String UI_NAME_KEY         = "uiname";
    
    CodeableName getCodeableName();
    
    Code getCode() throws CodeableException;

    void setCode(Code code) throws CodeableException;
}
