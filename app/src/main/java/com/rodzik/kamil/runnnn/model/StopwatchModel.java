package com.rodzik.kamil.runnnn.model;


import android.os.SystemClock;
import android.widget.Chronometer;

import java.util.Locale;

public class StopwatchModel {

    private Chronometer mChronometer;
    private boolean mIsPaused;
    private long mLastPauseTime;
    private long mSystemClockStopTime;

    public StopwatchModel(Chronometer chronometer) {
        mChronometer = chronometer;
        setupChronometer();
    }

    private void setupChronometer() {
        mChronometer.setOnChronometerTickListener(cArg -> {
            long time = SystemClock.elapsedRealtime() - cArg.getBase();
            cArg.setText(formatToReadableTime(time));
        });
    }

    private String formatToReadableTime(long time) {
        int h = (int) (time / 3600000);
        int m = (int) (time - h * 3600000) / 60000;
        int s = (int) (time - h * 3600000 - m * 60000) / 1000;
        return String.format(Locale.US, "%02d:%02d:%02d", h, m, s);
    }

    public void startStopwatch() {
        if (mLastPauseTime == 0) {
            mChronometer.setBase(SystemClock.elapsedRealtime());
        } else {
            long intervalOnPause = (SystemClock.elapsedRealtime() - mLastPauseTime);
            mChronometer.setBase(mChronometer.getBase() + intervalOnPause);
        }
        mChronometer.start();
    }

    public void pauseStopwatch() {
        mChronometer.stop();
        if (mIsPaused) {
            startStopwatch();
            mIsPaused = false;
        } else {
            mLastPauseTime = SystemClock.elapsedRealtime();
            mIsPaused = true;
        }
    }

    public void stopStopwatch() {
        mChronometer.stop();
        if (mIsPaused) {
            mSystemClockStopTime = mLastPauseTime;
            return;
        }
        mSystemClockStopTime = SystemClock.elapsedRealtime();
    }

    public String getTime() {
        long time = mSystemClockStopTime - mChronometer.getBase();
        return formatToReadableTime(time);
    }
}
