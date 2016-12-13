package com.rodzik.kamil.runnnn.view.activities;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.rodzik.kamil.runnnn.R;
import com.rodzik.kamil.runnnn.databinding.ActivityConnectedDeviceBinding;
import com.rodzik.kamil.runnnn.viewmodel.main.ConnectedDeviceViewModel;
import com.rodzik.kamil.runnnn.viewmodel.main.ConnectedDeviceViewModelContract;

public class ConnectedDeviceActivity extends AppCompatActivity {

    private ActivityConnectedDeviceBinding mBinding;
    private ConnectedDeviceViewModelContract.ViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataBinding();
        setupToolbar();
    }

    private void initDataBinding() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_connected_device);
        mViewModel = new ConnectedDeviceViewModel(this);
        mBinding.setViewModel((ConnectedDeviceViewModel) mViewModel);
    }

    private void setupToolbar() {
        Toolbar toolbar = mBinding.toolbar;
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.action_sensor);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewModel.destroy();
    }
}
