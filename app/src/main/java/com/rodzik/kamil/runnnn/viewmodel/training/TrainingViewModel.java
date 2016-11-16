package com.rodzik.kamil.runnnn.viewmodel.training;


import android.content.Context;

public class TrainingViewModel implements TrainingViewModelContract.ViewModel {

    private Context mContext;

    public TrainingViewModel(Context context) {
        this.mContext = context;
    }

    @Override
    public void destroy() {
        mContext = null;
    }
}
