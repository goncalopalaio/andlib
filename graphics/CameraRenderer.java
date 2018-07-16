package andlib.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.gplio.faceoff.MainActivity;
import com.gplio.faceoff.RecognitionResult;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import andlib.threading.GHandlerThread;

import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.orthoM;
import static android.opengl.Matrix.setIdentityM;

/**
 * Created by goncalopalaio on 24/05/18.
 */

public class CameraRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "CameraRenderer";

    private final Context context;
    private final CameraIntoTexture cameraIntoTexture;
    private final GHandlerThread<RecognitionResult> recognitionThread;

    private class Camera {
        public GMath.V3<Float> eye;
        public GMath.V3<Float> center;
        public GMath.V3<Float> up;

        Camera() {
            eye = new GMath.V3<>(1f, 0.5f, 1f);
            center = new GMath.V3<>(0f, 0.5f, 0f);
            up = new GMath.V3<>(0f, 1.0f, 0f);
        }
    }

    private float tick = 0;

    private int height = 0;
    private int width = 0;

    GShader mainShader;
    List<GShape> shapes;
    private Camera camera = new Camera();

    private float[] viewMatrix = GMath.matrix4();
    private float[] projectionMatrix = GMath.matrix4();
    private float[] viewProjectionMatrix = GMath.matrix4();

    private float aspectRatio = 1.0f;
    private float left = -1f;
    private float right = 1f;
    private float bottom = -1f;
    private float top = 1f;
    private float near = -1f;
    private float far = 100f;
    //
    private ByteBuffer screenBuffer;

    public CameraRenderer(Context context, CameraIntoTexture cameraIntoTexture, GHandlerThread<RecognitionResult> recognitionThread) {

        this.context = context;
        this.cameraIntoTexture = cameraIntoTexture;
        this.recognitionThread = recognitionThread;
    }

    private void postScreenBuffer() {
        screenBuffer = ByteBuffer.allocate(width * height * 4); // note: this does not need to be here


        GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, screenBuffer);
        final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(screenBuffer);
        this.recognitionThread.postDelayed(new GHandlerThread.Job<RecognitionResult>() {
            @Override
            public RecognitionResult work() {
                Log.d(TAG, "work: " + bitmap.getWidth() + " : " + bitmap.getHeight());
                RecognitionResult result = ((MainActivity) context).infer(bitmap);
                result.bitmap = bitmap;
                return result;
            }
        }, 1000);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.5f, 0.5f , 1.0f, 1.0f);

        mainShader = new FunkyCameraTextureShader(cameraIntoTexture.getTextureOES());
        mainShader.init(context);

        shapes = new ArrayList<>(1);
        shapes.add(new QuadShape());

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int w, int h) {
        width = w;
        height = h;


        if (width > height) {
            aspectRatio = width / ((float)height);
            left = -aspectRatio;
            right = aspectRatio;
        } else {
            aspectRatio = height / ((float) width);
            bottom = -aspectRatio;
            top = aspectRatio;
        }

        orthoM(projectionMatrix, 0, left, right, bottom, top, near, far);
        setIdentityM(viewMatrix, 0);
        GMath.lookAt(viewMatrix, camera.eye, camera.center, camera.up);
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(0.5f, (float) Math.sin(tick) , 0.4f, 1.0f);

        cameraIntoTexture.update();
        mainShader.draw(shapes, tick);

        postScreenBuffer();
        tick += 0.1f;
    }
}
