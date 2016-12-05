package com.rodzik.kamil.runnnn.viewmodel.training;


import android.content.Context;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import io.reactivex.Observable;

public interface MapViewModelContract {

    interface ViewModel {
        void setObservableOnPauseButton(Observable<View> observable);
        void setObservableOnStopButton(Observable<View> observable);
        void setContext(Context context);
        void onMapReady(GoogleMap map);
        void destroy();
    }
}
