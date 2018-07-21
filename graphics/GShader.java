package com.gplio.andlib.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.io.IOException;
import java.util.List;

/**
 * Created by goncalopalaio on 24/05/18.
 */

public class GShader {

    protected String vertexShaderCode;
    protected String fragmentShaderCode;
    private String assetTextureName = null;
    private int textureOES = -1;
    private int program;
    private int positionLoc;
    private int uvLoc;
    private int utextureLoc;
    private int[] textures = new int[1];
    private int uTimeLoc;
    private int uWidthLoc;
    private int uHeightLoc;
    private int uViewProjectionLoc;

    public GShader (String vertexShaderCode, String fragmentShaderCode) {
        this.vertexShaderCode = vertexShaderCode;
        this.fragmentShaderCode = fragmentShaderCode;
    }

    public GShader (String vertexShaderCode, String fragmentShaderCode, int textureOES) {
        this.vertexShaderCode = vertexShaderCode;
        this.fragmentShaderCode = fragmentShaderCode;
        this.textureOES = textureOES;
    }

    public boolean init(Context context) {
        boolean error = compile();
        if (error) {
            return true;
        }

        GLES20.glUseProgram(program);

        // attrib locations
        positionLoc = GLES20.glGetAttribLocation(program, "position");

        if (positionLoc < 0) {
            Log.e("init", "init: attribute positionLoc not found");
        }

        uvLoc = GLES20.glGetAttribLocation(program, "uv");

        if (uvLoc < 0) {
            Log.e("init", "init: attribute uvLoc not found");
        }


        // uniform locations
        GLES20.glGetUniformLocation(program, "color");
        GLES20.glGetUniformLocation(program, "mvp");
        uTimeLoc = GLES20.glGetUniformLocation(program, "time");
        utextureLoc = GLES20.glGetUniformLocation(program, "utexture0");

        uWidthLoc = GLES20.glGetUniformLocation(program, "width");
        uHeightLoc = GLES20.glGetUniformLocation(program, "height");
        uViewProjectionLoc = GLES20.glGetUniformLocation(program, "vp");

        ShaderUtil.checkGLError("init", "end of init");


        /**********************/
        // Extract this later

        // read texture.
        assetTextureName = "texture0.png";
        Bitmap textureBitmap = null;
        try {
            textureBitmap = BitmapFactory.decodeStream(
                    context.getAssets().open(assetTextureName));
        } catch (IOException e) {
            Log.e("init", "init: while decoding " + assetTextureName + " " + e);
        }

        if (textureBitmap == null) {
            Log.e("init", "init: texture open failed. Ignoring texture.");
            return false;
        }


        // upload texture
        GLES20.glUseProgram(program);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glGenTextures(textures.length, textures, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, textureBitmap, 0);
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glUseProgram(0);
        ShaderUtil.checkGLError("init", "texture upload");
        /**********************/


        return false;
    }

    public void draw(List<GShape> shapes, float time, int width, int height, float[] viewProjectionMatrix) {
        GLES20.glUseProgram(program);
        GLES20.glEnableVertexAttribArray(positionLoc);
        GLES20.glEnableVertexAttribArray(uvLoc);

        GLES20.glUniform1f(uTimeLoc, time);
        GLES20.glUniform1f(uWidthLoc, width);
        GLES20.glUniform1f(uHeightLoc, height);
        GLES20.glUniformMatrix4fv(uViewProjectionLoc, 1, false, viewProjectionMatrix, 0);

        for (GShape shape : shapes) {
            GLES20.glVertexAttribPointer(
                    positionLoc, shape.coordsPerVertex, GLES20.GL_FLOAT, false,
                    shape.vertexStride, shape.vertexBuffer);

            if (shape.uvsBuffer != null) {
                GLES20.glVertexAttribPointer(
                        uvLoc, shape.uvsPerVertex, GLES20.GL_FLOAT,
                        false, 0, shape.uvsBuffer);
            }


            if (assetTextureName != null) {
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
                GLES20.glUniform1i(utextureLoc, 0);
            }

            if (textureOES != -1) {
                GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
                GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureOES);
                GLES20.glUniform1i(utextureLoc, 1);
            }

            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, shape.vertexCount);


            if (assetTextureName != null) {
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
                GLES20.glUniform1i(utextureLoc, 0);
            }
        }
        GLES20.glDisableVertexAttribArray(positionLoc);
        GLES20.glDisableVertexAttribArray(uvLoc);
        GLES20.glUseProgram(0);


    }

    private boolean compile() {
        program = ShaderUtil.createGLShaderProgram(vertexShaderCode, fragmentShaderCode);
        return program == -1;
    }
}
