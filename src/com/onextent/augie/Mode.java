package com.onextent.augie;

import org.json.JSONException;
import org.json.JSONObject;
import com.onextent.augie.data.Codable;
import android.util.Log;

public class Mode implements Codable {
    
    static final String NAME = "name";
    static final String AUGIENAME = "augieName";
    static final String CAMERA = "camera";
    static final String AUGIEMENTS = "augiements";

    private String name, augieName;

    @Override
    public JSONObject getCode() {
        JSONObject json = new JSONObject();
        try {
            json.put(NAME, name);
            json.put(AUGIENAME, augieName);
        } catch (JSONException e) {
            Log.e(TAG, e.toString(), e);
        }
        return json;
    }

    @Override
    public void setCode(JSONObject code) {
        try {
            name = code.getString(NAME);
            augieName = code.getString(AUGIENAME);
        } catch (JSONException e) {
            Log.e(TAG, e.toString(), e);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getAugieName() {
        return augieName;
    }

    public void setAugieName(String augieName) {
        this.augieName = augieName;
    }
}
