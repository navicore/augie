package com.onextent.util.codeable;

import android.hardware.Camera;

public class Size implements Codeable {
    
    private int width, height;
    private final CodeableName cname = new CodeableName("AUGIE/CODEABLE/SIZE") { };
    
    public Size() {
        this(0,0);
    }
    public Size(Camera.Size sz) {
        this(sz.width, sz.height);
    }
    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public CodeableName getCodeableName() { return cname; }

    @Override
    public Code getCode() throws CodeableException {
        Code code = JSONCoder.newCode();
        code.put(Codeable.CODEABLE_NAME_KEY, cname);
        code.put("width", width);
        code.put("height", height);
        return code;
    }

    @Override
    public void setCode(Code code) throws CodeableException {
        if (!code.has("width")) throw new CodeableException("no width");
        if (!code.has("height")) throw new CodeableException("no height");
        width = code.getInt("width");
        height = code.getInt("height");
    }

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    @Override
    public boolean equals(Object o) {
        if (o instanceof Size) {
            Size os = (Size) o;
            return os.width == width && os.height == height;
        }
        return super.equals(o);
    }
    @Override
    public int hashCode() {
        return getClass().hashCode() + height + width;
    }
    @Override
    public String toString() {
        return width + "x" + height;
    }
}
