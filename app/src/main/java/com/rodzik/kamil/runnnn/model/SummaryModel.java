package com.rodzik.kamil.runnnn.model;

import com.google.android.gms.maps.model.PolylineOptions;

public class SummaryModel {
    private static SummaryModel mInstance = null;

    private String mTime;
    private PolylineOptions mPolylineOptions;

    private SummaryModel() {
    }

    public static SummaryModel getInstance() {
        if (mInstance == null) {
            mInstance = new SummaryModel();
        }
        return mInstance;
    }

    public void reset() {
        mTime = null;
        mPolylineOptions = null;
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

    public void setPolylineOptions(PolylineOptions mPolylineOptions) {
        this.mPolylineOptions = mPolylineOptions;
    }
}
