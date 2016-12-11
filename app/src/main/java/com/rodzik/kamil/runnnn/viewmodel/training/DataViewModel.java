package com.rodzik.kamil.runnnn.viewmodel.training;


import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.location.Location;
import android.view.View;
import android.widget.Chronometer;

import com.orhanobut.logger.Logger;
import com.rodzik.kamil.runnnn.model.LocationModel;
import com.rodzik.kamil.runnnn.model.StopwatchModel;
import com.rodzik.kamil.runnnn.model.SummaryModel;

import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;


public class DataViewModel implements DataViewModelContract.ViewModel {

    public ObservableField<String> distanceField;
    public ObservableInt gpsRelatedFieldsVisibility;

    private Location firstLocation = null;
    private Location secondLocation = null;
    private float distance = 0;
    private String mDistanceString;
    private StopwatchModel mStopwatchModel;
    private CompositeDisposable mDisposables = new CompositeDisposable();

    public DataViewModel() {
        distanceField = new ObservableField<>("0.00");
        gpsRelatedFieldsVisibility = new ObservableInt(View.GONE);
    }

    @Override
    public void enableGpsRelatedFeature() {
        gpsRelatedFieldsVisibility.set(View.VISIBLE);
        subscribeLocation();
    }

    private void subscribeLocation() {
        mDisposables.add(LocationModel.getLocationObservable().subscribeWith(new DisposableObserver<Location>() {
            @Override
            public void onNext(Location location) {
                Logger.d("Getting location updates in DataViewModel");
                calculateDistance(location);
                setDistanceField();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        }));
    }

    private void calculateDistance(Location location) {
        if (firstLocation == null) {
            firstLocation = location;
        } else {
            secondLocation = location;
        }

        if (firstLocation != null && secondLocation != null) {
            distance += firstLocation.distanceTo(secondLocation);
            firstLocation = null;
            secondLocation = null;
        }
    }

    private void setDistanceField() {
        mDistanceString = String.format(Locale.US, "%.2f", distance / 1000);
        distanceField.set(mDistanceString);
    }

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
        SummaryModel.getInstance().setDistance(mDistanceString);
    }

    @Override
    public void destroy() {
        mDisposables.dispose();
    }
}
