package com.qrcode.matcher;


import android.content.Context;
import android.os.Build;
import android.os.Environment;

import java.io.File;

public class Utils {

    public static File getDocumentsDirectory(Context context) {
        File dir;

        // For Android 10 and above, use Scoped Storage
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            if (dir != null && !dir.exists()) {
                dir.mkdirs();
            }
            return dir;
        }
        // For Android 9 and below, use direct access to external storage
        dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

}
