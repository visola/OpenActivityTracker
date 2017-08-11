package com.github.visola.openactivitytracker;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class LocationTrackingService extends Service {

    public static final String INITIALIZE_SERVICE = "INITIALIZE_SERVICE";
    public static final String START_TRACKING = "START_TRACKING";
    public static final String STOP_TRACKING = "STOP_TRACKING";

    private static final int FOREGROUND_NOTIFICATION_ID = 10;

    private static final String LOG_TAG = LocationTrackingService.class.getName();

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationTracker mLocationTracker;
    private boolean mTracking = false;
    private List<LocationTrackingStatusListener> statusListeners = new ArrayList<>();

    public void addTrackingStatusListener(LocationTrackingStatusListener l) {
        if (!statusListeners.contains(l)) {
            statusListeners.add(l);
        }
    }

    public boolean isTracking() {
        return mTracking;
    }

    @Override
    public void onCreate() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LocationTrackingBinder(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String data = intent.getDataString();
        if (START_TRACKING.equals(data)) {
            startLocationTracking();
        } else if (STOP_TRACKING.equals(data)) {
            stopLocationTracking();
        }
        return START_STICKY;
    }

    public void removeTrackingStatusListener(LocationTrackingStatusListener l) {
        statusListeners.remove(l);
    }

    private void setTracking(final boolean isTracking) {
        mTracking = isTracking;
        for(LocationTrackingStatusListener l : statusListeners) {
            l.statusChanged(isTracking);
        }
    }

    private LocationRequest createLocationRequest() {
        LocationRequest request = new LocationRequest();
        request.setInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return request;
    }

    private void startLocationTracking() {
        if (mLocationTracker != null) {
            return;
        }

        // TODO: This will silently fail if we don't have permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationTracker = new LocationTracker();
            mFusedLocationClient.requestLocationUpdates(
                    createLocationRequest(),
                    mLocationTracker,
                    null
            );

            startForeground(
                    FOREGROUND_NOTIFICATION_ID,
                    new Notification.Builder(this)
                            .setOngoing(true)
                            .setContentTitle("Tracking Activity")
                            .setContentText("Your activity is being tracked.")
                            .setSmallIcon(android.R.drawable.btn_star).build()
            );

            setTracking(true);
        }
    }

    private void stopLocationTracking() {
        mFusedLocationClient.removeLocationUpdates(mLocationTracker);
        mLocationTracker = null;

        setTracking(false);
        stopForeground(true);
        stopSelf();
    }

}
