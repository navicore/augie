package com.onextent.augie;

import org.json.JSONException;
import org.json.JSONObject;
import com.onextent.augie.data.Codable;
import android.util.Log;

public class Mode implements Codable {
    
    static final String KEY_NAME = "name";
    static final String KEY_AUGIENAME = "augieName";
    static final String KEY_CAMERA = "camera";
    static final String KEY_AUGIEMENTS = "augiements";

    private String name;
    private AugieName augieName;

    @Override
    public JSONObject getCode() {
        JSONObject json = new JSONObject();
        try {
            json.put(KEY_NAME, name);
            json.put(KEY_AUGIENAME, augieName.toString());
        } catch (JSONException e) {
            Log.e(TAG, e.toString(), e);
        }
        return json;
    }

    @Override
    public void setCode(JSONObject code) {
        try {
            name = code.getString(KEY_NAME);
            augieName = new ModeName(code.getString(KEY_AUGIENAME));
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
    public AugieName getAugieName() {
        return augieName;
    }

    public void setAugieName(AugieName augieName) {
        this.augieName = augieName;
    }
}
