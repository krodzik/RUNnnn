package com.rodzik.kamil.runnnn.viewmodel.training;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.orhanobut.logger.Logger;

public class MapViewModel implements MapViewModelContract.ViewModel, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private GoogleMap mMap;

    public MapViewModel(@NonNull Context context,
                        @NonNull SupportMapFragment mapFragment) {

        mContext = context;
        mapFragment.getMapAsync(this);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mLastLocation = new Location("Me");
        mLastLocation.setLatitude(53.446990);
        mLastLocation.setLongitude(14.492432);

        mGoogleApiClient.connect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Configure map
        //mMap.getUiSettings().setAllGesturesEnabled(false);

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
            Toast.makeText(mContext, "Permission denied. You can't use maps.", Toast.LENGTH_SHORT).show();
        }

//        LatLng zut = new LatLng(53.446990, 14.492432);
//        mMap.addMarker(new MarkerOptions().position(zut).title("ZUT"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(zut,17.0f));
//        LatLng currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
//        Logger.d("What is used");
//        Logger.d(String.valueOf(mLastLocation.getLatitude()));
//        Logger.d(String.valueOf(mLastLocation.getLongitude()));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17.0f));
    }

    @Override
    public void destroy() {
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        Logger.d("From Google Services");
        Logger.d(String.valueOf(mLastLocation.getLatitude()));
        Logger.d(String.valueOf(mLastLocation.getLongitude()));

        if (mMap != null) {
            LatLng currentLocation = new LatLng(mLastLocation.getLongitude(), mLastLocation.getLatitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17.0f));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
