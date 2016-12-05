package com.rodzik.kamil.runnnn.viewmodel.training;


import android.view.View;

import io.reactivex.Observable;

public interface TrainingViewModelContract {

    interface ViewModel {
        void setObservableOnPauseButton(Observable<View> observable);
        void setObservableOnStopButton(Observable<View> observable);
        void destroy();
    }
}
