package com.gplio.andlibrary.debug;

import com.gplio.andlibrary.R;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by goncalopalaio on 16/09/18.
 */
public class HotValues {
    private static String TAG = "HotValues";

    private static int REQUEST_CODE = 13235;

    @TargetApi(Build.VERSION_CODES.M)
    public static void launchService(Activity currentActivity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.d(TAG, "#FIXME not supported prior to M");
            return;
        }
        if (Settings.canDrawOverlays(currentActivity)) {
            currentActivity.startService(new Intent(currentActivity, FloatingViewService.class));
            currentActivity.finish();
            return;
        }

        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + currentActivity.getPackageName()));
        currentActivity.startActivityForResult(intent, REQUEST_CODE);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void onActivityResult(Activity activity, int requestCode) {
        if (requestCode != REQUEST_CODE) {
            return;
        }

        if (!Settings.canDrawOverlays(activity)) {
            Toast.makeText(activity, "Cannot draw overlays without permission...", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + activity.getPackageName()));
        activity.startActivityForResult(intent, REQUEST_CODE);
    }


}
