package com.gplio.andlib.graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by goncalopalaio on 23/07/18.
 */

public class TextShape extends GShape {
    public static float QUAD_SIDE = 0.1f;
    private int textLength = 0;
    private int positionsLength;
    private int uvLength;

    public TextShape() {
        coordsPerVertex = 2;

    }

    public void updateBuffer(String text) {
        textLength = text.length();

        vertexBuffer = allocatePositionBuffer(text);
        uvsBuffer = allocateUvsBuffer(textLength);

        vertexStride = coordsPerVertex * BYTES_PER_FLOAT;
        vertexCount =  positionsLength / coordsPerVertex;
    }


    private FloatBuffer allocatePositionBuffer(String text) {
        // @note this will slooow
        float[] quadPositions = getQuadPositions();
        float[] quads = fasterBuildArrayWithLength(text, quadPositions);

        positionsLength = quads.length;
        return allocateBuffer(quads);
    }

    private FloatBuffer allocateUvsBuffer(int textLength) {
        // @note this will slooow
        float[] uvPositions = getUvPositions();
        float[] uvs = fasterBuildArrayWithLength(textLength, uvPositions);

        uvLength = uvs.length;
        return allocateBuffer(uvs);
    }

    private static FloatBuffer allocateBuffer(float[] values) {
        FloatBuffer floatBuffer = ByteBuffer
                .allocateDirect(values.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        floatBuffer
                .put(values).position(0);
        return floatBuffer;
    }


    private float[] fasterBuildArrayWithLength(String text, float[] baseArray) {
        int partialLen = baseArray.length;
        final int totalLen = partialLen * text.length();

        float[] quads = new float[totalLen];


        float xOffset = 0f;

        for (int j = 0; j < totalLen; j+=partialLen) {

            for (int i = 0; i < partialLen; i++) {

                if (i %2 == 0) {
                    // x's
                    quads[j + i] = baseArray[i] + (xOffset);
                } else {
                    quads[j + i] = baseArray[i];
                }
            }

            xOffset += (QUAD_SIDE + 0.2f);
        }

        return quads;
    }


    private float[] fasterBuildArrayWithLength(int textLength, float[] baseArray) {
        int partialLen = baseArray.length;
        final int totalLen = partialLen * textLength;

        float[] quads = new float[totalLen];

        for (int j = 0; j < totalLen; j+=partialLen) {
            System.arraycopy(baseArray, 0, quads, j, partialLen);
        }

        return quads;
    }


    private float[] getQuadPositions() {
        return new float[] {
                QUAD_SIDE, -QUAD_SIDE,
                QUAD_SIDE, QUAD_SIDE,
                -QUAD_SIDE, QUAD_SIDE,
                -QUAD_SIDE, QUAD_SIDE,
                -QUAD_SIDE, -QUAD_SIDE,
                QUAD_SIDE, -QUAD_SIDE
        };
    }

    private float[] getUvPositions() {
        return new float[] {
                1.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f,
                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f
        };
    }

}
