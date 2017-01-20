package com.rodzik.kamil.runnnn.model;

import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class SummarySingleton {
    private static SummarySingleton mInstance = null;

    private String mTime;
    private PolylineOptions mPolylineOptions;
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
        mTime = null;
        mPolylineOptions = null;
        mDistance = 0;
        mTimeInMilliseconds = 0;
        mHeartRate.clear();
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        this.mTime = time;
    }

    public PolylineOptions getPolylineOptions() {
        return mPolylineOptions;
    }

    public void setPolylineOptions(PolylineOptions polylineOptions) {
        this.mPolylineOptions = polylineOptions;
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
