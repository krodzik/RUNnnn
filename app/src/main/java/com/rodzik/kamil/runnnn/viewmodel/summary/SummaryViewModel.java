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
import com.rodzik.kamil.runnnn.model.SummaryModel;
import com.rodzik.kamil.runnnn.view.activities.MapSummaryActivity;

import java.util.Locale;

public class SummaryViewModel implements SummaryViewModelContract.ViewModel,
        GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {

    public ObservableInt noMapAvailableTextVisibility;
    public ObservableInt gpsRelatedFieldsVisibility;

    private Context mContext;
    private SummaryViewModelContract.View mView;

    private MapManager mMap;

    public SummaryViewModel(@NonNull Context context,
                            SummaryViewModelContract.View view) {
        mContext = context;
        mView = view;

        noMapAvailableTextVisibility = new ObservableInt(View.VISIBLE);
        gpsRelatedFieldsVisibility = new ObservableInt(View.GONE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (SummaryModel.getInstance().getPolylineOptions() != null &&
                !SummaryModel.getInstance().getPolylineOptions().getPoints().isEmpty()) {

            mMap = new MapManager(googleMap);
            mMap.configureMapInSummary(this, this);
            mMap.drawRoute(SummaryModel.getInstance().getPolylineOptions());
            mMap.moveCameraToLatLngBounds(mContext,
                    SummaryModel.getInstance().getPolylineOptions());
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
        return SummaryModel.getInstance().getTime();
    }

    public String getDistance() {
        return String.format(Locale.US, "%.2f", SummaryModel.getInstance().getDistance() / 1000);
    }

    public String getAveragePace() {
        double averagePace = (((SummaryModel.getInstance().getTimeInMilliseconds() / 1000) /
                SummaryModel.getInstance().getDistance()) * 16.67);
        return String.format(Locale.US, "%d:%02d", (int) averagePace, (int) (averagePace * 100) % 100);
    }

    public String getAverageSpeed() {
        double averageSpeed = ((SummaryModel.getInstance().getDistance() /
                (SummaryModel.getInstance().getTimeInMilliseconds() / 1000)) * 3.6);
        return String.format(Locale.US, "%.2f", averageSpeed);
    }

    public void onDoneButtonClicked(View view) {
        // Delete training.
        SummaryModel.getInstance().reset();

        ((Activity) mContext).finish();
    }

    @Override
    public void destroy() {
        mContext = null;
    }
}
