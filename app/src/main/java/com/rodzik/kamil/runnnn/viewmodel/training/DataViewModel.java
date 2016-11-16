package com.rodzik.kamil.runnnn.viewmodel.training;


import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableField;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Chronometer;

import com.orhanobut.logger.Logger;
import com.rodzik.kamil.runnnn.R;
import com.rodzik.kamil.runnnn.view.activities.SummaryActivity;

import java.util.Locale;


public class DataViewModel implements DataViewModelContract.ViewModel {

    public ObservableField<String> pauseButtonText;

    private Context mContext;
    private Chronometer mChronometer;
    private boolean mIsPaused;
    private long mLastPauseTime;
    private DataViewModelContract.View mView;

    public DataViewModel(@NonNull Context context,
                         @NonNull DataViewModelContract.View view,
                         @NonNull Chronometer chronometer) {
        mContext = context;
        mView = view;
        mChronometer = chronometer;

        pauseButtonText = new ObservableField<>(mContext.getString(R.string.pauseButton));

        mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer cArg) {
                long time = SystemClock.elapsedRealtime() - cArg.getBase();
                int h = (int) (time / 3600000);
                int m = (int) (time - h * 3600000) / 60000;
                int s = (int) (time - h * 3600000 - m * 60000) / 1000;
                cArg.setText(String.format(Locale.US, "%02d:%02d:%02d", h, m, s));
                //Logger.d("Tick");
            }
        });
        chronometerStart();
    }

    private void chronometerStart() {
        if (mLastPauseTime == 0) {
            mChronometer.setBase(SystemClock.elapsedRealtime());
        } else {
            long intervalOnPause = (SystemClock.elapsedRealtime() - mLastPauseTime);
            mChronometer.setBase( mChronometer.getBase() + intervalOnPause );
        }
        mChronometer.start();
    }

    public void onPauseButtonClicked(View view) {
        mChronometer.stop();
        if (mIsPaused) {
            chronometerStart();
            pauseButtonText.set(mContext.getString(R.string.pauseButton));
            mIsPaused = false;
        } else {
            mLastPauseTime = SystemClock.elapsedRealtime();
            pauseButtonText.set(mContext.getString(R.string.resumeButton));
            mIsPaused = true;
        }
    }

    public void onStopButtonClicked(View view) {
        Intent intent = new Intent(mContext, SummaryActivity.class);
        mContext.startActivity(intent);
        mView.exitTraining();
    }

    @Override
    public void destroy() {
        mContext = null;
    }
}
