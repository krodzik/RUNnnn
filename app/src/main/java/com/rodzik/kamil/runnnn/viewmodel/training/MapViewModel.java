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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.rodzik.kamil.runnnn.MapManager;
import com.rodzik.kamil.runnnn.model.LocationModel;
import com.rodzik.kamil.runnnn.model.SummaryModel;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

public class MapViewModel implements MapViewModelContract.ViewModel,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private Context mContext;
    private LocationModel mLocationModel;
    private PolylineOptions mPolylineOptions;

    private CompositeDisposable mDisposables = new CompositeDisposable();
    private Observable<View> mPauseButtonObservable;
    private Observable<View> mStopButtonObservable;
    private boolean mIsPaused;

    private MapManager mMap;

    @Override
    public void setContext(Context context) {
        mContext = context;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mPolylineOptions = new PolylineOptions().color(Color.BLUE).width(10);
        mMap = new MapManager(googleMap);
        mMap.configureMapInTraining(mContext);
        mLocationModel = new LocationModel(mContext, this, this, this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location lastKnownLocation = mLocationModel.getLastKnownLocation();
        mMap.moveToLatLng(new LatLng(lastKnownLocation.getLatitude(),
                lastKnownLocation.getLongitude()));
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
        mPolylineOptions.add(new LatLng(location.getLatitude(), location.getLongitude()));
        mMap.updateMap(mPolylineOptions);

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
            if (mIsPaused) {
                mLocationModel.startLocationUpdates();
                mIsPaused = false;
                mMap.configureMapOnPause(false);
            } else {
                mLocationModel.stopLocationUpdates();
                mIsPaused = true;
                mMap.configureMapOnPause(true);
            }
        }
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
