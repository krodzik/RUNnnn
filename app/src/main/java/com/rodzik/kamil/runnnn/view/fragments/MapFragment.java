package com.rodzik.kamil.runnnn.view.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;
import com.orhanobut.logger.Logger;
import com.rodzik.kamil.runnnn.R;
import com.rodzik.kamil.runnnn.databinding.FragmentMapBinding;
import com.rodzik.kamil.runnnn.viewmodel.training.MapViewModel;
import com.rodzik.kamil.runnnn.viewmodel.training.MapViewModelContract;

import io.reactivex.Observable;


public class MapFragment extends Fragment {

    //    private Context mContext;
    private FragmentMapBinding mBinding;
    private MapViewModelContract.ViewModel mViewModel = new MapViewModel();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        mContext = context;
        mViewModel.setContext(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportMapFragment().getMapAsync(googleMap -> mViewModel.onMapReady(googleMap));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false);
        View view = mBinding.getRoot();
        mBinding.setViewModel((MapViewModel) mViewModel);
        return view;
    }

    private SupportMapFragment getSupportMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map, mapFragment).commit();
        }
        return mapFragment;
    }

    public void setObservableOnPauseButton(Observable<View> observable) {
        mViewModel.setObservableOnPauseButton(observable);
    }

    public void setObservableOnStopButton(Observable<View> observable) {
        mViewModel.setObservableOnStopButton(observable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModel.destroy();
    }
}
