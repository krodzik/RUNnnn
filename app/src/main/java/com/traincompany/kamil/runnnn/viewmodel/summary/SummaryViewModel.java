package com.traincompany.kamil.runnnn.viewmodel.summary;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

public class SummaryViewModel implements SummaryViewModelContract.ViewModel {

    private Context mContext;
    private SummaryViewModelContract.View mSummaryView;

    public SummaryViewModel(@NonNull Context context,
                            @NonNull SummaryViewModelContract.View summaryView) {
        mContext = context;
        mSummaryView = summaryView;
    }

    public void onDoneButtonClicked(View view) {
        mSummaryView.exitTraining();
    }

    @Override
    public void destroy() {
        mContext = null;
    }
}
