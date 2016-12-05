package com.rodzik.kamil.runnnn.view.activities;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.orhanobut.logger.Logger;
import com.rodzik.kamil.runnnn.R;
import com.rodzik.kamil.runnnn.model.SummaryModel;
import com.rodzik.kamil.runnnn.utils.PixelConverterUtils;

public class MapSummaryActivity extends AppCompatActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_summary);
        getSupportMapFragment().getMapAsync(this);

        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    private SupportMapFragment getSupportMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_summary);
        if (mapFragment == null) {
            Logger.d("mapFragment == null");
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map, mapFragment).commit();
        }
        return mapFragment;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap != null && !SummaryModel.getInstance().getPolylineOptions().getPoints().isEmpty()) {
            PolylineOptions polylineOptions = SummaryModel.getInstance().getPolylineOptions();
            // configure
//            googleMap.getUiSettings().setAllGesturesEnabled(false);
            // add route
            googleMap.addMarker(new MarkerOptions()
                    .position(polylineOptions.getPoints().get(0))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            googleMap.addMarker(new MarkerOptions()
                    .position(polylineOptions.getPoints().get(polylineOptions.getPoints().size() - 1)));
            googleMap.addPolyline(polylineOptions);
            // Create bounds that include all locations of the map
            LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
            for (LatLng latLng : polylineOptions.getPoints()) {
                boundsBuilder.include(latLng);
            }

            //
            googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                @Override
                public void onCameraIdle() {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(),
                            (int) PixelConverterUtils.convertDpToPixel(20, getBaseContext())));

                    googleMap.setOnCameraIdleListener(null);
                }
            });
        }
    }
}
