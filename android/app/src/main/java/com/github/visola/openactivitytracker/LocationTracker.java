package com.github.visola.openactivitytracker;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

public class LocationTracker extends LocationCallback {

    private static final String LOG_TAG = LocationTracker.class.getName();

    @Override
    public void onLocationAvailability(LocationAvailability locationAvailability) {
        Log.i(LOG_TAG, "Location availability changed, available: " + locationAvailability.isLocationAvailable());
    }

    @Override
    public void onLocationResult(LocationResult locationResult) {
        Log.i(LOG_TAG, "Location result received");
        for (Location location : locationResult.getLocations()) {
            Log.i(LOG_TAG, "Location: ("+location.getLatitude()+","+location.getLongitude()+")");
        }
    }

}
