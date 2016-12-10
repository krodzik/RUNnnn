package com.rodzik.kamil.runnnn.view.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.rodzik.kamil.runnnn.MapManager;
import com.rodzik.kamil.runnnn.R;
import com.rodzik.kamil.runnnn.model.SummaryModel;

public class MapSummaryActivity extends AppCompatActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_summary);
        getSupportMapFragment().getMapAsync(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private SupportMapFragment getSupportMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map, mapFragment).commit();
        }
        return mapFragment;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapManager mMap = new MapManager(googleMap);

        mMap.drawRoute(SummaryModel.getInstance().getPolylineOptions());
        googleMap.setOnCameraIdleListener(() -> {
            mMap.moveCameraToLatLngBounds(getBaseContext(),
                    SummaryModel.getInstance().getPolylineOptions());
            googleMap.setOnCameraIdleListener(null);
        });
    }
}
