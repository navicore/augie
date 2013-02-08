package com.onextent.augmatic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.Codeable;
import com.onextent.android.codeable.CodeableException;
import com.onextent.android.codeable.CodeableHandler;
import com.onextent.android.codeable.CodeableName;
import com.onextent.android.ui.AbstractTwoFingerListener;
import com.onextent.augie.AugieActivity;
import com.onextent.augie.AugieException;
import com.onextent.augie.AugieScape;
import com.onextent.augie.AugieStoreException;
import com.onextent.augie.AugiementException;
import com.onextent.augie.AugiementFactory;
import com.onextent.augie.Mode;
import com.onextent.augie.ModeManager;
import com.onextent.augie.camera.AugCameraFactory;
import com.onextent.augie.camera.fonecam.FoneCamFactory;
import com.onextent.augie.system.AugieScapeImpl;
import com.onextent.augie.system.ModeManagerImpl;
import com.onextent.augie.system.SuperScape;

public abstract class BaseAugmaticActivity 
extends SherlockFragmentActivity 
implements AugieActivity {

    private static final String INTENT_KEY_MODE_NAME = "augieModeName";
    private OrientationEventListener orientationEventListener;
    protected AugCameraFactory    cameraFactory;

    private AugiementFactory    augiementFactory;

    protected ModeManager       modeManager;
    private AugieScape          augieScape;

    private final Map<CodeableName, Set<CodeableHandler>> handlerSets;

    private int orientation = 0;

    private RelativeLayout prevlayout;
    private SuperScape superScape;

    public BaseAugmaticActivity() {
        super();
        callbacks = new HashMap<CodeableName, Callback>();
        handlerSets = new HashMap<CodeableName, Set<CodeableHandler>>();
    }

    private static File getOutputFile(String pref, String suffix)
            throws AugieException {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Augie");

        if ( !mediaStorageDir.exists() ){
            if (! mediaStorageDir.mkdirs()){
                AugAppLog.e( "failed to create directory");
                throw new AugieException("can not create dir") {
                    private static final long serialVersionUID = 1L; };
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + pref + timeStamp + suffix);

        return mediaFile;
    }

    //begin subclass methods
    protected abstract View getControlLayout();

    protected abstract int getLayoutId();

    protected abstract int getPreviewId();

    protected abstract View configMenuButton();
    //end subclass methods

    private int normalizeOrientation(int degrees) {

        //correct for device and rendering context

        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int devOrienation = display.getRotation();
        switch(devOrienation) {
        case Surface.ROTATION_90:
            degrees += 90;
            break;
        case Surface.ROTATION_180:
            degrees += 180;
            break;
        case Surface.ROTATION_270:
            degrees += 270;
            break;
        case Surface.ROTATION_0:
        default:
        }

        if (degrees > 315 || degrees <= 45) {
            return Surface.ROTATION_0;
        }

        if (degrees > 45 && degrees <= 135) {
            return Surface.ROTATION_90;
        }

        if (degrees > 135 && degrees <= 225) {
            return Surface.ROTATION_180;
        }

        if (degrees > 225 && degrees <= 315) {
            return Surface.ROTATION_270;
        }
        return Surface.ROTATION_0;
    }

    private void stopOrientationEventListener() {
        if (orientationEventListener != null) {
            orientationEventListener.disable(); 
            orientationEventListener = null;
        }
    }
    private void startOrientationEventListener() {
        orientationEventListener = 
                new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL)
        {
            @Override
            public void onOrientationChanged(int o) {
                orientation = normalizeOrientation(o);
            }};
            if (orientationEventListener.canDetectOrientation()) {
                orientationEventListener.enable();
            } else {
                AugAppLog.w("Can not detect orientation");
                Toast.makeText(this, "Can't Detect Orientation", Toast.LENGTH_LONG).show();
            }
    }

    SharedPreferences sharedPrefs;

    private View menuButton;

    public View getMenuButton() {
        return menuButton;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        try {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setFormat(PixelFormat.TRANSLUCENT);
            getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
            getWindow().requestFeature(Window.FEATURE_ACTION_MODE_OVERLAY);

            setContentView(getLayoutId());

            menuButton = configMenuButton();

        } catch (Throwable err) {
            AugAppLog.e( "can not create augmatic", err);
        }
    }

    @Override
    public ViewGroup getCamPrevLayout() {
        return prevlayout;
    }

    private void initCamFactory() {

        cameraFactory = new FoneCamFactory();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {

            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            switch (info.facing) {
            case CameraInfo.CAMERA_FACING_BACK:
                cameraFactory.registerCamera(i, AugCameraFactory.AUGIE_BACK_CAMERA, "Back Camera");
                break;
            case CameraInfo.CAMERA_FACING_FRONT:
                cameraFactory.registerCamera(i, AugCameraFactory.AUGIE_FRONT_CAMERA, "Front Camera");
                break;
            default:
                cameraFactory.registerCamera(i, new CodeableName("AUGIE/FEATURES/CAMERA/CAMERA_" + i), "Camera " + i);
            }
        }
    }

    private boolean setModeByIntent(Intent intent) throws CodeableException, AugieException {

        boolean newModeIsSet = false;
        Intent i;
        if (intent == null) {
            i = getIntent(); //bug! this only returns the intent that launched us,
                             //not the one waking us up.  todo: receive custom intent.
        } else {
            i = intent;
        }
        if (i != null) {
            Bundle extras = i.getExtras();
            if (extras != null && extras.containsKey(INTENT_KEY_MODE_NAME)) {
                String modeName = extras.getString(INTENT_KEY_MODE_NAME);
                if (modeName != null) {
                    if (intent != null) { //only for "newIntent" launch
                        Mode cm = modeManager.getCurrentMode();
                        if (cm != null) cm.deactivate();
                    }
                    CodeableName cn = new CodeableName(modeName);
                    Mode m = modeManager.getMode(cn);
                    if (m != null) {
                        AugAppLog.d("setting mode " + modeName + " from intent");
                        modeManager.setCurrentMode(m);
                        newModeIsSet = true;
                    }
                }
            }
        }
        return newModeIsSet;
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode,
            Bundle options) {
        // TODO Auto-generated method stub
        super.startActivityForResult(intent, requestCode, options);
        AugAppLog.d("ejs startActivityForResult ********************* " + intent);
    }

    @Override
    public void startActivity(Intent intent) {
        // TODO Auto-generated method stub
        super.startActivity(intent);
        AugAppLog.d("ejs startActivity ********************* " + intent);
    }

    private void init() {

        AugAppLog.d( "BaseAugmaticActivity.init");
        prevlayout = (RelativeLayout) findViewById(getPreviewId());
        try {
            
            initCamFactory();
            
            augieScape = new AugieScapeImpl(this);

            superScape = new SuperScape(this);

            augiementFactory = new AugmaticAugiementFactory();

            modeManager = new ModeManagerImpl(this, cameraFactory, augiementFactory);
            
            modeManager.onCreate(this);
            
            cameraFactory.onCreate(augieScape, null);

            if (!setModeByIntent(null))
                modeManager.getCurrentMode().activate();

            prevlayout.setOnTouchListener(new TouchListener(augieScape));
            View b = getControlLayout();
            if (b != null) {
                b.setOnLongClickListener(augieScape);
            }

        } catch (AugiementException e) {
            AugAppLog.e( "can not create augmatic", e);
        } catch (Throwable err) {
            AugAppLog.e( "can not create augmatic", err);
        }
    }

    @Override
    protected void onPause() {
        AugAppLog.d( getClass().getName() + " onPause");
        super.onPause();
        stopOrientationEventListener();
        augieScape.stop();
        cameraFactory.stop();
        modeManager.stop();
    }

    @Override
    protected void onResume() {
        AugAppLog.d( getClass().getName() + " onResume");
        super.onResume();
        init();
        startOrientationEventListener();
    }

    @Override
    protected void onDestroy() {
        AugAppLog.d( getClass().getName() + " onDestroy");
        super.onDestroy();
    }

    protected void enterNavMode() {

        getSupportActionBar().show();
        View v = getControlLayout();
        if (v != null) {
            Button b = (Button) v.findViewById(R.id.menuButton);
            if (b != null) b.setVisibility(View.INVISIBLE);
        }
        activateSwipeNav(true);
    }

    protected void leaveNavMode() {

        getSupportActionBar().hide();
        View v = getControlLayout();
        if (v != null) {
            Button b = (Button) v.findViewById(R.id.menuButton);
            if (b != null) b.setVisibility(View.VISIBLE);
        }
        activateSwipeNav(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        DialogFragment ald;
        switch (item.getItemId()) {
        case R.id.menu_control:
            startActivity(new Intent(this, ControlActivity.class));
            return true;
        case R.id.menu_settings:
            return true;
        case R.id.menu_hide:
            leaveNavMode();
            return true;
        case R.id.prefs:
            startActivity(new Intent(this, AugmaticPreferencesActivity.class));
            return true;
        case R.id.installShortcut:
            installShortCut();
            return true;
        case R.id.about:
            ald = new AboutDialog();
            ald.show(getSupportFragmentManager(), "About Fragment");
            return true;
        case R.id.dump:
            try {
                dump();
            } catch (CodeableException e) {
                AugAppLog.e( e.toString(), e);
            }
            return true;
        default:
            AugAppLog.d( "menu default for id " + item.getItemId());
            return super.onOptionsItemSelected(item);
        }
    }

    private void installShortCut() {

        Mode m = modeManager.getCurrentMode();
        CodeableName cn = m.getCodeableName();
        String uiname = m.getName();

//                <action android:name="com.onextent.augmatic.START_MODE" />
        final Intent shortcutIntent = new Intent(this, AugmaticActivity.class);
        shortcutIntent.setAction("com.onextent.augmatic.START_MODE");
        shortcutIntent.putExtra(INTENT_KEY_MODE_NAME, cn.toString());

        final Intent intent = new Intent();

        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);

        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, uiname + " shortcut");

        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, 
                Intent.ShortcutIconResource.fromContext(this, R.drawable.ic_launcher));
        // add the shortcut
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        sendBroadcast(intent);
    }

    private void dump() throws CodeableException {
        Code dcode = modeManager.getStore().dump();
        String pstr = modeManager.getCurrentMode().getCamera().flatten();
        dcode.put("AUGIE/DEBUG/CAMERA/FLATTEN", pstr);
        AugAppLog.d( "dcode: " + dcode);
        try {
            writeStringToFile(getOutputFile("dump_", ".json"), dcode.toString());
        } catch (Exception e) {
            AugAppLog.e( e.toString(), e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.options, menu);

        if (getControlLayout() == null) {
            MenuItem mi = menu.findItem(R.id.menu_hide);
            mi.setVisible(false);
        }

        return true;
    }

    @Override
    public ModeManager getModeManager() {

        //if (modeManager == null) throw new java.lang.NullPointerException("mode manager is null");
        if (modeManager == null) init();
        return modeManager;
    }

    private void writeStringToFile(File file, String str)
            throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(str.getBytes());
        fos.close();
        Toast.makeText(this, "file saved as " + file.getName(), Toast.LENGTH_LONG).show();
    }

    class ModeNameList extends AbstractList<String> {

        @Override
        public String get(int location) {
            try {
                List<Code> modes = modeManager.getAllModeCode();
                if (modes == null) return null;
                Code cm = modes.get(location);
                if (cm != null) {
                    return cm.getString(Codeable.UI_NAME_KEY);
                } else {
                    AugAppLog.e( "can not find mode for idx " + location);
                }
            } catch (AugieStoreException e) {
                AugAppLog.e( e.toString(), e);
            } catch (CodeableException e) {
                AugAppLog.e( e.toString(), e);
            }
            return null;
        }

        @Override
        public int size() {
            try {
                List<Code> modes = modeManager.getAllModeCode();
                if (modes == null) return 0;
                return modes.size();
            } catch (AugieStoreException e) {
                AugAppLog.e( e.toString(), e);
            }
            return 0;
        }
    }

    class ModeAdapter extends ArrayAdapter<String> {

        public ModeAdapter(Context context, List<String> list) {
            super(context, android.R.layout.simple_spinner_item, list);
        }
    }

    //
    // part to part communication must go through activity
    //
    private final Map<CodeableName, Callback> callbacks;

    @Override
    public void registerCallback(CodeableName dest, Callback cb) {

        if (callbacks.containsKey(dest))
            throw new IllegalStateException("dest already has handler"); //fail fast

        if (cb == null) callbacks.remove(cb);

        else  callbacks.put(dest, cb);
    }

    @Override
    public Code sendCode(CodeableName dest, Code code) {

        Callback cb = callbacks.get(dest);
        if (cb == null) {
            throw new NullPointerException("no callback for " + dest);
        }
        return cb.handleCode(dest, code);
    }

    @Override
    public int getOrientation() {

        return orientation;
    }

    @Override
    public void fire(Code code) {

        Set<CodeableHandler> handlers;

        try {
            handlers = handlerSets.get(code.getCodeableName());
            if (handlers != null) {
                for (CodeableHandler h : handlers) {

                    h.onCode(code);
                }
            }
        } catch (CodeableException ex) {
            AugAppLog.e( ex.toString(), ex);
        }
    }

    @Override
    public void unlisten(CodeableName name, CodeableHandler handler) {

        if (handler == null) return;

        Set<CodeableHandler> handlers = null;
        if (handlerSets.containsKey(name)) {
            handlers = handlerSets.get(name);
        } 
        if (handler != null && handlers != null)
            handlers.remove(handler);
    }

    @Override
    public void listen(CodeableName name, CodeableHandler handler) {

        if (handler == null) return;

        Set<CodeableHandler> handlers;
        if (handlerSets.containsKey(name)) {
            handlers = handlerSets.get(name);
        } else {
            handlers = new HashSet<CodeableHandler>();
            handlerSets.put(name, handlers);
        }
        handlers.add(handler);
    }

    private int currentAugiementIdx = 0;
    public int getCurrentAugiementIdx() {
        return currentAugiementIdx;
    }

    public void setCurrentAugiementIdx(int currentAugiementIdx) {
        this.currentAugiementIdx = currentAugiementIdx;
    }

    protected abstract void activateSwipeNav(boolean activate);

    protected void setMode(CodeableName cn) throws CodeableException, AugieException {

        Mode m = modeManager.getMode(cn);
        if (m == null) throw new AugieException("mode not found") {
            private static final long serialVersionUID = -6373280937418946550L;
        };
        modeManager.setCurrentMode(m);
    }

    protected final class TouchListener extends AbstractTwoFingerListener {

        TouchListener(OnTouchListener l) {
            super(l);
        }

        @Override
        protected void doit() {
            boolean navGestureEnabled = sharedPrefs.getBoolean("nav_shift_gesture", false);
            if (navGestureEnabled) enterNavMode();
        }
    };

    @Override
    public AugCameraFactory getCameraFactory() {
        return cameraFactory;
    }

    @Override
    public AugiementFactory getAugiementFactory() {
        return augiementFactory;
    }

    @Override
    public AugieScape getAugieScape() {
        return augieScape;
    }

    @Override
    public SuperScape getSuperScape() {
        return superScape;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        
        super.onNewIntent(intent);
        
        try {
            setModeByIntent(intent);
        } catch (CodeableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (AugieException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public boolean startActivityIfNeeded(Intent intent, int requestCode) {
        // TODO Auto-generated method stub
        AugAppLog.d("ejs startActivityIfNeeded *********************");
        return super.startActivityIfNeeded(intent, requestCode);
    }
}
