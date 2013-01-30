package com.onextent.augie.ments;

import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import com.onextent.android.codeable.Code;
import com.onextent.android.codeable.CodeableException;
import com.onextent.android.codeable.CodeableName;
import com.onextent.android.codeable.EventManager;
import com.onextent.android.codeable.JSONCoder;
import com.onextent.augie.AugLog;
import com.onextent.augie.AugieScape;
import com.onextent.augie.Augiement;
import com.onextent.augie.AugiementException;
import com.onextent.augie.AugiementName;

public class GPS implements Augiement, LocationListener {

    private static final String DESCRIPTION = "GPS coordinates for photo EXIF data.";
    public static final CodeableName AUGIE_NAME = new AugiementName("AUGIE/FEATURES/GPS");
    private static final String UI_NAME = "GPS";

    private boolean enabled;

    private LocationManager locationManager;
    private String provider;
    private EventManager em;

    private double longitude = 0;
    private double latitude = 0;
    private AugieScape augieScape;

    private final static Set<CodeableName> deps;
    static {
        //deps = new HashSet<CodeableName>();
        //deps.add(Shutter.AUGIE_NAME);
        deps = null;
    }

    @Override
    public CodeableName getCodeableName() {

        return AUGIE_NAME;
    }

    @Override
    public Code getCode() throws CodeableException {
        return null;
    }

    @Override
    public void setCode(Code code) throws CodeableException {
    }

    @Override
    public void updateCanvas() { }

    @Override
    public void clear() { }

    @Override
    public void stop() {
        if (locationManager != null && enabled) {
            
            locationManager.removeUpdates(this);
            locationManager = null;
        }
    }

    @Override
    public void resume() {
        Activity a = (Activity) augieScape.getContext();
        em = (EventManager) a;
        locationManager = (LocationManager) a.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager == null) return;

        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (provider != null) {

            enabled = locationManager .isProviderEnabled(provider);
            Location location = locationManager.getLastKnownLocation(provider);

            if (location != null) {
                AugLog.d( "Provider " + provider + " has been selected.");
                onLocationChanged(location);
            }
        }
        if (locationManager != null && enabled)
            locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    @Override
    public void onCreate(AugieScape av, Set<Augiement> helpers) throws AugiementException {
        augieScape = av;
    }

    @Override
    public DialogFragment getUI() {

        return new GPSDialog();
    }

    public Meta getMeta() {
        return META;
    }

    public final static Meta META = new Meta() {

        @Override
        public Class<? extends Augiement> getAugiementClass() {

            return GPS.class;
        }

            @Override
            public int getMinSdkVer() {
                return 0;
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

    public static final String LATITUDE_KEY = "latitude";
    public static final String LONGITUDE_KEY = "longitude";
    public static final CodeableName GPS_UPDATE_AUGIE_NAME = new AugiementName("AUGIE/FEATURES/GPS/GPS_UPDATE");
    @Override
    public void onLocationChanged(Location location) {
        Code code = JSONCoder.newCode();
        try {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            code.put(GPS_UPDATE_AUGIE_NAME);
            code.put(LATITUDE_KEY, latitude);
            code.put(LONGITUDE_KEY, longitude);
            em.fire(code);
        } catch (CodeableException e) {
            AugLog.e( e.toString(), e);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText((Context) em, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText((Context) em, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getProvider() {

        return provider;
    }
}
