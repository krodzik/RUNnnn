package com.rodzik.kamil.runnnn.view.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.rodzik.kamil.runnnn.R;
import com.rodzik.kamil.runnnn.databinding.ActivityTrainingBinding;
import com.rodzik.kamil.runnnn.view.adapters.ViewPagerAdapter;
import com.rodzik.kamil.runnnn.view.fragments.DataFragment;
import com.rodzik.kamil.runnnn.view.fragments.MapFragment;
import com.rodzik.kamil.runnnn.viewmodel.training.TrainingViewModel;
import com.rodzik.kamil.runnnn.viewmodel.training.TrainingViewModelContract;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class TrainingActivity extends AppCompatActivity {

    private ActivityTrainingBinding mBinding;
    private TrainingViewModelContract.ViewModel mViewModel;

    private boolean isMapEnable;

    private DataFragment mDataFragment;
    private MapFragment mMapFragment;

    private Observable<View> mOnPauseClickObservable;
    private Observable<View> mOnStopClickObservable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataBinding();
        createOnClickObservable();
        setupObservableInViewModel();
        isMapEnable = getIntent().getExtras().getBoolean("MAP");
        setupTabFragment();
        setupViewPager(mBinding.pager);
        mBinding.tabLayout.setupWithViewPager(mBinding.pager);
    }

    private void initDataBinding() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_training);
        mViewModel = new TrainingViewModel(this);
        mBinding.setViewModel((TrainingViewModel) mViewModel);
    }

    private void createOnClickObservable() {
        mOnPauseClickObservable = Observable.create(new ObservableOnSubscribe<View>() {
            @Override
            public void subscribe(ObservableEmitter<View> e) throws Exception {
                mBinding.pauseButton.setOnClickListener(view -> {
                    if (e.isDisposed())
                        return;
                    e.onNext(view);
                });
            }
        }).share();

        mOnStopClickObservable = Observable.create(new ObservableOnSubscribe<View>() {
            @Override
            public void subscribe(ObservableEmitter<View> e) throws Exception {
                mBinding.stopButton.setOnClickListener(view -> {
                    if (e.isDisposed())
                        return;
                    e.onNext(view);
                });
            }
        }).share();
    }

    private void setupObservableInViewModel() {
        mViewModel.setObservableOnPauseButton(mOnPauseClickObservable);
        mViewModel.setObservableOnStopButton(mOnStopClickObservable);
    }

    private void setupTabFragment() {
        Bundle args = new Bundle();
        args.putBoolean("MAP", getIntent().getExtras().getBoolean("MAP"));
        args.putBoolean("HEART_RATE", getIntent().getExtras().getBoolean("HEART_RATE"));

        mDataFragment = new DataFragment();
        mDataFragment.setArguments(args);
        mDataFragment.setObservableOnPauseButton(mOnPauseClickObservable);
        mDataFragment.setObservableOnStopButton(mOnStopClickObservable);

        if (isMapEnable) {
            mMapFragment = new MapFragment();
            mMapFragment.setObservableOnPauseButton(mOnPauseClickObservable);
            mMapFragment.setObservableOnStopButton(mOnStopClickObservable);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        // TODO Create a Collection of fragments and iterate through it. Although for now it's fine.
        adapter.addFragment(mDataFragment, this.getString(R.string.dataTab));
        if (isMapEnable) {
            adapter.addFragment(mMapFragment, this.getString(R.string.mapTab));
        }
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewModel.destroy();
    }

    @Override
    public void onBackPressed() {
        // To disable back button during training.
    }
}
