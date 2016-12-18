package com.rodzik.kamil.runnnn.viewmodel.training;


import android.content.Context;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.location.Location;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Chronometer;

import com.orhanobut.logger.Logger;
import com.rodzik.kamil.runnnn.model.HeartRateProvider;
import com.rodzik.kamil.runnnn.model.LocationProvider;
import com.rodzik.kamil.runnnn.model.StopwatchModel;
import com.rodzik.kamil.runnnn.model.SummaryModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

public class DataViewModel implements DataViewModelContract.ViewModel, HeartRateProvider.HeartRateCallbacks {
    // Number of last locations from which current pace is calculated.
    private final int PACE_BUFFER_SIZE = 5;

    public final ObservableField<String> distanceField;
    public final ObservableField<String> paceField;
    public final ObservableField<String> getSpeedField;
    public final ObservableField<String> heartRateField;
    public final ObservableInt gpsRelatedFieldsVisibility;
    public final ObservableInt heartRateRelatedFieldsVisibility;

    private Context mContext;
    private StopwatchModel mStopwatchModel;
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
    private boolean mIsHeartRateEnable;
    private HeartRateProvider mHeartRateProvider;
    private List<Integer> mHeartRateList = new ArrayList<>();
    private boolean mIsPaused;

    public DataViewModel() {
        distanceField = new ObservableField<>("0.00");
        paceField = new ObservableField<>("-:--");
        getSpeedField = new ObservableField<>("-:--");
        heartRateField = new ObservableField<>("--");
        gpsRelatedFieldsVisibility = new ObservableInt(View.GONE);
        heartRateRelatedFieldsVisibility = new ObservableInt(View.GONE);
    }

    @Override
    public void enableGpsRelatedFeature() {
        gpsRelatedFieldsVisibility.set(View.VISIBLE);
        subscribeLocation();
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

    private void calculateDistance() {
        mDistance += mDistanceBetweenTwoLocations;
    }

    private void setDistanceField() {
        distanceField.set(String.format(Locale.US, "%.2f", mDistance / 1000));
    }

    private void calculatePace() {
        long lastLocationTime = mLastLocation.getTime();
        long currentLocationTime = mCurrentLocation.getTime();
        double timeBetweenTwoLocationsInMinutes = (currentLocationTime - lastLocationTime) / 1000;

        mBufferForPaceArray[4][0] = timeBetweenTwoLocationsInMinutes;
        mBufferForPaceArray[4][1] = mDistanceBetweenTwoLocations;

        if (mBufferForPaceArray[0][1] == 0) {
            mPace = 0;
        } else {
            // Calculating average pace for "PACE_BUFFER_SIZE" last locations.
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
        paceField.set(paceString);
    }

    private void setSpeed() {
        // Speed from location
        if (mCurrentLocation.hasSpeed()) {
            getSpeedField.set(String.format(Locale.US, "%.2f", mCurrentLocation.getSpeed() * 3.6));
        } else {
            getSpeedField.set("-:--");
        }
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
        mIsPaused = !mIsPaused;
        mStopwatchModel.pauseStopwatch();
        mIsFirstLocation = true;
        for (int i = PACE_BUFFER_SIZE; i > 0; i--) {
            shiftArrayLeft(mBufferForPaceArray);
        }
        if (mIsHeartRateEnable) {
            mHeartRateProvider.enableHeartRateUpdates(!mIsPaused);
        }
    }

    private void onStopButtonClick() {
        mStopwatchModel.stopStopwatch();
        SummaryModel.getInstance().setTime(mStopwatchModel.getTime());
        SummaryModel.getInstance().setDistance(mDistance);
        SummaryModel.getInstance().setTimeInMilliseconds(mStopwatchModel.getTimeInMiliseconds());
        SummaryModel.getInstance().setHeartRate(mHeartRateList);
    }

    @Override
    public void setContext(@NonNull Context context) {
        mContext = context;
    }

    @Override
    public void setupHeartRateMeasurement(boolean enabled) {
        if (!enabled) {
            return;
        }
        mIsHeartRateEnable = true;
        mHeartRateProvider = new HeartRateProvider(mContext, this, true);
        heartRateRelatedFieldsVisibility.set(View.VISIBLE);
    }

    @Override
    public void destroy() {
        mDisposables.dispose();
        if (mIsHeartRateEnable) {
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
        heartRateField.set("--");
    }

    @Override
    public void serviceDiscovered() {
        mHeartRateProvider.enableHeartRateUpdates(true);
    }

    @Override
    public void heartRateUpdate(String data) {
        heartRateField.set(data);
        mHeartRateList.add(Integer.valueOf(data));
    }

    @Override
    public void connectionTimeout() {

    }
}
