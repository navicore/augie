package com.onextent.util.codeable;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONCoder {

    public static Code newCode() {
        return new CodeImpl();
    }
    public static Code newCode(String ser) throws CodeableException {
        JSONObject jobj;
        try {
            jobj = new JSONObject(ser);
        } catch (JSONException e) {
            throw new CodeableException(e);
        }
        return new CodeImpl(jobj);
    }
    public static CodeArray<CodeArray<?>> newArrayOfArray() {
        return new CodeArrayImpl<CodeArray<?>>();
    }
    public static CodeArray<Code> newArrayOfCode() {
        return new CodeArrayImpl<Code>();
    }
    public static CodeArray<Integer> newArrayOfInt() {
        return new CodeArrayImpl<Integer>();
    }
    public static CodeArray<Long> newArrayOfLong() {
        return new CodeArrayImpl<Long>();
    }
    public static CodeArray<Double> newArrayOfDouble() {
        return new CodeArrayImpl<Double>();
    }
    public static CodeArray<Float> newArrayOfFloat() {
        return new CodeArrayImpl<Float>();
    }
    public static CodeArray<String> newArrayOfString() {
        return new CodeArrayImpl<String>();
    }

    private static class CodeArrayImpl<E> implements CodeArray<E> {

        final JSONArray jarray;
        
        CodeArrayImpl() {
           this(new JSONArray());
        }       
        CodeArrayImpl(JSONArray ja) {
           jarray = ja; 
        }
        @Override
        public void add(E value) throws CodeableException {
            if (value instanceof Code) {
                JSONObject jo = ((CodeImpl) value).json;
                jarray.put(jo);
            } else if (value instanceof CodeArray) {
                @SuppressWarnings("rawtypes")
                JSONArray ja = ((CodeArrayImpl) value).jarray;
                jarray.put(ja);
            } else {
                jarray.put(value);
            }
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        public E get(int i) throws CodeableException {
            try {
                Object o = jarray.get(i);
                if (o instanceof JSONObject) {
                    return (E) new CodeImpl((JSONObject)o);
                    
                } else if (o instanceof JSONArray) {
                    return (E) new CodeArrayImpl((JSONArray)o);
                    
                } else {
                    return (E) o;
                }
            } catch (JSONException e) {
                throw new CodeableException(e);
            }
        }

        @Override
        public Iterator<E> iterator() {
            // TODO Auto-generated method stub
            return null;
        }
        @Override
        public int length() {
            return jarray.length();
        }
        public String toString() {
            return jarray.toString();
        }
        @Override
        public int hashCode() {
            return jarray.hashCode() + this.getClass().hashCode();
        }
        @Override
        public boolean equals(Object obj) {
            return jarray.equals(obj);
        }
    }
    
    private static class CodeImpl implements Code {
        
        private final JSONObject json;
        
        CodeImpl(JSONObject j) {
            json = j;
        }       
        CodeImpl() {
            this(new JSONObject());
        }

        @Override
        public void put(String key, Code value) throws CodeableException {
            CodeImpl ci = (CodeImpl) value;
            try {
                json.put(key, ci.json);
            } catch (JSONException e) {
                throw new CodeableException(e);
            }
        }

        @Override
        public void put(String key, String value) throws CodeableException {
            try {
                json.put(key, value);
            } catch (JSONException e) {
                throw new CodeableException(e);
            }
        }
        @Override
        public void put(String key, CodeArray<?> value) throws CodeableException {
            CodeArrayImpl<?> ca = (CodeArrayImpl<?>) value;
            try {
                json.put(key, ca.jarray);
            } catch (JSONException e) {
                throw new CodeableException(e);
            }
        }
        @Override
        public void put(String key, int value) throws CodeableException {
            try {
                json.put(key, value);
            } catch (JSONException e) {
                throw new CodeableException(e);
            }           
        }
        @Override
        public void put(String key, long value) throws CodeableException {
            try {
                json.put(key, value);
            } catch (JSONException e) {
                throw new CodeableException(e);
            }                      
        }
        @Override
        public void put(String key, float value) throws CodeableException {
            try {
                json.put(key, value);
            } catch (JSONException e) {
                throw new CodeableException(e);
            }                      
        }
        @Override
        public void put(String key, double value) throws CodeableException {
            try {
                json.put(key, value);
            } catch (JSONException e) {
                throw new CodeableException(e);
            }                                 
        }
        @Override
        public Code get(String key) throws CodeableException {
            JSONObject jo;
            try {
                jo = json.getJSONObject(key);
                return new CodeImpl(jo);
            } catch (JSONException e) {
                throw new CodeableException(e);
            }
        }
        @SuppressWarnings("rawtypes")
        @Override
        public CodeArray<?> getCodeArray(String key) throws CodeableException {
            JSONArray jarray;
            try {
                jarray = json.getJSONArray(key);
                return new CodeArrayImpl(jarray);
            } catch (JSONException e) {
                throw new CodeableException(e);
            }
        }
        @Override
        public String getString(String key) throws CodeableException {
            try {
                return json.getString(key);
            } catch (JSONException e) {
                throw new CodeableException(e);
            }
        }
        @Override
        public int getInt(String key) throws CodeableException {
            try {
                return json.getInt(key);
            } catch (JSONException e) {
                throw new CodeableException(e);
            }
        }
        @Override
        public long getLong(String key) throws CodeableException {
            try {
                return json.getLong(key);
            } catch (JSONException e) {
                throw new CodeableException(e);
            }
        }
        @Override
        public float getFloat(String key) throws CodeableException {
            try {
                return (float) json.getDouble(key);
            } catch (JSONException e) {
                throw new CodeableException(e);
            }
        }
        @Override
        public double getDouble(String key) throws CodeableException {
            try {
                return json.getDouble(key);
            } catch (JSONException e) {
                throw new CodeableException(e);
            }
        }
        @Override
        public boolean has(String key) {
            
            return json.has(key);
        }
        @Override
        public boolean equals(Object obj) {
            return json.equals(obj);
        }
        @Override
        public int hashCode() {
            return json.hashCode() + this.getClass().hashCode();
        }
        @Override
        public String toString() {
            return json.toString();
        }
    }
}
