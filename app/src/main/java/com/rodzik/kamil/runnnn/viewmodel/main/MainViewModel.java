package com.rodzik.kamil.runnnn.viewmodel.main;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.orhanobut.logger.Logger;
import com.rodzik.kamil.runnnn.R;
import com.rodzik.kamil.runnnn.data.HeartRateProvider;
import com.rodzik.kamil.runnnn.data.LocationProvider;
import com.rodzik.kamil.runnnn.utils.PermissionUtils;
import com.rodzik.kamil.runnnn.view.activities.TrainingActivity;

import static android.content.Context.MODE_PRIVATE;

public class MainViewModel implements MainViewModelContract.ViewModel,
        HeartRateProvider.HeartRateCallbacks, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public ObservableBoolean gpsChecked;
    public ObservableBoolean heartRateChecked;

    private LocationProvider mLocationProvider;

    private Context mContext;
    private MainViewModelContract.View mView;
    private HeartRateProvider mHeartRateProvider;
    private String mBluetoothDeviceAddress;
    private SharedPreferences mSharedPreferences;

    public MainViewModel(@NonNull Context context,
                         MainViewModelContract.View view) {
        mContext = context;
        mView = view;
        gpsChecked = new ObservableBoolean(false);
        heartRateChecked = new ObservableBoolean(false);
        mSharedPreferences = context.getSharedPreferences(context
                .getString(R.string.shared_preferences_key), MODE_PRIVATE);
    }

    public void onStartButtonClicked(Context context) {
        Intent intent = new Intent(context, TrainingActivity.class);
        intent.putExtra("MAP", gpsChecked.get());
        intent.putExtra("HEART_RATE", heartRateChecked.get());
        context.startActivity(intent);
    }

    public void onGpsCheckedChanged(boolean isChecked, Context context) {
        if (!isChecked) {
            return;
        }
        if (!PermissionUtils.isLocationAccessPermissionGranted(context)) {
            mView.requestLocationAccessPermission();
        } else {
            // Check if required resolution is meet.
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
        Logger.e("Connection to google services failed");
        Toast.makeText(mContext, R.string.errorCannotConnectToGoogleService, Toast.LENGTH_SHORT).show();
        gpsChecked.set(false);
    }

    public void onHeartRateCheckedChanged(boolean isChecked, Context context) {
        if (!isChecked) {
            if (mHeartRateProvider != null) {
                mHeartRateProvider.disconnectBluetoothDevice();
            }
            return;
        }
        // Check if Bluetooth is supported and enabled.
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth.
            Toast.makeText(context, R.string.error_bluetooth_le_not_supported, Toast.LENGTH_SHORT).show();
            mView.setHeartRateSwitchOff();
            return;
        }
        if (checkForSavedBluetoothDevices()) {
            if (!mBluetoothAdapter.isEnabled()) {
                // Bluetooth is not enable.
                mView.requestEnableBluetooth();
                return;
            }
            // Show progress bar. Trying to connect to device right now.
            mView.showProgressDialog();
            connectToBluetoothDevice();
        } else {
            mView.showFirstNeedToAddDeviceDialog();
        }
    }

    private boolean checkForSavedBluetoothDevices() {
        mBluetoothDeviceAddress =
                mSharedPreferences.getString(mContext.getString(R.string.saved_bluetooth_device_address),
                        "empty");
        Logger.d(mBluetoothDeviceAddress);
        if (mBluetoothDeviceAddress.compareTo("empty") == 0 ||
                !BluetoothAdapter.checkBluetoothAddress(mBluetoothDeviceAddress)) {
            return false;
        }
        return true;
    }

    @Override
    public void connectToBluetoothDevice() {
        if (mHeartRateProvider == null) {
            mHeartRateProvider = new HeartRateProvider(mContext, this, true);
        } else {
            mHeartRateProvider.connectToBluetoothDevice(mBluetoothDeviceAddress);
        }
    }

    @Override
    public void destroy() {
        if (mHeartRateProvider != null) {
            mHeartRateProvider.destroy();
        }
        if (mLocationProvider != null) {
            mLocationProvider.disconnectLocationProvider();
        }
        mContext = null;
    }


    // Callbacks from HearRateProvider
    @Override
    public void connected() {
        mView.dismissProgressDialog();
    }

    @Override
    public void disconnected() {
        if (heartRateChecked.get()) {
            mView.showCannotConnectToDeviceDialog();
        }
    }

    @Override
    public void serviceDiscovered() {

    }

    @Override
    public void heartRateUpdate(String data) {

    }

    @Override
    public void connectionTimeout() {
        if (heartRateChecked.get()) {
            mView.dismissProgressDialog();
            mView.showCannotConnectToDeviceDialog();
        }
    }
}
