package com.rodzik.kamil.runnnn.view.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.rodzik.kamil.runnnn.R;
import com.rodzik.kamil.runnnn.databinding.ActivityMainBinding;
import com.rodzik.kamil.runnnn.viewmodel.main.MainViewModel;
import com.rodzik.kamil.runnnn.viewmodel.main.MainViewModelContract;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mBinding;
    private MainViewModelContract.ViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataBinding();
        setupToolbar();
    }

    private void initDataBinding() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mViewModel = new MainViewModel(this);
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
                Intent intent = new Intent(this, ConnectedDeviceActivity.class);
                this.startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewModel.destroy();
    }
}
