package com.traincompany.kamil.runnnn.viewmodel.main;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import com.orhanobut.logger.Logger;
import com.traincompany.kamil.runnnn.view.activities.TrainingActivity;

public class MainViewModel implements MainViewModelContract.ViewModel {

    private Context mContext;

    public MainViewModel(@NonNull Context context) {
        mContext = context;
    }

    public void onStartButtonClicked(View view) {
        Intent intent = new Intent(mContext, TrainingActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    public void destroy() {
        mContext = null;
    }
}
