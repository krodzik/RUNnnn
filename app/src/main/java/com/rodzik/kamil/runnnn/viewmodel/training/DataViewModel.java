package com.rodzik.kamil.runnnn.viewmodel.training;


import android.content.Context;
import android.location.Location;
import android.view.View;
import android.widget.Chronometer;

import com.rodzik.kamil.runnnn.data.HeartRateProvider;
import com.rodzik.kamil.runnnn.data.LocationProvider;
import com.rodzik.kamil.runnnn.data.StopwatchProvider;
import com.rodzik.kamil.runnnn.model.SummarySingleton;
import com.rodzik.kamil.runnnn.model.TrainingDataModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

public class DataViewModel implements DataViewModelContract.ViewModel, HeartRateProvider.HeartRateCallbacks {
    // Number of last locations from which current pace is calculated.
    private final int PACE_BUFFER_SIZE = 5;

    private TrainingDataModel mModel;

    private StopwatchProvider mStopwatchProvider;
    private CompositeDisposable mDisposables = new CompositeDisposable();
    private Location mLastLocation = null;
    private Location mCurrentLocation = null;
    private double mDistanceBetweenTwoLocations;
    private boolean mIsFirstLocation = true;
    private double mDistance = 0;
    private double mPace = 0;
    // [0] - Time between two last locations.
    // [1] - Distance between two last locations.
    private double[][] mBufferForPaceArray = new double[PACE_BUFFER_SIZE][2];
    private HeartRateProvider mHeartRateProvider;
    private List<Integer> mHeartRateList = new ArrayList<>();
    private boolean mIsPaused;

    public DataViewModel() {
        SummarySingleton.getInstance().reset();
    }

    @Override
    public void setModel(TrainingDataModel model, Context context) {
        mModel = model;
        if (mModel.isGpsVisible()) {
            subscribeLocation();
        }
        if (mModel.isHeartRateVisible()) {
            mHeartRateProvider = new HeartRateProvider(context, this, true);
        }
    }

    private void subscribeLocation() {
        mDisposables.add(LocationProvider.getLocationObservable().subscribeWith(new DisposableObserver<Location>() {
            @Override
            public void onNext(Location location) {
                updateLocation(location);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        }));
    }

    private void updateLocation(Location location) {
        mCurrentLocation = location;

        if (mIsFirstLocation) {
            mLastLocation = location;
            mIsFirstLocation = false;
        }
        mDistanceBetweenTwoLocations = mLastLocation.distanceTo(mCurrentLocation);

        calculatePace();
        setPaceField();

        if (mCurrentLocation.getAccuracy() < mDistanceBetweenTwoLocations) {
            calculateDistance();
            setDistanceField();
            setSpeed();
            mLastLocation = mCurrentLocation;
        }
    }

    private void calculatePace() {
        long lastLocationTime = mLastLocation.getTime();
        long currentLocationTime = mCurrentLocation.getTime();
        double timeBetweenTwoLocationsInMinutes = (currentLocationTime - lastLocationTime) / 1000;

        mBufferForPaceArray[PACE_BUFFER_SIZE - 1][0] = timeBetweenTwoLocationsInMinutes;
        mBufferForPaceArray[PACE_BUFFER_SIZE - 1][1] = mDistanceBetweenTwoLocations;

        if (mBufferForPaceArray[0][1] == 0) {
            mPace = 0;
        } else {
            // Calculating average pace for PACE_BUFFER_SIZE last locations.
            double sumTimeBetweenLocations = 0;
            double sumDistanceBetweenLocations = 0;
            for (double[] location : mBufferForPaceArray) {
                sumTimeBetweenLocations += location[0];
                sumDistanceBetweenLocations += location[1];
            }
            mPace = (sumTimeBetweenLocations / sumDistanceBetweenLocations) * 16.67;
        }

        shiftArrayLeft(mBufferForPaceArray);
    }

    private void shiftArrayLeft(double[][] array) {
        for (int i = 1; i < PACE_BUFFER_SIZE; i++) {
            array[i - 1][0] = array[i][0];
            array[i - 1][1] = array[i][1];
        }
        array[PACE_BUFFER_SIZE - 1][0] = 0;
        array[PACE_BUFFER_SIZE - 1][1] = 0;
    }

    private void setPaceField() {
        String paceString;
        if (mPace != 0) {
            paceString = String.format(Locale.US, "%d:%02d", (int) mPace, (int) (mPace * 100) % 100);
        } else {
            paceString = "-:--";
        }
        mModel.setPace(paceString);
    }

    private void calculateDistance() {
        mDistance += mDistanceBetweenTwoLocations;
    }

    private void setDistanceField() {
        mModel.setDistance(String.format(Locale.US, "%.2f", mDistance / 1000));
    }

    private void setSpeed() {
        // Speed from location
        if (mCurrentLocation.hasSpeed()) {
            mModel.setSpeed(String.format(Locale.US, "%.2f", mCurrentLocation.getSpeed() * 3.6));
        } else {
            mModel.setSpeed("-:--");
        }
    }

    @Override
    public void setupChronometer(Chronometer chronometer) {
        mStopwatchProvider = new StopwatchProvider(chronometer);
        mStopwatchProvider.startStopwatch();
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
        mIsPaused = !mIsPaused;
        mStopwatchProvider.pauseStopwatch();
        mIsFirstLocation = true;
        for (int i = PACE_BUFFER_SIZE; i > 0; i--) {
            shiftArrayLeft(mBufferForPaceArray);
        }
        if (mModel.isHeartRateVisible()) {
            mHeartRateProvider.enableHeartRateUpdates(!mIsPaused);
        }
    }

    private void onStopButtonClick() {
        mStopwatchProvider.stopStopwatch();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy  HH:mm", Locale.US);
        Date date = new Date();
        SummarySingleton.getInstance().setName(dateFormat.format(date));
        SummarySingleton.getInstance().setDistance(mDistance);
        SummarySingleton.getInstance().setTimeInMilliseconds(mStopwatchProvider.getTimeInMilliseconds());
        SummarySingleton.getInstance().setHeartRate(mHeartRateList);
    }

    @Override
    public void destroy() {
        mDisposables.dispose();
        if (mModel.isHeartRateVisible()) {
            mHeartRateProvider.enableHeartRateUpdates(false);
            mHeartRateProvider.destroy();
        }
    }

    @Override
    public void connected() {
        mHeartRateProvider.discoverServices();
    }

    @Override
    public void disconnected() {
        mModel.setHeartRate("--");
    }

    @Override
    public void serviceDiscovered() {
        mHeartRateProvider.enableHeartRateUpdates(true);
    }

    @Override
    public void heartRateUpdate(String data) {
        mModel.setHeartRate(data);
        mHeartRateList.add(Integer.valueOf(data));
    }

    @Override
    public void connectionTimeout() {

    }
}
