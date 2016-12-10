package com.rodzik.kamil.runnnn.view.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rodzik.kamil.runnnn.R;
import com.rodzik.kamil.runnnn.databinding.FragmentDataBinding;
import com.rodzik.kamil.runnnn.viewmodel.training.DataViewModel;
import com.rodzik.kamil.runnnn.viewmodel.training.DataViewModelContract;

import io.reactivex.Observable;


public class DataFragment extends Fragment {

    private FragmentDataBinding mBinding;
    private DataViewModelContract.ViewModel mViewModel = new DataViewModel();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_data, container, false);
        View view = mBinding.getRoot();
        mViewModel.setupChronometer(mBinding.chronometer);
        mBinding.setViewModel((DataViewModel) mViewModel);
        return view;
    }

    public void setObservableOnPauseButton(Observable<View> observable) {
        if (mViewModel != null) {
            mViewModel.setObservableOnPauseButton(observable);
        }
    }

    public void setObservableOnStopButton(Observable<View> observable) {
        if (mViewModel != null) {
            mViewModel.setObservableOnStopButton(observable);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModel.destroy();
    }
}
