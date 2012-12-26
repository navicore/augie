package com.onextent.util.codeable;

public interface Code {
    
    boolean has(String key);
    
    void put(String key, Code value) throws CodeableException;
    
    void put(String key, CodeArray<?> value) throws CodeableException;
    
    void put(String key, String value) throws CodeableException;
    
    void put(String key, CodeableName value) throws CodeableException;
    
    void put(String key, boolean value) throws CodeableException;
    
    void put(String key, int value) throws CodeableException;
    
    void put(String key, long value) throws CodeableException; 
    
    void put(String key, float value) throws CodeableException;
    
    void put(String key, double value) throws CodeableException;
   
    
    Code get(String key) throws CodeableException;
    CodeArray<?> getCodeArray(String key) throws CodeableException;
    
    String getString(String key) throws CodeableException;
    CodeableName getCodeableName(String key) throws CodeableException;
    
    boolean getBoolean(String key) throws CodeableException;
    int getInt(String key) throws CodeableException;
    long getLong(String key) throws CodeableException;
    float getFloat(String key) throws CodeableException;
    double getDouble(String key) throws CodeableException;

    @Override
    String toString();
    @Override
    int hashCode();
}
