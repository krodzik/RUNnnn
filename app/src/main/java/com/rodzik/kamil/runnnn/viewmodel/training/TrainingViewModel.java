package com.rodzik.kamil.runnnn.viewmodel.training;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.view.View;

import com.orhanobut.logger.Logger;
import com.rodzik.kamil.runnnn.R;
import com.rodzik.kamil.runnnn.model.SummaryModel;
import com.rodzik.kamil.runnnn.utils.PermissionUtils;
import com.rodzik.kamil.runnnn.view.activities.SummaryActivity;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

public class TrainingViewModel implements TrainingViewModelContract.ViewModel {

    public ObservableField<String> pauseButtonText;

    private Context mContext;
    private boolean mIsPaused;

    private Observable<View> mPauseButtonObservable;
    private Observable<View> mStopButtonObservable;
    private CompositeDisposable mDisposables = new CompositeDisposable();

    public TrainingViewModel(@NonNull Context context) {
        mContext = context;
        pauseButtonText = new ObservableField<>(mContext.getString(R.string.pauseButton));
    }

    private void onStopButtonClick() {
        Intent intent = new Intent(mContext, SummaryActivity.class);
        mContext.startActivity(intent);
        ((Activity)mContext).finish();
    }

    private void onPauseButtonClick() {
        if (mIsPaused) {
            pauseButtonText.set(mContext.getString(R.string.pauseButton));
            mIsPaused = false;
        } else {
            pauseButtonText.set(mContext.getString(R.string.resumeButton));
            mIsPaused = true;
        }
    }

    @Override
    public void setObservableOnPauseButton(Observable<View> pauseButtonObservable) {
        mPauseButtonObservable = pauseButtonObservable;
        mDisposables.add(mPauseButtonObservable.subscribeWith(new DisposableObserver<View>() {
            @Override
            public void onNext(View value) {
                onPauseButtonClick();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        }));
    }

    @Override
    public void setObservableOnStopButton(Observable<View> stopButtonObservable) {
        mStopButtonObservable = stopButtonObservable;
        mDisposables.add(mStopButtonObservable.subscribeWith(new DisposableObserver<View>() {
            @Override
            public void onNext(View value) {
                onStopButtonClick();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        }));
    }

    @Override
    public void destroy() {
        mContext = null;
        mDisposables.dispose();
    }
}
