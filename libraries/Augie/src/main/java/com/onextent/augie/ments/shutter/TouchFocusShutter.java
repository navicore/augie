/**
 * copyright Ed Sweeney, 2012, 2013 all rights reserved
 */
package com.onextent.augie.ments.shutter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.app.DialogFragment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.CodeArray;
import com.onextent.android.codeable.Codeable;
import com.onextent.android.codeable.CodeableException;
import com.onextent.android.codeable.CodeableName;
import com.onextent.android.codeable.JSONCoder;
import com.onextent.augie.AugLog;
import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.AugiementName;
import com.onextent.augie.camera.AugCamera;
import com.onextent.augie.camera.AugCameraException;
import com.onextent.augie.camera.AugPictureCallback;
import com.onextent.augie.marker.AugLine;
import com.onextent.augie.marker.AugScrible;
import com.onextent.augie.marker.AugScrible.GESTURE_TYPE;

public class TouchFocusShutter extends SimpleCameraShutter {

    public static final CodeableName AUGIE_NAME = new AugiementName("AUGIE/FEATURES/TOUCH_FOCUS_SHUTTER");
    public static final String UI_NAME = "Touch Focus Shutter";
    public static final String DESCRIPTION = "Trigger camera shutter by touching the screen.";
    
    final List<ScribleHolder> focus_areas, meter_areas;

    private ScribleHolder movingRect;
    private Point startP;
    private ScribleHolder touchFocusArea;

    public TouchFocusShutter() {

        focus_areas = new ArrayList<ScribleHolder>();
        meter_areas = new ArrayList<ScribleHolder>();
    }

    @Override
    public void onCreate(AugieScape av, Set<Augiement> helpers) throws AugiementException {
        super.onCreate(av, helpers);
    }

    private class ScribleHolder implements Codeable {

        Rect rect;

        @Override
        public CodeableName getCodeableName() { return new CodeableName("AUGIE/UTIL/RECT") {}; }

        @Override
        public Code getCode() throws CodeableException {
            Code code = JSONCoder.newCode();
            code.put( CODEABLE_NAME_KEY, getCodeableName() );
            code.put("top", rect.top);
            code.put("bottom", rect.bottom);
            code.put("left", rect.left);
            code.put("right", rect.right);
            return code;
        }

        @Override
        public void setCode(Code code) throws CodeableException {
            if (!code.getCodeableName(CODEABLE_NAME_KEY).equals(getCodeableName())) throw new CodeableException("not a rect");
            int top, bottom, left, right;
            top = code.getInt("top");
            bottom = code.getInt("bottom");
            left = code.getInt("left");
            right = code.getInt("right");
            rect = new Rect(left, top, right, bottom);
        }
    }

    protected void takePicture() throws AugCameraException {

        try {

            String focusmode =  camera.getParameters().getFocusMode();
            if (focusmode.equals( Camera.Parameters.FOCUS_MODE_AUTO)) {

                _setMeterAreas();
                if (focus_areas.isEmpty() && isAlways_set_focus_area()) {
                    _setTouchFocusArea();
                } else {
                    _setFocusAreas();
                }
                camera.applyParameters();
            }

            super.takePicture(new AugPictureCallback() {

                @Override
                public void onPictureTaken(byte[] data, AugCamera c) {
                    clearTouchFocusArea();
                }
            });
        } catch (AugCameraException e) {
            clearTouchFocusArea();
            throw e;
        }

    }

    void clearTouchFocusArea() {

        if (touchFocusArea != null) {
            focus_areas.remove(touchFocusArea);
            touchFocusArea = null;
        }
    }

    private void _setTouchFocusArea() {
        AugScrible scrible = augdraw.getCurrentScrible();
        if (scrible == null || scrible.size() == 0) return;
        AugLine l = scrible.get(scrible.size() -1);
        if (l == null) return;
        Point p = l.getP2();
        if (p == null) return;
        List<Camera.Area> fa = new ArrayList<Camera.Area>();
        Rect rect = getTouchFocusRect(p);
        Camera.Area ca = new Camera.Area(rect, 500);
        fa.add(ca);
        camera.getParameters().setFocusAreas(fa);

        ScribleHolder h = new ScribleHolder();
        touchFocusArea = h;
        h.rect = rect;
        focus_areas.add(h);
    }

