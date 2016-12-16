package com.rodzik.kamil.runnnn.viewmodel.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import com.rodzik.kamil.runnnn.model.HeartRateProvider;
import com.rodzik.kamil.runnnn.utils.PermissionUtils;
import com.rodzik.kamil.runnnn.view.activities.TrainingActivity;

public class MainViewModel implements MainViewModelContract.ViewModel, HeartRateProvider.HeartRateCallbacks {
    private Context mContext;
    private MainViewModelContract.View mView;
    private boolean mHeartRateToggle;
    private HeartRateProvider mHeartRateProvider;

    public MainViewModel(@NonNull Context context,
                         MainViewModelContract.View view) {
        mContext = context;
        mView = view;
        PermissionUtils.requestLocationPermission((Activity) context);

        mHeartRateProvider = new HeartRateProvider(context, this);
    }

    public void onStartButtonClicked(View view) {
        Intent intent = new Intent(mContext, TrainingActivity.class);
        intent.putExtra("MAP", PermissionUtils.isLocationAccessPermissionGranted(mContext));
        intent.putExtra("HEART_RATE", mHeartRateToggle);
        mContext.startActivity(intent);
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
