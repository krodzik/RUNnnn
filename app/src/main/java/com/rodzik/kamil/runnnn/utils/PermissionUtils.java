package com.rodzik.kamil.runnnn.utils;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.orhanobut.logger.Logger;

public class PermissionUtils {
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 0;

    public static boolean isLocationAccessPermissionGranted(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestLocationPermission(Activity activity) {
        if (!isLocationAccessPermissionGranted(activity)) {
            Logger.d("No permission - ask for it");

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.

                // MY_PERMISSIONS_REQUEST_ACCESS_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);
        }
    }
}
