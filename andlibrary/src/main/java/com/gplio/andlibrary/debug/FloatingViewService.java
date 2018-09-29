package com.gplio.andlibrary.debug;

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

public class FloatingViewService extends Service {
    private static int REQUEST_CODE = 13235;
    private static String TAG = "WarmValue";
    private LinearLayout layout;
    /***
     * TODO Handle touch event pass through correctly
     * TODO Make the views movable
     *
     *
     *
     *
     *
     *
     *
     */


    public FloatingViewService() {
    }

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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();

        // Add overlay view

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
            Toast.makeText(this, "#FIXME Versions prior to Oreo not supported", Toast.LENGTH_LONG)
                    .show();
            return;
        }

        final WindowManager.LayoutParams params =
                new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                        0,
                        PixelFormat.TRANSLUCENT);


        params.gravity = Gravity.CENTER | Gravity.START;
        params.x = 0;
        params.y = 0;

        layout = buildLayout(this);
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) {
            return;
        }
        windowManager.addView(layout, params);

    }

    private LinearLayout buildLayout(final Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));


        final String text1 = "This is a value";

        TextView textView = new TextView(context);
        textView.setText(text1);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, text1, Toast.LENGTH_SHORT).show();
            }
        });
        layout.addView(textView);

        final String text2 = "This is a another value";
        textView = new TextView(context);
        textView.setText(text2);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, text2, Toast.LENGTH_SHORT).show();
            }
        });
        layout.addView(textView);

        return layout;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (layout != null) {
            WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            windowManager.removeView(layout);
            layout = null;
        }
    }
}