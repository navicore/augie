package com.onextent.augie.camera.shutter;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.graphics.Color;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;

import com.onextent.android.codeable.Code;
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

public class RoboShutter implements Augiement, OnLongClickListener {

    public static final CodeableName AUGIE_NAME = new AugiementName("AUGIE/FEATURES/SHUTTER/ROBO");
    public static final String UI_NAME = "Robo Shutter";
    public static final String DESCRIPTION = "Operates shutter at timed intervals.";

    private AugieScape augieScape;
    private Shutter shutter;
    private int initInterval = 0; //for now in seconds
    private int interval = 10; //for now in seconds
    private int duration = 1; //for now in minutes
    private long startTime = 0;

    private boolean blackout;
    private boolean running;

    final static Set<CodeableName> deps;
    static {
        deps = new HashSet<CodeableName>();
        deps.add(Shutter.AUGIE_NAME);
    }
    private ScheduledThreadPoolExecutor runner;

    @Override
    public CodeableName getCodeableName() {
        return AUGIE_NAME;
    }

    @Override
    public Code getCode() throws CodeableException {
        Code code = JSONCoder.newCode();
        code.put(AUGIE_NAME);
        code.put("blackout", blackout);
        code.put("interval", interval);
        code.put("duration", duration);
        return code;
    }

    @Override
    public void setCode(Code code) throws CodeableException {
        if (code == null) return;
        if (code.has("blackout")) blackout = code.getBoolean("blackout");
        if (code.has("interval")) interval = code.getInt("interval");
        if (code.has("duration")) duration = code.getInt("duration");
    }

    @Override
    public void updateCanvas() { 

        if (running && augieScape != null && blackout) {
            augieScape.getCanvas().drawColor(Color.BLACK);
        }
    }

    @Override
    public void clear() { }

    @Override
    public void stop() {

        runner.shutdown();
        runner = null;
    }

    @Override
    public void resume() {

        runner = new ScheduledThreadPoolExecutor(2);
    }

    @Override
    public void onCreate(AugieScape av, Set<Augiement> helpers) throws AugiementException {

        augieScape = av;
        for (Augiement a: helpers) {

            if (a instanceof Shutter) {
                shutter = (Shutter) a;
            }
        }
        if (shutter == null) throw new AugiementException("no shutter found");
    }

    @Override
    public DialogFragment getUI() {

        return new RoboShutterDialog();
    }

    @Override
    public Meta getMeta() {

        return META;
    }

    public static final Meta META =
            new Augiement.Meta() {

        @Override
        public Class<? extends Augiement> getAugiementClass() {

            return RoboShutter.class;
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
            return deps;
        }
    };

    public synchronized boolean isBlackout() {
        return blackout;
    }

    public synchronized void setBlackout(boolean blackout) {
        this.blackout = blackout;
    }

    public synchronized int getInterval() {
        return interval;
    }

    public synchronized void setInterval(int interval) {
        this.interval = interval;
    }

    public synchronized int getDuration() {
        return duration;
    }

    public synchronized void setDuration(int duration) {
        this.duration = duration;
    }

    private final AugPictureCallback myCb = new AugPictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, AugCamera c) {
            AugLog.d( "robo shutter took pic");
            long now = System.currentTimeMillis();
            if (isRunning() && (getStarttime() + (getDuration() * 1000 * 60) < now)) {
                stopRobo();
                AugLog.d( "robo shutter stopped / duration expired");
            } else {
                setTaskFuture(runner.schedule(task, interval, TimeUnit.SECONDS));
            }
        }
    };

    private final Runnable task = new Runnable() {

        @Override
        public void run() {
            try {
                shutter.takePicture(myCb);
            } catch (AugCameraException e) {
                Log.w(TAG, "could not take pic: " + e.toString());
            } catch (Throwable e) {
                AugLog.e( e.toString(), e);
                stopRobo();
            }
        }
    };

    private void stopRobo() {
        if (isRunning()) {
            AugLog.d( "stopping robo shutter");
            setRunning(false);

            ScheduledFuture<?> t = getTaskFuture();
            if (t != null) {
                t.cancel(false);
                setTaskFuture(null);
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (isRunning()) {
            stopRobo();

        } else {
            setRunning(true);
            setStattime(System.currentTimeMillis());
            setTaskFuture(runner.schedule(task, initInterval, TimeUnit.SECONDS));
            AugLog.d( "robo shutter started. init: " + initInterval + " interval:" + interval + " duration: " + duration);
        }
        return true;
    }

    private ScheduledFuture<?> taskFuture;
    private synchronized void setTaskFuture(ScheduledFuture<?> t) {
        taskFuture = t;
    }
    private synchronized ScheduledFuture<?> getTaskFuture() {
        return taskFuture;
    }
    private synchronized boolean isRunning() {
        return running;
    }
    private synchronized void setRunning(boolean r) {
        running = r;
    }

    private synchronized long getStarttime() {
        return startTime;
    }
    private synchronized void setStattime(long s) {
        startTime = s;
    }

    public synchronized int getInitInterval() {
        return initInterval;
    }

    public synchronized void setInitInterval(int initInterval) {
        this.initInterval = initInterval;
    }
}
