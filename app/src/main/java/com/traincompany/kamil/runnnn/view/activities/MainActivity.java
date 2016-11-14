package com.traincompany.kamil.runnnn.view.activities;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.traincompany.kamil.runnnn.R;
import com.traincompany.kamil.runnnn.databinding.ActivityMainBinding;
import com.traincompany.kamil.runnnn.viewmodel.main.MainViewModel;
import com.traincompany.kamil.runnnn.viewmodel.main.MainViewModelContract;

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
