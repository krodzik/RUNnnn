package com.rodzik.kamil.runnnn.model;


import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.rodzik.kamil.runnnn.BR;

public class TrainingDataModel extends BaseObservable {
    @Bindable
    private String mDistance;
    @Bindable
    private String mPace;
    @Bindable
    private String mSpeed;
    @Bindable
    private String mHeartRate;
    @Bindable
    private boolean mGpsVisibile;
    @Bindable
    private boolean mHeartRateVisibile;

    public TrainingDataModel(boolean gpsVisible, boolean heartRateVisible) {
        mGpsVisibile = gpsVisible;
        if (gpsVisible) {
            mDistance = "0.00";
            mPace = "-:--";
            mSpeed = "-:--";
        }

        mHeartRateVisibile = heartRateVisible;
        if (heartRateVisible) {
            mHeartRate = "--";
        }
    }

    public String getDistance() {
        return mDistance;
    }

    public void setDistance(String distance) {
        this.mDistance = distance;
        notifyPropertyChanged(BR.distance);
    }

    public String getPace() {
        return mPace;
    }

    public void setPace(String pace) {
        this.mPace = pace;
        notifyPropertyChanged(BR.pace);
    }

    public String getSpeed() {
        return mSpeed;
    }

    public void setSpeed(String speed) {
        this.mSpeed = speed;
        notifyPropertyChanged(BR.speed);
    }

    public String getHeartRate() {
        return mHeartRate;
    }

    public void setHeartRate(String heartRate) {
        this.mHeartRate = heartRate;
        notifyPropertyChanged(BR.heartRate);
    }
}
