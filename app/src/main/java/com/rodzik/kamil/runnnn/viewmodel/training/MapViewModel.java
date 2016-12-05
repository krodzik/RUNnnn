package com.rodzik.kamil.runnnn.viewmodel.training;


import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.orhanobut.logger.Logger;
import com.rodzik.kamil.runnnn.model.LocationModel;
import com.rodzik.kamil.runnnn.model.SummaryModel;
import com.rodzik.kamil.runnnn.utils.PermissionUtils;
import com.rodzik.kamil.runnnn.utils.PixelConverterUtils;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

public class MapViewModel implements MapViewModelContract.ViewModel,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final float MAP_ZOOM_LEVEL = 17.5f;

    private Context mContext;
    private GoogleMap mMap;
    private LocationModel mLocationModel;
    private LatLng mStartLatLng;
    private LatLng mCurrentLatLng;
    private boolean mIsFirstUpdatedPoint = true;
    private PolylineOptions mPolylineOptions;

    private CompositeDisposable mDisposables = new CompositeDisposable();
    private Observable<View> mPauseButtonObservable;
    private Observable<View> mStopButtonObservable;
    private boolean mIsPaused;

    @Override
    public void setContext(Context context) {
        mContext = context;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        // TODO delete as we check it in previous activity
        if (PermissionUtils.isLocationAccessPermissionGranted(mContext)) {
            mMap = map;
            configureMap();
            mLocationModel = new LocationModel(mContext, this, this, this);
        } else {
            // TODO what happen when map start without permission
            // set text instead of map fragment
            // Show rationale and request permission.
        }
    }

    private void configureMap() {
        mMap.setPadding(0, 0, 0, (int) PixelConverterUtils.convertDpToPixel(64, mContext));
        // Disable on marker click.
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return true;
            }
        });
        configureMapOnPause();
        mPolylineOptions = new PolylineOptions().color(Color.BLUE).width(10);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location lastKnownLocation = mLocationModel.getLastKnownLocation();
        LatLng latLngLastKnownLocation = new LatLng(lastKnownLocation.getLatitude(),
                lastKnownLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngLastKnownLocation, MAP_ZOOM_LEVEL));

        mLocationModel.startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mIsFirstUpdatedPoint) {
            mStartLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            mIsFirstUpdatedPoint = false;
        }
        mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        Logger.d("Location updated. Lat : %s Lng: %s",
                String.valueOf(mCurrentLatLng.latitude), String.valueOf(mCurrentLatLng.longitude));
        updatePolyline();
        updateCamera();
        updateMarker();
    }

    private void updatePolyline() {
        mMap.clear();
        mMap.addPolyline(mPolylineOptions.add(mCurrentLatLng));
    }

    private void updateCamera() {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLatLng, MAP_ZOOM_LEVEL));
    }

    private void updateMarker() {
        mMap.addMarker(new MarkerOptions().position(mStartLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        mMap.addMarker(new MarkerOptions().position(mCurrentLatLng));
    }

    @Override
    public void setObservableOnPauseButton(Observable<View> pauseButtonObservable) {
        mPauseButtonObservable = pauseButtonObservable;
        mDisposables.add(mPauseButtonObservable.subscribeWith(new DisposableObserver<View>() {
            @Override
            public void onNext(View value) {
                onPauseButtonClick();
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        }));
    }

    @Override
    public void setObservableOnStopButton(Observable<View> stopButtonObservable) {
        mStopButtonObservable = stopButtonObservable;
        mDisposables.add(mStopButtonObservable.subscribeWith(new DisposableObserver<View>() {
            @Override
            public void onNext(View value) {
                onStopButtonClick();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        }));
    }

    private void onPauseButtonClick() {
        if (mLocationModel != null) {
            Logger.d("Pause map");
            if (mIsPaused) {
                mLocationModel.startLocationUpdates();
                mIsPaused = false;
                configureMapOnPause();
            } else {
                mLocationModel.stopLocationUpdates();
                mIsPaused = true;
                configureMapOnPause();
            }
        }
    }

    private void configureMapOnPause() {
        mMap.getUiSettings().setAllGesturesEnabled(mIsPaused);
        mMap.setMyLocationEnabled(mIsPaused);
        mMap.getUiSettings().setZoomControlsEnabled(mIsPaused);
    }

    private void onStopButtonClick() {
        if (mLocationModel != null) {
            SummaryModel.getInstance().setPolylineOptions(mPolylineOptions);
        }
    }

    @Override
    public void destroy() {
        mContext = null;
        if (mLocationModel != null) {
            mLocationModel.disconnectLocationModel();
        }
        mDisposables.dispose();
    }
}
