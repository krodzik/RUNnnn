package com.rodzik.kamil.runnnn.viewmodel.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.orhanobut.logger.Logger;
import com.rodzik.kamil.runnnn.model.HeartRateProvider;
import com.rodzik.kamil.runnnn.model.LocationProvider;
import com.rodzik.kamil.runnnn.utils.PermissionUtils;
import com.rodzik.kamil.runnnn.view.activities.TrainingActivity;

public class MainViewModel implements MainViewModelContract.ViewModel,
        HeartRateProvider.HeartRateCallbacks, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public boolean gpsChecked;

    private LocationProvider mLocationProvider;

    private Context mContext;
    private MainViewModelContract.View mView;
    private boolean mHeartRateToggle;
    private HeartRateProvider mHeartRateProvider;

    public MainViewModel(@NonNull Context context,
                         MainViewModelContract.View view) {
        mContext = context;
        mView = view;
        mHeartRateProvider = new HeartRateProvider(context, this);
    }

    public void onStartButtonClicked(View view) {
        Intent intent = new Intent(mContext, TrainingActivity.class);
        Logger.d("Starting with:\nMAP : %1$s\nHEART_RATE : %2$s", gpsChecked, mHeartRateToggle);
//        intent.putExtra("MAP", mGpsToggle);
        intent.putExtra("MAP", gpsChecked);
        intent.putExtra("HEART_RATE", mHeartRateToggle);
        mContext.startActivity(intent);
    }

    public void onGpsCheckedChanged(Context context) {
        Logger.d("%s", gpsChecked);
        if (gpsChecked) {
            return;
        }
        if (!PermissionUtils.isLocationAccessPermissionGranted(context)) {
            mView.requestLocationAccessPermission();
        } else {
            // Check if required resolution is ok
            Logger.d("Check for resolution - toggled");
            mLocationProvider = new LocationProvider(context, this, this);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void connectToBluetoothDevice(final String deviceAddress) {
        mHeartRateProvider.connectToBluetoothDevice(deviceAddress);
    }

    @Override
    public void disconnectBluetoothDevice() {
        mHeartRateProvider.disconnectBluetoothDevice();
    }

    @Override
    public void setHeartRateToggle(boolean enabled) {
        mHeartRateToggle = enabled;
    }

    @Override
    public void destroy() {
        mHeartRateProvider.destroy();
        if (mLocationProvider != null) {
            mLocationProvider.disconnectLocationProvider();
        }
        mContext = null;
    }

    @Override
    public void connected() {
        mView.connectedToLeDevice();
    }

    @Override
    public void disconnected() {
        mView.deviceDisconnected();
    }

    @Override
    public void serviceDiscovered() {

    }

    @Override
    public void heartRateUpdate(String data) {

    }

    @Override
    public void connectionTimeout() {
        mView.cannotConnectToLeDevice();
    }
}
