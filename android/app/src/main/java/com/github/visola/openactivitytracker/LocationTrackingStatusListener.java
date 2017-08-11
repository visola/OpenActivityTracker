package com.github.visola.openactivitytracker;

public interface LocationTrackingStatusListener {

    /**
     * Tell listeners that the status changed.
     */
    void statusChanged(boolean isTracking);

}
