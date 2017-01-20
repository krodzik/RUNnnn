package com.rodzik.kamil.runnnn.viewmodel.summary;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.rodzik.kamil.runnnn.MapManager;
import com.rodzik.kamil.runnnn.model.SummarySingleton;
import com.rodzik.kamil.runnnn.view.activities.MapSummaryActivity;

import java.util.List;
import java.util.Locale;

public class SummaryViewModel implements SummaryViewModelContract.ViewModel,
        GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {

    public ObservableInt noMapAvailableTextVisibility;
    public ObservableInt gpsRelatedFieldsVisibility;
    public ObservableInt heartRateRelatedFieldsVisibility;

    private Context mContext;
    private SummaryViewModelContract.View mView;
    private double mDistance;

    private MapManager mMap;

    public SummaryViewModel(@NonNull Context context,
                            SummaryViewModelContract.View view) {
        mContext = context;
        mView = view;
        mDistance = SummarySingleton.getInstance().getDistance();

        noMapAvailableTextVisibility = new ObservableInt(View.VISIBLE);
        gpsRelatedFieldsVisibility = new ObservableInt(View.GONE);
        heartRateRelatedFieldsVisibility = new ObservableInt(View.GONE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (SummarySingleton.getInstance().getPolylineOptions() != null &&
                !SummarySingleton.getInstance().getPolylineOptions().getPoints().isEmpty()) {

            mMap = new MapManager(googleMap);
            mMap.configureMapInSummary(this, this);
            mMap.drawRoute(SummarySingleton.getInstance().getPolylineOptions());
            mMap.moveCameraToLatLngBounds(mContext,
                    SummarySingleton.getInstance().getPolylineOptions());
            noMapAvailableTextVisibility.set(View.GONE);
            gpsRelatedFieldsVisibility.set(View.VISIBLE);
        } else {
            mView.hideMapFragment();
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        startMapSummaryActivity();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        startMapSummaryActivity();
        return true;
    }

    private void startMapSummaryActivity() {
        Intent intent = new Intent(mContext, MapSummaryActivity.class);
        mContext.startActivity(intent);
    }

    public String getTime() {
        return SummarySingleton.getInstance().getTime();
    }

    public String getDistance() {
        return String.format(Locale.US, "%.2f", mDistance / 1000);
    }

    public String getAveragePace() {
        if (mDistance == 0) {
            return "-:--";
        }
        double averagePace = (((SummarySingleton.getInstance().getTimeInMilliseconds() / 1000) /
                mDistance) * 16.67);
        return String.format(Locale.US, "%d:%02d", (int) averagePace, (int) (averagePace * 100) % 100);
    }

    public String getAverageSpeed() {
        if (mDistance == 0) {
            return "-.--";
        }
        double averageSpeed = ((mDistance /
                (SummarySingleton.getInstance().getTimeInMilliseconds() / 1000)) * 3.6);
        return String.format(Locale.US, "%.2f", averageSpeed);
    }

    public String getAverageHeartRate() {
        List<Integer> heartRateList = SummarySingleton.getInstance().getHeartRate();
        if (heartRateList.isEmpty()) {
            return "--";
        }
        heartRateRelatedFieldsVisibility.set(View.VISIBLE);
        int heartRateAverage = 0;
        for (Integer heartRate : heartRateList) {
            heartRateAverage += heartRate;
        }
        heartRateAverage /= heartRateList.size();
        return String.valueOf(heartRateAverage);
    }

    public void onDoneButtonClicked(View view) {
        // Delete training.
        SummarySingleton.getInstance().reset();

        ((Activity) mContext).finish();
    }

    @Override
    public void destroy() {
        mContext = null;
    }
}
