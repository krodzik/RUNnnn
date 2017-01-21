package com.rodzik.kamil.runnnn.viewmodel.summary;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableInt;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;
import com.rodzik.kamil.runnnn.MapManager;
import com.rodzik.kamil.runnnn.data.StopwatchProvider;
import com.rodzik.kamil.runnnn.database.RealmInt;
import com.rodzik.kamil.runnnn.database.RunObject;
import com.rodzik.kamil.runnnn.model.SummarySingleton;
import com.rodzik.kamil.runnnn.utils.PolyUtil;
import com.rodzik.kamil.runnnn.view.activities.MapSummaryActivity;

import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class SummaryViewModel implements SummaryViewModelContract.ViewModel,
        GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {

    public ObservableInt noMapAvailableTextVisibility;
    public ObservableInt gpsRelatedFieldsVisibility;
    public ObservableInt heartRateRelatedFieldsVisibility;

    private Context mContext;
    private SummaryViewModelContract.View mView;
    private double mDistance;
    public boolean isFromDatabase;

    private MapManager mMap;

    private Realm mRealm;

    public SummaryViewModel(@NonNull Context context,
                            SummaryViewModelContract.View view) {
        mContext = context;
        mView = view;
        mDistance = SummarySingleton.getInstance().getDistance();
        isFromDatabase = SummarySingleton.getInstance().isFromDatabase();

        noMapAvailableTextVisibility = new ObservableInt(View.VISIBLE);
        gpsRelatedFieldsVisibility = new ObservableInt(View.GONE);
        heartRateRelatedFieldsVisibility = new ObservableInt(View.GONE);

        mRealm = Realm.getDefaultInstance();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (SummarySingleton.getInstance().getLatLngList() != null &&
                !SummarySingleton.getInstance().getLatLngList().isEmpty()) {

            mMap = new MapManager(googleMap);
            mMap.configureMapInSummary(this, this);
            mMap.drawRoute(new PolylineOptions().addAll(SummarySingleton.getInstance().getLatLngList()).color(Color.BLUE).width(10));
            mMap.moveCameraToLatLngBounds(mContext,
                    new PolylineOptions().addAll(SummarySingleton.getInstance().getLatLngList()));
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
        return StopwatchProvider.formatToReadableTime(SummarySingleton.getInstance().getTimeInMilliseconds());
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

    public void onRejectButtonClicked(View view) {
        // Delete training.
        if (isFromDatabase) {
            // obtain the results of a query
            final RealmResults<RunObject> results = mRealm.where(RunObject.class).equalTo("dateTime",
                    SummarySingleton.getInstance().getName()).findAll();

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    // Delete all matches
                    results.deleteAllFromRealm();
                }
            });
        }
        SummarySingleton.getInstance().reset();
        ((Activity) mContext).finish();
    }

    public void onSaveButtonClicked(View view) {
        // Saving training to database
        // All writes must be wrapped in a transaction to facilitate safe multi threading
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // Add a person
                RunObject runObject = realm.createObject(RunObject.class);
                runObject.setDateTime(SummarySingleton.getInstance().getName());
                runObject.setTimeInMilliseconds(SummarySingleton.getInstance().getTimeInMilliseconds());
                if (gpsRelatedFieldsVisibility.get() == View.VISIBLE) {
                    String encodedLatLngList = PolyUtil.encode(SummarySingleton.getInstance().getLatLngList());
                    runObject.setDistance(SummarySingleton.getInstance().getDistance());
                    runObject.setEncodedLatLngList(encodedLatLngList);
                }
                if (heartRateRelatedFieldsVisibility.get() == View.VISIBLE) {
                    RealmList<RealmInt> realmIntList = new RealmList();
                    for (Integer heartRate : SummarySingleton.getInstance().getHeartRate()) {
                        realmIntList.add(new RealmInt(heartRate));
                    }
                    runObject.setHeartRateList(realmIntList);
                }
            }
        });

        ((Activity) mContext).finish();
    }

    @Override
    public void destroy() {
        mContext = null;
        mRealm.close();
    }
}
