package com.rodzik.kamil.runnnn.viewmodel.main;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.rodzik.kamil.runnnn.view.activities.TrainingActivity;

public class MainViewModel implements MainViewModelContract.ViewModel {

    private Context mContext;

    public MainViewModel(@NonNull Context context) {
        mContext = context;

        // Ask for permission ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) mContext,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
            //MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
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
