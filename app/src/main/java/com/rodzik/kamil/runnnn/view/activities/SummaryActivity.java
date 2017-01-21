package com.rodzik.kamil.runnnn.view.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.SupportMapFragment;
import com.rodzik.kamil.runnnn.R;
import com.rodzik.kamil.runnnn.databinding.ActivitySummaryBinding;
import com.rodzik.kamil.runnnn.model.SummarySingleton;
import com.rodzik.kamil.runnnn.viewmodel.summary.SummaryViewModel;
import com.rodzik.kamil.runnnn.viewmodel.summary.SummaryViewModelContract;

public class SummaryActivity extends AppCompatActivity implements SummaryViewModelContract.View {

    private ActivitySummaryBinding mBinding;
    private SummaryViewModelContract.ViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataBinding();
        getSupportMapFragment().getMapAsync(googleMap -> mViewModel.onMapReady(googleMap));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (SummarySingleton.getInstance().isFromDatabase()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        getSupportActionBar().setTitle(SummarySingleton.getInstance().getName());
    }

    private void initDataBinding() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_summary);
        mViewModel = new SummaryViewModel(this, this, mBinding.chart);
        mBinding.setViewModel((SummaryViewModel) mViewModel);
    }

    private SupportMapFragment getSupportMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map, mapFragment).commit();
        }
        return mapFragment;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void hideMapFragment() {
        if (getSupportMapFragment().getView() != null) {
            getSupportMapFragment().getView().setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        // To disable back button in summary.
        if (SummarySingleton.getInstance().isFromDatabase()){
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewModel.destroy();
    }
}