    private List<Camera.Area> _scribleToArea(List<ScribleHolder> scribles) {

        List<Camera.Area> al = new ArrayList<Camera.Area>();
        for (ScribleHolder sh : scribles) {
            int weight = 500;
            Rect rect = getRelCoord(sh.rect);
            Camera.Area a = new Camera.Area(rect, weight);
            al.add(a);
        }
        return al;
    }

    protected boolean _setMeterAreas() throws AugCameraException {

        if (augieScape == null) return false;

        List<Camera.Area> ma = _scribleToArea(meter_areas);
        boolean success = ma.size() > 0;
        camera.getParameters().setMeteringAreas(ma);
        return success;
    }

    protected boolean _setFocusAreas() throws AugCameraException {

        if (augieScape == null) return false;

        List<Camera.Area> fa = _scribleToArea(focus_areas);
        boolean success = fa.size() > 0;
        camera.getParameters().setFocusAreas(fa);
        return success;
    }

    private void saveArea(AugScrible s, List<ScribleHolder> areas) {
        ScribleHolder h = new ScribleHolder();
        //h.scrible = s;
        h.rect = new Rect(s.getMinX(), s.getMinY(), s.getMaxX(), s.getMaxY());
        areas.add(h);
        augdraw.undoCurrentScrible();
    }

    private void saveFocusArea(AugScrible s) throws AugCameraException {
        int max_focus_areas = camera.getParameters().getMaxNumFocusAreas();
        if (max_focus_areas > 0 && max_focus_areas == focus_areas.size()) {
            focus_areas.remove(0);
        }
        saveArea(s, focus_areas);
        _setFocusAreas();
    }

    private void saveMeterArea(AugScrible s) throws AugCameraException {
        int max_metering_areas = camera.getParameters().getMaxNumMeteringAreas();
        if (max_metering_areas > 0 && max_metering_areas == meter_areas.size()) {
            meter_areas.remove(0);
        }
        saveArea(s, meter_areas);
        _setMeterAreas();
    }

    private ScribleHolder getRect(Point p) {
        //todo: deal with rect inside rect
        for (ScribleHolder h : focus_areas) 
            if (h.rect.contains(p.x, p.y)) return h;
        for (ScribleHolder h : meter_areas) 
            if (h.rect.contains(p.x, p.y)) return h;

        return null;
    }

