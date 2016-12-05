package com.rodzik.kamil.runnnn.viewmodel.summary;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.orhanobut.logger.Logger;
import com.rodzik.kamil.runnnn.model.SummaryModel;
import com.rodzik.kamil.runnnn.utils.PixelConverterUtils;
import com.rodzik.kamil.runnnn.view.activities.MapSummaryActivity;

public class SummaryViewModel implements SummaryViewModelContract.ViewModel {

    public ObservableField<String> time;
    public ObservableInt noMapAvailableTextVisibility;
    private GoogleMap mMap;
    private PolylineOptions mPolylineOptions;

    private Context mContext;
    private SummaryViewModelContract.View mView;

    public SummaryViewModel(@NonNull Context context,
                            SummaryViewModelContract.View view) {
        mContext = context;
        mView = view;

        time = new ObservableField<>("00:00:00");
        noMapAvailableTextVisibility = new ObservableInt(View.VISIBLE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap != null && SummaryModel.getInstance().getPolylineOptions() != null
                && !SummaryModel.getInstance().getPolylineOptions().getPoints().isEmpty()) {
            mMap = googleMap;
            mPolylineOptions = SummaryModel.getInstance().getPolylineOptions();
            noMapAvailableTextVisibility.set(View.GONE);
            // configure
            mMap.getUiSettings().setAllGesturesEnabled(false);
            // Disable on marker click.
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    return true;
                }
            });
            // add route
            mMap.addMarker(new MarkerOptions()
                    .position(mPolylineOptions.getPoints().get(0))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            mMap.addMarker(new MarkerOptions()
                    .position(mPolylineOptions.getPoints().get(mPolylineOptions.getPoints().size() - 1)));
            mMap.addPolyline(mPolylineOptions);
            // Create bounds that include all locations of the map
            LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
            for (LatLng latLng : mPolylineOptions.getPoints()) {
                boundsBuilder.include(latLng);
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(),
                    (int) PixelConverterUtils.convertDpToPixel(20, mContext)));

            // on click
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    Intent intent = new Intent(mContext, MapSummaryActivity.class);
                    mContext.startActivity(intent);
                }
            });
        } else {
            Logger.e("MAP == null");
            mView.hideMapFragment();
        }
    }

    public String getTime() {
        return SummaryModel.getInstance().getTime();
    }

    private void getMapSnapshot() {
        Logger.d("getting polylines");
        PolylineOptions polylineOptions = SummaryModel.getInstance().getPolylineOptions();
        if (polylineOptions != null) {
            Logger.d("Nie rowna sie null");
        }
    }

    public void onDoneButtonClicked(View view) {
        ((Activity) mContext).finish();
    }

    @Override
    public void destroy() {
        mContext = null;
    }
}
