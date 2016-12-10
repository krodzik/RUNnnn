package com.rodzik.kamil.runnnn.viewmodel.training;


import android.view.View;
import android.widget.Chronometer;

import com.rodzik.kamil.runnnn.model.StopwatchModel;
import com.rodzik.kamil.runnnn.model.SummaryModel;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;


public class DataViewModel implements DataViewModelContract.ViewModel {

    private StopwatchModel mStopwatchModel;
    private CompositeDisposable mDisposables = new CompositeDisposable();

    @Override
    public void setupChronometer(Chronometer chronometer) {
        mStopwatchModel = new StopwatchModel(chronometer);
        mStopwatchModel.startStopwatch();
    }

    @Override
    public void setObservableOnPauseButton(Observable<View> pauseButtonObservable) {
        mDisposables.add(pauseButtonObservable.subscribeWith(new DisposableObserver<View>() {
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
        mDisposables.add(stopButtonObservable.subscribeWith(new DisposableObserver<View>() {
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

    private void onPauseButtonClick() {
        mStopwatchModel.pauseStopwatch();
    }

    private void onStopButtonClick() {
        mStopwatchModel.stopStopwatch();
        SummaryModel.getInstance().setTime(mStopwatchModel.getTime());
    }

    @Override
    public void destroy() {
        mDisposables.dispose();
    }
}
