package com.github.visola.openruntrainer.event;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.github.visola.openruntrainer.R;
import com.github.visola.openruntrainer.activity.EditTrackActivity;

public class DrawerNavigationHandler implements NavigationView.OnNavigationItemSelectedListener {

    private final Activity owner;

    public DrawerNavigationHandler(Activity owner) {
        this.owner = owner;
    }

    public void initialize(Toolbar toolbar) {
        DrawerLayout drawer = getDrawer();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(owner, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) owner.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public boolean handleOnBackPressed() {
        DrawerLayout drawer = getDrawer();
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_tracks) {
            Intent intent = new Intent(owner, EditTrackActivity.class);
            owner.startActivity(intent);
        }

        DrawerLayout drawer = getDrawer();
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private DrawerLayout getDrawer() {
        return (DrawerLayout) owner.findViewById(R.id.drawer_layout);
    }

}
