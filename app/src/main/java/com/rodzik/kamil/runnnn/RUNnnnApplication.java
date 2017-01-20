package com.rodzik.kamil.runnnn;


import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import io.realm.Realm;

public class RUNnnnApplication extends Application {
    public RUNnnnApplication() {
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
