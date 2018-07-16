package com.gplio.andlib.graphics;

/**
 * Created by goncalopalaio on 26/05/18.
 */

public class GenericShader extends GShader {
    private static String vertex =
            "attribute vec4 position;" +
            "attribute vec2 uv;" +
            "void main() {" +
            "gl_Position = position;" +
            "}";

    private static String fragment =
            "precision mediump float;" +
                    "void main() {" +
                    "gl_FragColor = vec4(1.0,0.0,0.0,1.0);" +
                    "}";
    public GenericShader() {
        super(vertex, fragment);
    }
}
