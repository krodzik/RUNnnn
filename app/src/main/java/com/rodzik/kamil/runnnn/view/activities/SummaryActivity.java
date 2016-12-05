package com.rodzik.kamil.runnnn.view.activities;

import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.SupportMapFragment;
import com.orhanobut.logger.Logger;
import com.rodzik.kamil.runnnn.R;
import com.rodzik.kamil.runnnn.databinding.ActivitySummaryBinding;
import com.rodzik.kamil.runnnn.model.SummaryModel;
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
    }

    public void showHideFragment(final Fragment fragment) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.setCustomAnimations(android.R.animator.fade_in,
//                android.R.animator.fade_out);

        if (fragment.isHidden()) {
            ft.show(fragment);
            Logger.d("hidden", "Show");
        } else {
            ft.hide(fragment);
            Logger.d("Shown", "Hide");
        }

        ft.commit();
    }

    private void initDataBinding() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_summary);
        mViewModel = new SummaryViewModel(this, this);
        mBinding.setViewModel((SummaryViewModel) mViewModel);
    }

    private SupportMapFragment getSupportMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            Logger.d("mapFragment == null");
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map, mapFragment).commit();
        }
        return mapFragment;
    }

    @Override
    public void hideMapFragment() {
        Logger.d("Hiding map");
        if (getSupportMapFragment().getView() != null) {
            getSupportMapFragment().getView().setVisibility(View.GONE);
            showHideFragment(getSupportMapFragment());
        } else {
            Logger.d("Can't hide map. View == null.");
        }
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewModel.destroy();
    }
}
