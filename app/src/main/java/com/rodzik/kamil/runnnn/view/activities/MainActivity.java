package com.rodzik.kamil.runnnn.view.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.orhanobut.logger.Logger;
import com.rodzik.kamil.runnnn.R;
import com.rodzik.kamil.runnnn.databinding.ActivityMainBinding;
import com.rodzik.kamil.runnnn.model.LocationProvider;
import com.rodzik.kamil.runnnn.viewmodel.main.MainViewModel;
import com.rodzik.kamil.runnnn.viewmodel.main.MainViewModelContract;

public class MainActivity extends AppCompatActivity implements MainViewModelContract.View,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_ACCESS_LOCATION = 0;
    private static final int REQUEST_ENABLE_BT = 1;

    private ActivityMainBinding mBinding;
    private MainViewModelContract.ViewModel mViewModel;
    private String mBluetoothDeviceAddress;
    private SharedPreferences mSharedPreferences;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataBinding();
        setupToolbar();
        mBinding.heartRateSwitch.setChecked(false);
        mViewModel.setHeartRateToggle(false);

        mBinding.gpsSwitch.setChecked(false);

        mSharedPreferences = this.getSharedPreferences(getString(R.string.shared_preferences_key), MODE_PRIVATE);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.connecting));
        mProgressDialog.setCancelable(false);
    }

    private void initDataBinding() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mViewModel = new MainViewModel(this, this);
        mBinding.setViewModel((MainViewModel) mViewModel);
    }

    private void setupToolbar() {
        Toolbar toolbar = mBinding.toolbar;
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_devices:
                if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                    Intent intent = new Intent(this, ConnectDeviceActivity.class);
                    this.startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_LONG).show();
                }
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBinding.heartRateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mViewModel.setHeartRateToggle(isChecked);
                if (!isChecked) {
                    mViewModel.disconnectBluetoothDevice();
                    return;
                }
                if (checkForSavedBluetoothDevices()) {
                    // Check if Bluetooth is supported and enabled
                    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (mBluetoothAdapter == null) {
                        // Device does not support Bluetooth
                        Toast.makeText(getApplicationContext(), R.string.error_bluetooth_le_not_supported, Toast.LENGTH_SHORT).show();
                        mBinding.heartRateSwitch.setChecked(false);
                        return;
                    } else {
                        if (!mBluetoothAdapter.isEnabled()) {
                            // Bluetooth is not enable
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                            mBinding.heartRateSwitch.setChecked(false);
                            return;
                        }
                    }
                    // Show progress bar. Trying to connect to device right now.
                    mProgressDialog.show();
                    mViewModel.connectToBluetoothDevice(mBluetoothDeviceAddress);
                } else {
                    mBinding.heartRateSwitch.setChecked(false);
                    showFirstNeedToAddDeviceDialog();
                }
            }
        });
    }

    @Override
    public void requestLocationAccessPermission() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_ACCESS_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ACCESS_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
                    Logger.d("Permission granted - Location");
                    // Imitating toggle switch to check again for location resolution.
                    mBinding.gpsSwitch.setChecked(false);
                    mBinding.gpsSwitch.toggle();
                } else {
                    mBinding.gpsSwitch.setChecked(false);
                    // permission denied, boo!
                    Logger.d("Permission not granted - Location");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            return;
        } else if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
            Logger.d("Result Bluetooth - OK");
            // Show some progress bar. Trying to connect to device right now.
            mProgressDialog.show();
            mViewModel.connectToBluetoothDevice(mBluetoothDeviceAddress);
        } else if (requestCode == LocationProvider.REQUEST_RESOLUTION_REQUIRED &&
                resultCode == Activity.RESULT_CANCELED) {
            Logger.d("Result Resolution - Cancel");
            mBinding.gpsSwitch.setChecked(false);
            return;
        } else if (requestCode == LocationProvider.REQUEST_RESOLUTION_REQUIRED &&
                resultCode == Activity.RESULT_OK) {
            Logger.d("Result Resolution - OK");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean checkForSavedBluetoothDevices() {
        mBluetoothDeviceAddress =
                mSharedPreferences.getString(getString(R.string.saved_bluetooth_device_address),
                        "empty");
        Logger.d(mBluetoothDeviceAddress);
        if (mBluetoothDeviceAddress.compareTo("empty") == 0) {
            return false;
        }
        return true;
    }

    private void showFirstNeedToAddDeviceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.addDeviceInfoTitle);
        builder.setMessage(R.string.addDeviceInfoMessage);
        builder.setPositiveButton(R.string.addDeviceInfoPositive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void showCannotConnectToDeviceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.cantConnectDeviceInfoTitle);
        builder.setMessage(R.string.cantConnectDeviceInfoMessage);
        builder.setPositiveButton(R.string.cantConnectDeviceInfoPositive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mBinding.heartRateSwitch.setChecked(false);
                mBinding.heartRateSwitch.toggle();
            }
        });
        builder.setNegativeButton(R.string.cantConnectDeviceInfoNegative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mBinding.heartRateSwitch.setChecked(false);
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    @Override
    public void connectedToLeDevice() {
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mBinding.heartRateSwitch.setChecked(true);
        }
    }

    @Override
    public void cannotConnectToLeDevice() {
        if (mProgressDialog.isShowing() && mBinding.heartRateSwitch.isChecked()) {
            mProgressDialog.dismiss();
            showCannotConnectToDeviceDialog();
        }
    }

    @Override
    public void deviceDisconnected() {
        if (mBinding.heartRateSwitch.isChecked()) {
            showCannotConnectToDeviceDialog();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewModel.destroy();
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
}