    private void deleteRect(ScribleHolder h) {
        focus_areas.remove(h);
        meter_areas.remove(h);
    }
    private void moveRect(MotionEvent event) {
        Point endP = new Point((int) event.getX(), (int) event.getY());
        movingRect.rect.offset(endP.x - startP.x, endP.y - startP.y);
        augdraw.undoCurrentScrible();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        try {

            switch(event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                //todo: 2 finger move should be property
                startP = new Point((int) event.getX(), (int) event.getY());
                movingRect = getRect(startP);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                //todo: 2 finger move should be property
                //startP = new Point((int) event.getX(), (int) event.getY());
                //movingRect = getRect(startP);
                break;
            case MotionEvent.ACTION_MOVE:
                if (movingRect != null) {
                    moveRect(event);
                    startP = new Point((int) event.getX(), (int) event.getY()); //reset startP
                }
                break;
            case MotionEvent.ACTION_UP:
                if (movingRect != null) {
                    moveRect(event);
                    if (closeToEdge(event)) deleteRect(movingRect);
                    startP = null;
                    movingRect = null;
                } else {
                    AugScrible scrible = augdraw.getCurrentScrible();

                    handleGesture(scrible);
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            default:
                return false;
            }
        } catch (Exception e) {
            Toast.makeText(augieScape.getContext(), e.toString(), Toast.LENGTH_SHORT).show(); //todo: tmp
            AugLog.e( e.toString(), e);
        }
        return true;
    }

    @Override
    public void stop() {
        //noop
    }

    @Override
    public void resume() {
        //noop
    }

    @Override
    public void updateCanvas() {
        Paint p = augieScape.getPaint();
        float orig_w = p.getStrokeWidth();
        int old_color = p.getColor();
        for (ScribleHolder h : meter_areas) {
            p.setColor(getMeterAreaColor());
            augieScape.getCanvas().drawRect(h.rect, p);
        }
        for (ScribleHolder h : focus_areas) {
            p.setColor(getFocusAreaColor());
            augieScape.getCanvas().drawRect(h.rect, p);
        }
        p.setStrokeWidth(orig_w);
        p.setColor(old_color);
    }

    @Override
    public void clear() {
        focus_areas.clear();
        meter_areas.clear();
    }

    private static float CLOSE_PIXELS = 25;
    private boolean xcloseToEdge(MotionEvent e) {
        float x = e.getX();
        if ( x < CLOSE_PIXELS ) return true;
        if ( x > (augieScape.getWidth() - CLOSE_PIXELS )) return true;

        return false;
    }
    private boolean ycloseToEdge(MotionEvent e) {
        float y = e.getY();
        if ( y < CLOSE_PIXELS ) return true;
        if ( y > (augieScape.getHeight() - CLOSE_PIXELS )) return true;

        return false;
    }
    protected boolean closeToEdge(MotionEvent e) {
        return xcloseToEdge(e) || ycloseToEdge(e);
    }

    @Override
    public CodeableName getCodeableName() {
        return AUGIE_NAME;
    }
    
    @Override
    public Code getCode() throws CodeableException {
        Code code = super.getCode();
        if (code == null) code = JSONCoder.newCode();
        CodeArray<Code> faScribleCode= JSONCoder.newArrayOfCode();
        code.put("focusAreas", faScribleCode);
        for (ScribleHolder sh : focus_areas) {
            faScribleCode.add(sh.getCode());
        }
        CodeArray<Code> maScribleCode= JSONCoder.newArrayOfCode();
        code.put("meterAreas", maScribleCode);
        for (ScribleHolder sh : meter_areas) {
            maScribleCode.add(sh.getCode());
        }

        return code;
    }

    @Override
    public void setCode(Code code) throws CodeableException {
        super.setCode(code);
        try {
            if (code.has("focusAreas")) {
                @SuppressWarnings("unchecked")
                CodeArray<Code> faCodeArray = (CodeArray<Code>) code.getCodeArray("focusAreas");
                for (Code c : faCodeArray) {
                    ScribleHolder sh = new ScribleHolder();
                    sh.setCode(c);
                    focus_areas.add(sh);
                    _setFocusAreas();
                }
            }
            if (code.has("meterAreas")) {
                @SuppressWarnings("unchecked")
                CodeArray<Code> maCodeArray = (CodeArray<Code>) code.getCodeArray("meterAreas");
                for (Code c : maCodeArray) {
                    ScribleHolder sh = new ScribleHolder();
                    sh.setCode(c);
                    meter_areas.add(sh);
                    _setMeterAreas();
                }
            }
            
        } catch (AugCameraException e) {
            throw new CodeableException(e);
        }
    }
    
    @Override
    public DialogFragment getUI() {
       
        return new TouchFocusShutterDialog();
    }
    
    public static final Meta META =
        new Augiement.Meta() {

            @Override
            public Class<? extends Augiement> getAugiementClass() {
    
                return TouchFocusShutter.class;
            }

            @Override
            public CodeableName getCodeableName() {
                
                return AUGIE_NAME;
            }

            @Override
            public String getUIName() {

                return UI_NAME;
            }

            @Override
            public String getDescription() {
                
                return DESCRIPTION;
            }

            @Override
            public Set<CodeableName> getDependencyNames() {
                return SimpleCameraShutter.deps;
            }

            @Override
            public int getMinSdkVer() {
                return Build.VERSION_CODES.ICE_CREAM_SANDWICH;
            }
        };
        
    @Override
    public Augiement.Meta getMeta() {
        
        return META;
    }
    
    protected void handleGesture(AugScrible scrible) throws AugCameraException {

        if (scrible == null)  {
            AugLog.w("no current scrible / gesture");
            return;
        }
        GESTURE_TYPE g_type = scrible.getGestureType();
        switch (g_type) {

        case TAP:
            takePicture();
            break;
        case CLOCKWISE_AREA:
            saveFocusArea(scrible);
            break;
        case COUNTER_CLOCKWISE_AREA:
            saveMeterArea(scrible);
            break;
        default:
            AugLog.w("unrecognized gesture");
        }
    }
}
