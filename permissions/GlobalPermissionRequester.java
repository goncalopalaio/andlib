package andlib.permissions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by goncalopalaio on 24/05/18.
 */

public class GlobalPermissionRequester {
    private static final String[] ALL_PERMISSIONS = {
            Manifest.permission.CAMERA
    };

    public static final int PERMISSIONS_REQ_CODE = 42122;

    public static void trigger(Activity activity) {
        if (!hasPermissions(activity)) {
            requestPermissions(activity);
        }
    }

    public static void requestPermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity, ALL_PERMISSIONS, PERMISSIONS_REQ_CODE);
    }

    public static boolean hasPermissions(Context application) {
        for (String p : ALL_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(application, p) == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }
}
