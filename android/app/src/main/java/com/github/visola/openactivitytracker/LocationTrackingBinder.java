package com.github.visola.openactivitytracker;

import android.location.Location;
import android.os.Binder;

public class LocationTrackingBinder extends Binder {

    private final LocationTrackingService mLocationTrackingService;

    public LocationTrackingBinder(LocationTrackingService locationTrackingService) {
        this.mLocationTrackingService = locationTrackingService;
    }

    public LocationTrackingService getTrackingService() {
        return mLocationTrackingService;
    }

}
