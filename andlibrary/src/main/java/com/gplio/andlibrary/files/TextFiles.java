package com.gplio.andlibrary.files;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by goncalopalaio on 19/07/18.
 */

public class TextFiles {

    @NonNull
    public static String readTextFromSdcard(String pathInSdcard, String defaultText) {
        File file = new File(Environment.getExternalStorageDirectory(), pathInSdcard);

        log("readTextFromSdcard: loading -> " + file);

        try {
            FileReader fileReader = new FileReader(file);
            return readString(fileReader, defaultText);
        } catch (FileNotFoundException e) {
            log("readTextFromSdcard: error " + e);
        }

        return defaultText;
    }

    public static String readStringFromAssets(@NonNull Context context, String filename, String defaultText) {
        try {
            InputStream inputStream = context.getAssets().open(filename);
            return readString(new InputStreamReader(inputStream), defaultText);
        } catch (IOException e) {
            log("readStringFromAssets: error " + e);
        }

        return defaultText;
    }

    private static String readString(InputStreamReader reader, String defaultText) {
        StringBuilder text = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(reader);
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        }
        catch (IOException e) {
            log("readString: error " + e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    log("readString: error closing " + e);
                }
            }
        }
        String res = text.toString();

        if (res.isEmpty()) {
            return defaultText;
        }
        return res;
    }

    private static void log(String msg) {
        Log.d("TextFiles", msg);
    }
}
