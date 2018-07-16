package com.gplio.andlib.graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by goncalopalaio on 26/05/18.
 */

public class QuadShape extends GShape {
    private static FloatBuffer sPositionsBuffer = null;
    private static FloatBuffer sUvsBuffer = null;
    private static int sPositionsLength;
    private static int sUvsLength;

    public QuadShape() {
        vertexBuffer = getPositionBuffer();
        uvsBuffer = getUvsBuffer();
        vertexStride = coordsPerVertex * BYTES_PER_FLOAT;
        vertexCount =  sPositionsLength / coordsPerVertex;
    }

    private static FloatBuffer getPositionBuffer() {
        if (sPositionsBuffer == null) {
            sPositionsBuffer = allocatePositionBuffer();
        }
        sPositionsBuffer.position(0);
        return sPositionsBuffer;
    }

    private static FloatBuffer getUvsBuffer() {
        if (sUvsBuffer == null) {
           sUvsBuffer = allocateUvsBuffer();
        }
        sUvsBuffer.position(0);
        return sUvsBuffer;
    }

    private static FloatBuffer allocatePositionBuffer() {
        float[] positions = {
                0.5f, -0.5f, 0f,
                0.5f, 0.5f, 0f,
                -0.5f, 0.5f, 0f,
                -0.5f, 0.5f, 0f,
                -0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0f
        };
        sPositionsLength = positions.length;
        return allocateBuffer(positions);
    }

    private static FloatBuffer allocateUvsBuffer() {
        float[] uvs = {
                1.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f,
                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f
        };
        sUvsLength = uvs.length;
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
}
