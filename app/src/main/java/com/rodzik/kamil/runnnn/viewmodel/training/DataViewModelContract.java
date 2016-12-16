package com.rodzik.kamil.runnnn.viewmodel.training;


import android.content.Context;
import android.view.View;
import android.widget.Chronometer;

import io.reactivex.Observable;

public interface DataViewModelContract {

    interface ViewModel {
        void destroy();
        void setContext(Context context);
        void setObservableOnPauseButton(Observable<View> observable);
        void setObservableOnStopButton(Observable<View> observable);
        void setupChronometer(Chronometer chronometer);
        void setupHeartRateMeasurement(boolean enabled);
        void enableGpsRelatedFeature();
    }
}
