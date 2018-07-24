package com.gplio.andlib.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by goncalopalaio on 23/07/18.
 */

public class DebugText {
    private final int charStart = 32;
    private final int charEnd = 126;
    private final int totalChars = 126 - 32;

    /**
     * Parses easy_font_raw.png from stb_easy_font
     * ascii 32 -> 126
     *       space -> ~
     */
    public static void parseEasyFont(Context context) {
        String path = Environment.getExternalStorageDirectory() + "/debug/easy_font_raw.png";
        Bitmap bitmap = BitmapFactory.decodeFile(path);

        if (bitmap == null) {
            log("Could not decode file " + path);
            return;
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        log("width: " + width + " height: " + height);

        //String s = "o";
        int previousStart = 0;
        List<Offsets> offsets = new LinkedList<>();
        for (int i = 1; i < width; i++) {
            int pixel = bitmap.getPixel(i,0);

            int red = Color.red(pixel);
            if (red == 0) {
                offsets.add(new Offsets(previousStart, i-1));
                previousStart = i;
            }

            /*if (red == 0) {
                s +="o";
            } else {
                s+="#";
            }*/
        }

        //log("s: " + s);

        for (Offsets offset : offsets) {
            log("offset: " + offset.start + " -> " + offset.end);
        }
    }

    private static void log(String m) {
        Log.d("DebugText", m);
    }

    private static class Offsets {
        int start = 0;
        int end = 0;

        public Offsets(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }
}
