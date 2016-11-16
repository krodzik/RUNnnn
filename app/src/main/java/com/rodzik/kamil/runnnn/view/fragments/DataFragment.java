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


public class DataFragment extends Fragment implements DataViewModelContract.View {

    private FragmentDataBinding mBinding;
    private DataViewModelContract.ViewModel mViewModel;
    private Context mContext;
    private DataViewModelContract.View mView = this;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public DataFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_data, container, false);
        View view = mBinding.getRoot();
        mViewModel = new DataViewModel(mContext, mView, mBinding.chronometer);
        mBinding.setViewModel((DataViewModel) mViewModel);
        return view;
    }

    @Override
    public void exitTraining() {
        getActivity().finish();
    }
}
