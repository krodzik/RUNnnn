package com.traincompany.kamil.runnnn.viewmodel.training;


import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.orhanobut.logger.Logger;
import com.traincompany.kamil.runnnn.view.activities.SummaryActivity;

public class DataViewModel implements DataViewModelContract.ViewModel {

    private Context mContext;

    public DataViewModel(Context context) {
        mContext = context;
    }

    public void onPauseButtonClicked(View view) {

    }

    public void onStopButtonClicked(View view) {
        Intent intent = new Intent(mContext, SummaryActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    public void destroy() {
        mContext = null;
    }
}
