package com.github.visola.openactivitytracker;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements LocationTrackingStatusListener {

    private static final int PERMISSION_REQUEST_LOCATION_FINE = 1;

    private FloatingActionButton mFab;
    private LocationTrackingService mLocationTrackingService;
    private TextView mStatusText;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            LocationTrackingBinder locationTrackingBinder = (LocationTrackingBinder) binder;
            mLocationTrackingService = locationTrackingBinder.getTrackingService();
            mLocationTrackingService.addTrackingStatusListener(MainActivity.this);
            updateStatus();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mLocationTrackingService = null;
            updateStatus();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupLayout();

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        // Disable the button until we know what state we're in
        mFab.setEnabled(false);
        setupFloatingActionBar();

        mStatusText = (TextView) findViewById(R.id.status_text);
        startService();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindToService();
    }

    @Override
    protected void onDestroy() {
        unbindService();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            // TODO: Go to settings screen
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode) {
            case PERMISSION_REQUEST_LOCATION_FINE:
                /* If we didn't have permission to access GPS location when the user started tracking,
                 * a request for the permission will be made. So we need to handle the result and
                 * ask to start tracking when we get it.
                 */
                startTrackingActivity();
                break;
            default:
                // Ignore unexpected permission results
        }
    }

    @Override
    public void statusChanged(boolean isTracking) {
        updateStatus();
    }

    private void bindToService() {
        if (mLocationTrackingService == null) {
            mStatusText.setText("Binding to service...");

            Intent initializeServiceIntent = new Intent(this, LocationTrackingService.class);
            initializeServiceIntent.setData(Uri.parse(LocationTrackingService.INITIALIZE_SERVICE));
            bindService(initializeServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private boolean checkForLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        // Request permission, if we don't have it
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION_FINE);
        return false;
    }

    private boolean isTrackingActivity() {
        return mLocationTrackingService != null && mLocationTrackingService.isTracking();
    }

    private void setupFloatingActionBar() {
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTrackingActivity()) {
                    stopTrackingActivity();
                } else {
                    if (checkForLocationPermission()) {
                        startTrackingActivity();
                    }
                }
            }
        });
    }

    private void setupLayout() {
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void startService() {
        Intent initializeServiceIntent = new Intent(this, LocationTrackingService.class);
        initializeServiceIntent.setData(Uri.parse(LocationTrackingService.INITIALIZE_SERVICE));
        startService(initializeServiceIntent);
    }

    private void startTrackingActivity() {
        Intent startTrackingIntent = new Intent(this, LocationTrackingService.class);
        startTrackingIntent.setData(Uri.parse(LocationTrackingService.START_TRACKING));
        startService(startTrackingIntent);
    }

    private void stopTrackingActivity() {
        Intent stopTrackingIntent = new Intent(this, LocationTrackingService.class);
        stopTrackingIntent.setData(Uri.parse(LocationTrackingService.STOP_TRACKING));
        startService(stopTrackingIntent);
    }

    private void unbindService() {
        unbindService(mServiceConnection);
        mServiceConnection = null;
    }

    private void updateStatus() {
        if (isTrackingActivity()) {
            mFab.setImageResource(android.R.drawable.ic_media_pause);
            mStatusText.setText("Tracking activity. Hit the pause button to stop tracking.");
        } else {
            mStatusText.setText("Not tracking any activity. Hit the play button to start tracking.");
            mFab.setImageResource(android.R.drawable.ic_media_play);
        }
        mFab.setEnabled(true);
    }

}
