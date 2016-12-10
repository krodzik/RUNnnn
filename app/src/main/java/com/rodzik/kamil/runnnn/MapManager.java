package com.rodzik.kamil.runnnn;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.rodzik.kamil.runnnn.utils.PixelConverterUtils;

public class MapManager {
    private final float MAP_ZOOM_LEVEL = 17.5f;
    private final float MAP_PADDING_BOTTOM_IN_DP = 64;
    private final int MAP_PADDING_IN_DP = 30;

    private GoogleMap mMap;

    private int mMapPaddingInPx;

    public MapManager(GoogleMap map) {
        mMap = map;
    }

    public void configureMapInTraining(Context context) {
        mMap.setPadding(0, 0, 0, (int) PixelConverterUtils.convertDpToPixel(MAP_PADDING_BOTTOM_IN_DP, context));

        mMap.getUiSettings().setAllGesturesEnabled(false);
        // Disable on marker click.
        mMap.setOnMarkerClickListener(marker -> true);
    }

    public void configureMapOnPause(boolean isPaused) {
        mMap.getUiSettings().setAllGesturesEnabled(isPaused);
        mMap.setMyLocationEnabled(isPaused);
        mMap.getUiSettings().setZoomControlsEnabled(isPaused);
    }

    public void updateMap(PolylineOptions polylineOptions) {
        mMap.clear();
        drawRoute(polylineOptions);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                polylineOptions.getPoints().get(polylineOptions.getPoints().size() - 1), MAP_ZOOM_LEVEL));
    }

    public void moveToLatLng(LatLng latLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, MAP_ZOOM_LEVEL));
    }

    public void configureMapInSummary(OnMapClickListener onMapClickListener,
                                      GoogleMap.OnMarkerClickListener onMarkerClickListener) {

        mMap.getUiSettings().setAllGesturesEnabled(false);

        mMap.setOnMarkerClickListener(onMarkerClickListener);
        mMap.setOnMapClickListener(onMapClickListener);
    }

    public void moveCameraToLatLngBounds(@NonNull Context context, PolylineOptions polylineOptions) {
        LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
        for (LatLng latLng : polylineOptions.getPoints()) {
            boundsBuilder.include(latLng);
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(),
                (int) PixelConverterUtils.convertDpToPixel(MAP_PADDING_IN_DP, context)));
    }

    public void drawRoute(PolylineOptions polylineOptions) {
        mMap.addMarker(new MarkerOptions()
                .position(polylineOptions.getPoints().get(0))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        mMap.addPolyline(polylineOptions);
        mMap.addMarker(new MarkerOptions()
                .position(polylineOptions.getPoints().get(polylineOptions.getPoints().size() - 1)));
    }
}
