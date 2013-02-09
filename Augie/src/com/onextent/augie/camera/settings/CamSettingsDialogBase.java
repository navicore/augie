package com.onextent.augie.camera.settings;

import java.util.ArrayList;
import java.util.List;

import android.app.DialogFragment;

import com.onextent.augie.AugLog;
import com.onextent.augie.camera.AugCamera;

public class CamSettingsDialogBase extends DialogFragment {

    protected static int getPosition(Object item, List<?> list) {
    
        if (item == null) return 0;
    
        for (int i = 0; i < list.size(); i++) {
            if (item.equals(list.get(i))) return i;
        }
        return 0;
    }

    protected static List<String> getChoiceList(List<String> list) {
        if (list == null) return null;
        List<String> r = new ArrayList<String>();
        r.add("<unset>");
        r.addAll(list);
        return r;
    }

    protected void applyChanges(AugCamera c) {
        
        try {
            
            c.applyParameters();
            
        } catch (Exception e) {
            AugLog.e( e.toString());
        }
    }

    public CamSettingsDialogBase() {
        super();
    }

}