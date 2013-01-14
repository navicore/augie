package com.onextent.augmatic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.onextent.augie.AugieActivity;
import com.onextent.augie.AugieException;
import com.onextent.augie.AugieScape;
import com.onextent.augie.AugieStoreException;
import com.onextent.augie.AugiementException;
import com.onextent.augie.AugiementFactory;
import com.onextent.augie.Mode;
import com.onextent.augie.ModeManager;
import com.onextent.augie.camera.AugCameraFactory;
import com.onextent.augie.camera.CameraName;
import com.onextent.augie.camera.impl.AugCameraFactoryImpl;
import com.onextent.augie.impl.AugieScapeImpl;
import com.onextent.augie.impl.ModeManagerImpl;
import com.onextent.augmatic.camera.CameraSelectionDialog;
import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.Codeable;
import com.onextent.android.codeable.CodeableException;
import com.onextent.android.codeable.CodeableName;

public abstract class BaseAugmaticActivity 
                      extends SherlockFragmentActivity 
                      implements AugieActivity {

    private AugCameraFactory    cameraFactory;
    private AugiementFactory    augiementFactory;
    protected ModeManager       modeManager;
    private AugieScape          augieScape;
    
    public BaseAugmaticActivity() {
        super();
        callbacks = new HashMap<CodeableName, Callback>();
    }

    private static File getOutputFile(String pref, String suffix)
            throws AugieException {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Augie");

        if ( !mediaStorageDir.exists() ){
            if (! mediaStorageDir.mkdirs()){
                Log.e(Codeable.TAG, "failed to create directory");
                throw new AugieException("can not create dir") {
                    private static final long serialVersionUID = 1L; };
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + pref + timeStamp + suffix);

        return mediaFile;
    }

    //begin subclass methods
    protected abstract Button getMenuButton();

    protected abstract int getLayoutId();

    protected abstract int getPreviewId();

    protected abstract void configMenuButton();
    //end subclass methods

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        try {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        getWindow().requestFeature(Window.FEATURE_ACTION_MODE_OVERLAY);

        setContentView(getLayoutId());

        init();

            configMenuButton();
            getSupportActionBar().setBackgroundDrawable(null);

        } catch (Throwable err) {
            Log.e(Codeable.TAG, "can not create augmatic", err);
        }
    }

    private void init() {

        Log.d(Codeable.TAG, "BaseAugmaticActivity.init");
        RelativeLayout prevlayout = (RelativeLayout) findViewById(getPreviewId());
        android.view.ViewGroup.LayoutParams p = prevlayout.getLayoutParams();
        try {
            augieScape = new AugieScapeImpl(this);
            augiementFactory = new AugmaticAugiementFactory();

            cameraFactory = new AugCameraFactoryImpl();
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
                    cameraFactory.registerCamera(i, new CameraName("AUGIE/FEATURES/CAMERA/CAMERA_" + i), "Camera " + i);
                }
            }

            modeManager = new ModeManagerImpl(this, 
                    augieScape, 
                    cameraFactory, 
                    augiementFactory, prevlayout, getMenuButton());
        } catch (Exception e) {
            throw new java.lang.IllegalStateException(e);
        }

        try {
            modeManager.onCreate(this);
        } catch (AugieStoreException e1) {
            Log.e(Codeable.TAG, e1.toString(), e1);
            return;
        } catch (CodeableException e) {
            Log.e(Codeable.TAG, e.toString(), e);
            return;
        }

        try {
            cameraFactory.onCreate(augieScape, null);

            modeManager.getCurrentMode().activate();

            prevlayout.setOnTouchListener(augieScape);

        } catch (AugiementException e) {
            Log.e(Codeable.TAG, "can not create augmatic", e);
        } catch (Throwable err) {
            Log.e(Codeable.TAG, "can not create augmatic", err);
        }
    }

    @Override
    protected void onStop() {
        Log.d(Codeable.TAG, getClass().getName() + " onStop");
        _paws();
        super.onStop();
    }

    private void _paws() {
        try {
            Mode m = modeManager.getCurrentMode();
            if (m != null) m.deactivate();
        } catch (AugieException e) {
            Log.e(Codeable.TAG, e.toString(), e);
        }
        augieScape.stop();
        cameraFactory.stop();
        modeManager.stop();
    }

    @Override
    protected void onPause() {
        Log.d(Codeable.TAG, getClass().getName() + " onPause");
        _paws();
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d(Codeable.TAG, getClass().getName() + " onResume");
        super.onResume();
        init();
    }

    @Override
    protected void onDestroy() {
        Log.d(Codeable.TAG, getClass().getName() + " onStop");
        modeManager.stop();
        this.onStop();
    }

    private boolean isDualPane() {

        //Log.d(TAG, "ejs w: " + augview.getWidth() );
        return augieScape.getWidth() > 300;
        //return augview.getWidth() > 1000;

        /*
        View detailsFrame = null;
        ViewGroup v = (ViewGroup) findViewById(R.layout.settings);
        Log.d(Codeable.TAG, "ejs isDualPane v is null: " + (v == null));
        if (v != null) {
            detailsFrame = v.findViewById(R.id.module_details_holder);
            Log.d(Codeable.TAG, "ejs isDualPane d is null: " + (detailsFrame == null));
        }
        boolean ret = detailsFrame != null;
        Log.d(Codeable.TAG, "isDualPane: " + ret);
        return ret;
         */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        SherlockDialogFragment ald;
        switch (item.getItemId()) {
        case R.id.menu_camera:
            //check if tablet
            if (isDualPane()) {
                startActivity(new Intent(this, CameraSettingsActivity.class));
            } else {
                ald = new CameraSelectionDialog();
                ald.show(getSupportFragmentManager(), "Camera Fragment");
            }
            return true;
        case R.id.menu_settings:
            return true;
        case R.id.menu_overlays:
            if (isDualPane()) {
                startActivity(new Intent(this, AugiementSettingsActivity.class));
            } else {
                ald = new AugiementListDialog();
                ald.show(getSupportFragmentManager(), "Augielay Fragment");
            }
            return true;
        case R.id.menu_hide:
            getSupportActionBar().hide();
            Button b = getMenuButton();
            if (b != null) b.setVisibility(View.VISIBLE);
            return true;
        case R.id.mode_add:
            ald = new NewModeDialog();
            ald.show(getSupportFragmentManager(), "New Mode Fragment");
            return true;
        case R.id.mode_del:
            ald = new DeleteModeDialog();
            ald.show(getSupportFragmentManager(), "Delete Mode Fragment");
            return true;
        case R.id.dump:
            try {
                dump();
            } catch (CodeableException e) {
                Log.e(Codeable.TAG, e.toString(), e);
            }
            return true;
        default:
            Log.d(Codeable.TAG, "menu default for id " + item.getItemId());
            return super.onOptionsItemSelected(item);
        }
    }

    private void dump() throws CodeableException {
        Code dcode = modeManager.getStore().dump();
        String pstr = modeManager.getCurrentMode().getCamera().flatten();
        dcode.put("AUGIE/DEBUG/CAMERA/FLATTEN", pstr);
        Log.d(Codeable.TAG, "dcode: " + dcode);
        try {
            writeStringToFile(getOutputFile("dump_", ".json"), dcode.toString());
        } catch (Exception e) {
            Log.e(Codeable.TAG, e.toString(), e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.options, menu);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        final BaseAdapter adapter = new ModeAdapter(this, new ModeNameList());

        OnNavigationListener navl = new OnNavigationListener() {

            @Override
            public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                try {
                    Code c = modeManager.getAllModeCode().get(itemPosition);
                    Mode cm = modeManager.getCurrentMode();
                    if (cm == null || !cm.getCodeableName().toString().equals(c.getString(Codeable.CODEABLE_NAME_KEY))) {
                        Mode m = modeManager.newMode();
                        m.setCode(c);
                        getModeManager().setCurrentMode(m);
                    }
                } catch (AugieStoreException e) {
                    Log.e(Codeable.TAG, e.toString(), e);
                } catch (AugieException e) {
                    Log.e(Codeable.TAG, e.toString(), e);
                } catch (CodeableException e) {
                    Log.e(Codeable.TAG, e.toString(), e);
                }
                return true;
            }
        };

        actionBar.setListNavigationCallbacks(adapter, navl);
        actionBar.setSelectedNavigationItem(modeManager.getCurrentModeIdx());

        return true;
    }

    /* (non-Javadoc)
     * @see com.onextent.augmatic.AugieActivity#getModeManager()
     */
    @Override
    public ModeManager getModeManager() {

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
                    Log.e(Codeable.TAG, "can not find mode for idx " + location);
                }
            } catch (AugieStoreException e) {
                Log.e(Codeable.TAG, e.toString(), e);
            } catch (CodeableException e) {
                Log.e(Codeable.TAG, e.toString(), e);
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
                Log.e(Codeable.TAG, e.toString(), e);
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
}