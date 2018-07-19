package com.gplio.andlib.files;

import android.support.annotation.NonNull;
import android.util.Log;

import com.gplio.fibrewallpaper.BuildConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by goncalopalaio on 19/07/18.
 */

public class TextFiles {

    @NonNull
    public static String readText(String fullPath) {
        File file = new File(fullPath);

        StringBuilder text = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        }
        catch (IOException e) {
            if (BuildConfig.DEBUG) log("Error reading " + fullPath + " " + e.getLocalizedMessage());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    if (BuildConfig.DEBUG) log("Error closing reader" + fullPath + " " + e.getLocalizedMessage());
                }
            }
        }

        return text.toString();
    }

    private static void log(String msg) {
        Log.d("TextFiles", msg);
    }
}
