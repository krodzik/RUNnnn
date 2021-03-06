package com.rodzik.kamil.runnnn.data;


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

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

@SuppressWarnings("MissingPermission")
public class LocationProvider implements ResultCallback<LocationSettingsResult> {

    public static final int REQUEST_RESOLUTION_REQUIRED = 2;

    private final long UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    private final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private final float SMALLEST_DISPLACEMENT = 0;  // Default is 0.

    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private GoogleApiClient.ConnectionCallbacks mConnectionCallbacks;
    private GoogleApiClient.OnConnectionFailedListener mOnConnectionFailedListener;

    private static LocationListener mOnLocationChangedListener;

    public LocationProvider(@NonNull Context context,
                            GoogleApiClient.ConnectionCallbacks connectionCallbacks,
                            GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
        mContext = context;
        mConnectionCallbacks = connectionCallbacks;
        mOnConnectionFailedListener = onConnectionFailedListener;

        createInstanceGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
        checkLocationSettings();
        mGoogleApiClient.connect();
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
        mLocationRequest.setSmallestDisplacement(SMALLEST_DISPLACEMENT);
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
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {
                    status.startResolutionForResult((Activity) mContext, REQUEST_RESOLUTION_REQUIRED);
                } catch (IntentSender.SendIntentException e) {
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                break;
        }
    }

    private static Observable<Location> locationObservable = Observable.create(new ObservableOnSubscribe<Location>() {
        @Override
        public void subscribe(ObservableEmitter<Location> e) throws Exception {
            mOnLocationChangedListener = location -> {
                // TODO I can return LatLng from here. One transformation instead of 2 (or more)
                if (e.isDisposed())
                    return;
                e.onNext(location);
            };
        }
    }).share();

    public static Observable<Location> getLocationObservable() {
        return locationObservable;
    }

    public void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, mOnLocationChangedListener);
    }

    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mOnLocationChangedListener);
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

    public void disconnectLocationProvider() {
        if (mOnLocationChangedListener != null) {
            stopLocationUpdates();
        }
        mGoogleApiClient.disconnect();
    }
}