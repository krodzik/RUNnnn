package com.rodzik.kamil.runnnn.view.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.rodzik.kamil.runnnn.R;
import com.rodzik.kamil.runnnn.databinding.ActivityMainBinding;
import com.rodzik.kamil.runnnn.data.LocationProvider;
import com.rodzik.kamil.runnnn.viewmodel.main.MainViewModel;
import com.rodzik.kamil.runnnn.viewmodel.main.MainViewModelContract;

public class MainActivity extends AppCompatActivity implements MainViewModelContract.View {

    private static final int REQUEST_ACCESS_LOCATION = 0;
    private static final int REQUEST_ENABLE_BT = 1;

    private ActivityMainBinding mBinding;
    private MainViewModelContract.ViewModel mViewModel;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataBinding();
        setupToolbar();
        mBinding.gpsSwitch.setChecked(false);
        mBinding.heartRateSwitch.setChecked(false);
        setupProgressDialog();
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

    private void setupProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.connecting));
        mProgressDialog.setCancelable(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;

            case R.id.action_devices:
                if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                    Intent intent = new Intent(this, ConnectDeviceActivity.class);
                    this.startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.error_bluetooth_le_not_supported, Toast.LENGTH_LONG).show();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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
                    // Imitating toggle switch to check again for location resolution.
                    mBinding.gpsSwitch.setChecked(false);
                    mBinding.gpsSwitch.toggle();
                } else {
                    mBinding.gpsSwitch.setChecked(false);
                }
            }
        }
    }

    @Override
    public void requestEnableBluetooth() {
        mBinding.heartRateSwitch.setChecked(false);
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            return;
        } else if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
            // Show progress bar. Trying to connect to device right now.
            showProgressDialog();
            mBinding.heartRateSwitch.setChecked(true);
        } else if (requestCode == LocationProvider.REQUEST_RESOLUTION_REQUIRED &&
                resultCode == Activity.RESULT_CANCELED) {
            mBinding.gpsSwitch.setChecked(false);
            return;
        } else if (requestCode == LocationProvider.REQUEST_RESOLUTION_REQUIRED &&
                resultCode == Activity.RESULT_OK) {
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void showProgressDialog() {
        if (mProgressDialog.isShowing()) {
            return;
        }
        mProgressDialog.show();
    }

    @Override
    public void dismissProgressDialog() {
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void showFirstNeedToAddDeviceDialog() {
        mBinding.heartRateSwitch.setChecked(false);
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

    @Override
    public void showCannotConnectToDeviceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.cantConnectDeviceInfoTitle);
        builder.setMessage(R.string.cantConnectDeviceInfoMessage);
        builder.setPositiveButton(R.string.cantConnectDeviceInfoPositive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showProgressDialog();
                mViewModel.connectToBluetoothDevice();
            }
        });
        builder.setNegativeButton(R.string.cantConnectDeviceInfoNegative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setHeartRateSwitchOff();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    @Override
    public void setHeartRateSwitchOff() {
        mBinding.heartRateSwitch.setChecked(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewModel.destroy();
    }
}
