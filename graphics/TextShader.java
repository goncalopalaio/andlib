package com.gplio.andlib.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

/**
 * Created by goncalopalaio on 24/07/18.
 */

public class TextShader extends GShader {
    private static String vertex =
            "attribute vec4 position;" +
                    "attribute vec2 uv;" +
                    "varying vec2 vuv;" +
                    "void main() {" +
                    "vuv = uv;" +
                    "gl_Position = position;" +
                    "}";

    private static String fragment =
            "precision mediump float;" +
                    "varying vec2 vuv;" +
                    "uniform sampler2D utexture0;" +
                    "void main() {" +
                    "gl_FragColor = texture2D(utexture0, vuv) * vec4(0.0,1.0,0.0,1.0);" +
                    //"gl_FragColor = vec4(vuv.x,0.0,0.0,1.0);" +
                    "}";
    public TextShader() {
        super(vertex, fragment);
    }

    @Override
    public boolean init(Context context) {
        String path = Environment.getExternalStorageDirectory() + "/debug/easy_font_raw.png";
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        return super.init(context, bitmap);
    }
}
