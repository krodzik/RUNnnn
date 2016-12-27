package com.rodzik.kamil.runnnn.data;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;

import com.rodzik.kamil.runnnn.service.BluetoothLeService;
import com.rodzik.kamil.runnnn.R;

import static android.content.Context.BIND_AUTO_CREATE;
import static android.content.Context.MODE_PRIVATE;

public class HeartRateProvider {

    private Context mContext;
    private boolean mAutoConnect;
    private HeartRateCallbacks mHeartRateCallbacks;
    private BluetoothLeService mBluetoothLeService;
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                return;
            }
            if (mAutoConnect) {
                String deviceAddress = mContext.getSharedPreferences(mContext.getString(R.string.shared_preferences_key),
                        MODE_PRIVATE).getString(mContext.getString(R.string.saved_bluetooth_device_address), "empty");
                if (mBluetoothLeService.connect(deviceAddress)) {
                    mBluetoothLeService.discoverServices();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    // ACTION_CONNECTION_TIMEOUT: cannot connect to a GATT server in specified time.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mHeartRateCallbacks.connected();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mHeartRateCallbacks.disconnected();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                mHeartRateCallbacks.serviceDiscovered();
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                mHeartRateCallbacks.heartRateUpdate(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            } else if (BluetoothLeService.ACTION_CONNECTION_TIMEOUT.equals(action)) {
                mHeartRateCallbacks.connectionTimeout();
            }
        }
    };

    public HeartRateProvider(@NonNull Context context,
                             @NonNull HeartRateCallbacks heartRateCallbacks,
                             boolean autoConnect) {
        mContext = context;
        mHeartRateCallbacks = heartRateCallbacks;
        mAutoConnect = autoConnect;

        Intent gattServiceIntent = new Intent(mContext, BluetoothLeService.class);
        mContext.bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        mContext.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    public HeartRateProvider(@NonNull Context context,
                             @NonNull HeartRateCallbacks heartRateCallbacks) {
        this(context, heartRateCallbacks, false);
    }

    public void connectToBluetoothDevice(final String deviceAddress) {
        if (!mBluetoothLeService.connect(deviceAddress)) {
            mHeartRateCallbacks.connectionTimeout();
        }
    }

    public void disconnectBluetoothDevice() {
        mBluetoothLeService.disconnect();
    }

    public void discoverServices() {
        mBluetoothLeService.discoverServices();
    }

    public void enableHeartRateUpdates(boolean enabled) {
        mBluetoothLeService.setNotificationForHeartRateCharacteristic(enabled);
    }

    public void destroy() {
        mContext.unregisterReceiver(mGattUpdateReceiver);
        mContext.unbindService(mServiceConnection);
        mBluetoothLeService = null;
        mContext = null;
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_CONNECTION_TIMEOUT);
        return intentFilter;
    }

    public interface HeartRateCallbacks {
        void connected();

        void disconnected();

        void serviceDiscovered();

        void heartRateUpdate(String data);

        void connectionTimeout();
    }
}
