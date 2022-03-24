package com.dazone.crewchatoff.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.view.Surface;
import android.view.WindowManager;

/**
 * Created by BM Anderson on 22/03/2022.
 */
public class OrientationUtils {
    private OrientationUtils() {
    }

    /** Locks the device window in landscape mode. */
    public static void lockOrientationLandscape(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
    }

    /** Locks the device window in portrait mode. */
    public static void lockOrientationPortrait(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /** Locks the device window in actual screen mode. */
    public static void lockOrientation(Activity activity) {
        final int orientation = activity.getResources().getConfiguration().orientation;
        final int rotation = ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getRotation();

        // Copied from Android docs, since we don't have these values in Froyo
        // 2.2
        int SCREEN_ORIENTATION_REVERSE_LANDSCAPE = 8;
        int SCREEN_ORIENTATION_REVERSE_PORTRAIT = 9;

        // Build.VERSION.SDK_INT <= Build.VERSION_CODES.FROYO
        if (!(Build.VERSION.SDK_INT <= Build.VERSION_CODES.FROYO)) {
            SCREEN_ORIENTATION_REVERSE_LANDSCAPE = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            SCREEN_ORIENTATION_REVERSE_PORTRAIT = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }

        if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90) {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        } else if (rotation == Surface.ROTATION_180 || rotation == Surface.ROTATION_270) {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                activity.setRequestedOrientation(SCREEN_ORIENTATION_REVERSE_PORTRAIT);
            } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                activity.setRequestedOrientation(SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            }
        }
    }

    /** Unlocks the device window in user defined screen mode. */
    public static void unlockOrientation(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
    }

}