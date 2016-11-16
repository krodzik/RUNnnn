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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.orhanobut.logger.Logger;
import com.rodzik.kamil.runnnn.R;
import com.rodzik.kamil.runnnn.databinding.FragmentMapBinding;
import com.rodzik.kamil.runnnn.viewmodel.training.MapViewModel;
import com.rodzik.kamil.runnnn.viewmodel.training.MapViewModelContract;


public class MapFragment extends Fragment {

    private Context mContext;
    private FragmentMapBinding mBinding;
    private MapViewModelContract.ViewModel mViewModel;

    public MapFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        GoogleMapOptions options = new GoogleMapOptions();
        options.mapType(GoogleMap.MAP_TYPE_NORMAL)
                .compassEnabled(true)
                .rotateGesturesEnabled(true)
                .tiltGesturesEnabled(false);

        if (mapFragment == null) {
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mapFragment = SupportMapFragment.newInstance(options);
            fragmentTransaction.replace(R.id.map, mapFragment).commit();
        }

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false);
        View view = mBinding.getRoot();
        mViewModel = new MapViewModel(mContext, mapFragment);
        mBinding.setViewModel((MapViewModel) mViewModel);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModel.destroy();
    }
}
