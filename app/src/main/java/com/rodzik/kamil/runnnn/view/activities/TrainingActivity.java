package com.rodzik.kamil.runnnn.view.activities;

import android.databinding.DataBindingUtil;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.rodzik.kamil.runnnn.R;
import com.rodzik.kamil.runnnn.databinding.ActivityTrainingBinding;
import com.rodzik.kamil.runnnn.view.adapters.ViewPagerAdapter;
import com.rodzik.kamil.runnnn.view.fragments.DataFragment;
import com.rodzik.kamil.runnnn.view.fragments.MapFragment;
import com.rodzik.kamil.runnnn.viewmodel.training.TrainingViewModel;
import com.rodzik.kamil.runnnn.viewmodel.training.TrainingViewModelContract;

public class TrainingActivity extends AppCompatActivity {

    private ActivityTrainingBinding mBinding;
    private TrainingViewModelContract.ViewModel mViewModel;

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataBinding();

        setupViewPager(mBinding.pager);

        mBinding.tabLayout.setupWithViewPager(mBinding.pager);
    }

    private void initDataBinding() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_training);
        mViewModel = new TrainingViewModel(this);
        mBinding.setViewModel((TrainingViewModel) mViewModel);
    }

    private void setupViewPager(ViewPager viewPager)
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DataFragment(), "DATA");
        adapter.addFragment(new MapFragment(), "MAP");
        viewPager.setAdapter(adapter);
    }
}
