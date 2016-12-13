package com.rodzik.kamil.runnnn.viewmodel.main;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

public class ConnectedDeviceViewModel implements ConnectedDeviceViewModelContract.ViewModel {

    private Context mContext;

    public ConnectedDeviceViewModel(@NonNull Context context) {
        mContext = context;
    }

    public void onAddDeviceButtonClicked(View view) {
    }

    @Override
    public void destroy() {
        mContext = null;
    }
}