package com.rodzik.kamil.runnnn.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class SummarySingleton {
    private static SummarySingleton mInstance = null;

    private String mName;
    private List<LatLng> mLatLngList;
    private double mDistance;   // in meters
    private long mTimeInMilliseconds;
    private List<Integer> mHeartRate = new ArrayList<>();

    private SummarySingleton() {
    }

    public static SummarySingleton getInstance() {
        if (mInstance == null) {
            mInstance = new SummarySingleton();
        }
        return mInstance;
    }

    public void reset() {
        mName = null;
        mLatLngList = null;
        mDistance = 0;
        mTimeInMilliseconds = 0;
        mHeartRate.clear();
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public List<LatLng> getLatLngList() {
        return mLatLngList;
    }

    public void setLatLngList(List<LatLng> mLatLngList) {
        this.mLatLngList = mLatLngList;
    }

    public double getDistance() {
        return mDistance;
    }

    public void setDistance(double distance) {
        this.mDistance = distance;
    }

    public long getTimeInMilliseconds() {
        return mTimeInMilliseconds;
    }

    public void setTimeInMilliseconds(long timeInMilliseconds) {
        this.mTimeInMilliseconds = timeInMilliseconds;
    }

    public List<Integer> getHeartRate() {
        return mHeartRate;
    }

    public void setHeartRate(List<Integer> heartRate) {
        this.mHeartRate.addAll(heartRate);
    }
}
