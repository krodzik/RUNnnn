package com.rodzik.kamil.runnnn.viewmodel.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import com.rodzik.kamil.runnnn.utils.PermissionUtils;
import com.rodzik.kamil.runnnn.view.activities.TrainingActivity;

public class MainViewModel implements MainViewModelContract.ViewModel {

    private Context mContext;

    public MainViewModel(@NonNull Context context) {
        mContext = context;
        PermissionUtils.requestLocationPermission((Activity) context);
    }

    public void onStartButtonClicked(View view) {
        Intent intent = new Intent(mContext, TrainingActivity.class);
        intent.putExtra("MAP", PermissionUtils.isLocationAccessPermissionGranted(mContext));
        mContext.startActivity(intent);
    }

    @Override
    public void destroy() {
        mContext = null;
    }
}
