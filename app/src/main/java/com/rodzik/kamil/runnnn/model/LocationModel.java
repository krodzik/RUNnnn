package com.rodzik.kamil.runnnn.model;


import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.orhanobut.logger.Logger;

import static com.google.android.gms.common.api.CommonStatusCodes.RESOLUTION_REQUIRED;

@SuppressWarnings("MissingPermission")
public class LocationModel implements ResultCallback<LocationSettingsResult> {

    private final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationListener mLocationListener;
    private GoogleApiClient.ConnectionCallbacks mConnectionCallbacks;
    private GoogleApiClient.OnConnectionFailedListener mOnConnectionFailedListener;

    public LocationModel(Context context,
                         LocationListener locationListener,
                         GoogleApiClient.ConnectionCallbacks connectionCallbacks,
                         GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
        mContext = context;
        mLocationListener = locationListener;
        mConnectionCallbacks = connectionCallbacks;
        mOnConnectionFailedListener = onConnectionFailedListener;

        if (mContext != null) {
            createInstanceGoogleApiClient();
            createLocationRequest();
            buildLocationSettingsRequest();
            checkLocationSettings();
            mGoogleApiClient.connect();

        }
    }

    private void createInstanceGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addConnectionCallbacks(mConnectionCallbacks)
                    .addOnConnectionFailedListener(mOnConnectionFailedListener)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    private void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Logger.d("Location settings are not satisfied. Show the user a dialog to " +
                        "upgrade location settings ");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
//                    status.startResolutionForResult((Activity) mContext, REQUEST_CHECK_SETTINGS);
                    status.startResolutionForResult((Activity) mContext, RESOLUTION_REQUIRED);
                } catch (IntentSender.SendIntentException e) {
                    Logger.d("PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Logger.d("Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

    public void startLocationUpdates() {
        // Permission is checked in ViewModel -> This would never be called without permission.
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, mLocationListener);
    }

    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mLocationListener);
    }

    public Location getLastKnownLocation() {
        // Permission is checked in ViewModel -> This would never be called without permission.
        Location lastKnownLocation =
                LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (lastKnownLocation != null) {
            return lastKnownLocation;
        } else {
            Location fakeZutLocation = new Location("ZUT");
            fakeZutLocation.setLatitude(53.446990);
            fakeZutLocation.setLongitude(14.492432);
            return fakeZutLocation;
        }
    }

    public void disconnectLocationModel() {
        stopLocationUpdates();
        mGoogleApiClient.disconnect();
    }
}