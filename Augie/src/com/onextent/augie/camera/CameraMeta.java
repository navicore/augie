package com.onextent.augie.camera;

import com.onextent.android.codeable.CodeableName;

public class CameraMeta {
    
    final public int id;
    final public CodeableName cn;
    final public String uiname;

    public CameraMeta(int id, CodeableName cn, String uiname) {
        this.id = id;
        this.cn = cn;
        this.uiname = uiname;
    }
    
    public int getId() {
        return id;
    }
    public CodeableName getCn() {
        return cn;
    }
    public String getUiname() {
        return uiname;
    }

}