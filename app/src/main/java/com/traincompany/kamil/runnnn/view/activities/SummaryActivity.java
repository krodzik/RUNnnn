package com.traincompany.kamil.runnnn.view.activities;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.traincompany.kamil.runnnn.R;
import com.traincompany.kamil.runnnn.databinding.ActivitySummaryBinding;
import com.traincompany.kamil.runnnn.viewmodel.summary.SummaryViewModel;
import com.traincompany.kamil.runnnn.viewmodel.summary.SummaryViewModelContract;

public class SummaryActivity extends AppCompatActivity implements SummaryViewModelContract.View {

    private ActivitySummaryBinding mBinding;
    private SummaryViewModelContract.ViewModel mViewModel;
    private SummaryViewModelContract.View mSummaryView = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataBinding();
    }

    @Override
    public void onBackPressed() {
    }

    private void initDataBinding() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_summary);
        mViewModel = new SummaryViewModel(this, mSummaryView);
        mBinding.setViewModel((SummaryViewModel) mViewModel);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewModel.destroy();
    }

    @Override
    public void saveTraining() {

    }

    @Override
    public void exitTraining() {
        finish();
    }
}
