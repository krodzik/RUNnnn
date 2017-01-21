package com.rodzik.kamil.runnnn.viewmodel.training;


import android.content.Context;
import android.view.View;
import android.widget.Chronometer;

import com.rodzik.kamil.runnnn.model.TrainingDataModel;

import io.reactivex.Observable;

public interface DataViewModelContract {

    interface ViewModel {
        void destroy();

        void setModel(TrainingDataModel model, Context context);

        void setupChronometer(Chronometer chronometer);

        void setObservableOnPauseButton(Observable<View> observable);

        void setObservableOnStopButton(Observable<View> observable);
    }
}
