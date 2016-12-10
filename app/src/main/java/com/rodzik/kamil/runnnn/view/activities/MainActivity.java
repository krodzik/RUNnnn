package com.rodzik.kamil.runnnn.view.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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
    }

    private void initDataBinding() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mViewModel = new MainViewModel(this);
        mBinding.setViewModel((MainViewModel) mViewModel);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewModel.destroy();
    }
}
