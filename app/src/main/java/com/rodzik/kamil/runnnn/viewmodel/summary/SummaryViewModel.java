package com.rodzik.kamil.runnnn.viewmodel.summary;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableInt;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
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

import java.util.ArrayList;
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
    private LineChart mChart;
    private double mDistance;
    public boolean isFromDatabase;

    private MapManager mMap;

    private Realm mRealm;

    public SummaryViewModel(@NonNull Context context,
                            SummaryViewModelContract.View view,
                            LineChart chart) {
        mContext = context;
        mView = view;
        mChart = chart;
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

        // Generate chart
        generateHeartRateChart();

        int heartRateAverage = 0;
        for (Integer heartRate : heartRateList) {
            heartRateAverage += heartRate;
        }
        heartRateAverage /= heartRateList.size();
        return String.valueOf(heartRateAverage);
    }

    private void generateHeartRateChart() {
        mChart.getAxisLeft().setTextColor(Color.WHITE);
        mChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return StopwatchProvider.formatToReadableTime((long) value);
            }
        });
        mChart.getXAxis().setTextColor(Color.WHITE);
        mChart.getAxisRight().setEnabled(false);
        mChart.getLegend().setTextColor(Color.WHITE);
        Description description = new Description();
        description.setText("");
        mChart.setDescription(description);

        setData((int) SummarySingleton.getInstance().getTimeInMilliseconds() / 1000,
                SummarySingleton.getInstance().getHeartRate());
        mChart.animateXY(2000, 0);
        mChart.invalidate();
    }

    private void setData(int time, List<Integer> heartRateList) {

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        if (heartRateList.size() < time) {
            for (int i = heartRateList.size(); i >= time; i++) {
                heartRateList.add(heartRateList.get(i - 1));
            }
        }

        for (int i = 0; i < time; i++) {
            yVals.add(new Entry(i * 1000, heartRateList.get(i)));
        }

        LineDataSet set1;
        // create a dataset and give it a type
        set1 = new LineDataSet(yVals, "Tętno");

        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set1.setCubicIntensity(0.2f);
        set1.setDrawCircles(false);
        set1.setLineWidth(1.8f);
        set1.setCircleRadius(4f);
        set1.setCircleColor(Color.WHITE);
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setColor(Color.WHITE);
        set1.setFillColor(Color.WHITE);
        set1.setFillAlpha(100);
        set1.setDrawHorizontalHighlightIndicator(true);
        set1.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return -10;
            }
        });

        // create a data object with the datasets
        LineData data = new LineData(set1);
        data.setValueTextSize(9f);
        data.setDrawValues(false);
        mChart.setData(data);
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
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RunObject runObject = realm.createObject(RunObject.class);
                runObject.setDateTime(SummarySingleton.getInstance().getName());
                runObject.setTimeInMilliseconds(SummarySingleton.getInstance().getTimeInMilliseconds());
                if (gpsRelatedFieldsVisibility.get() == View.VISIBLE) {
                    String encodedLatLngList = PolyUtil.encode(SummarySingleton.getInstance().getLatLngList());
                    runObject.setEncodedLatLngList(encodedLatLngList);
                    runObject.setDistance(SummarySingleton.getInstance().getDistance());
                }
                if (heartRateRelatedFieldsVisibility.get() == View.VISIBLE) {
                    RealmList<RealmInt> realmIntList = new RealmList<>();
                    for (Integer heartRate : SummarySingleton.getInstance().getHeartRate()) {
                        RealmInt realmInt = new RealmInt(heartRate);
                        if (realmInt.isManaged()) {
                            realmIntList.add(realmInt);
                        } else {
                            realmIntList.add(mRealm.copyToRealm(realmInt));
                        }
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
