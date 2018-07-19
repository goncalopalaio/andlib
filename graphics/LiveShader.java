package com.gplio.andlib.graphics;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.gplio.andlib.files.ExternalFileObserver;
import com.gplio.andlib.files.TextFiles;

/**
 * Created by goncalopalaio on 19/07/18.
 */

public class LiveShader extends GShader implements ExternalFileObserver.Listener {
    private final String defaultVertexShaderCode;
    private final String defaultFragmentShaderCode;
    private ExternalFileObserver observer;

    private volatile String currentVertexShaderCode ="";
    private volatile String currentFragmentShaderCode = "";
    private volatile boolean dirty = false;

    private final String folderPath;
    protected final String vertexShaderFile;
    protected final String fragmentShaderFile;


    @Nullable
    private Context context;

    public LiveShader(String defaultVertexShaderCode, String defaultFragmentShaderCode, String folderPath, String vertexShaderFile, String fragmentShaderFile) {
        super(defaultVertexShaderCode, defaultFragmentShaderCode);

        this.defaultVertexShaderCode = defaultVertexShaderCode;
        this.defaultFragmentShaderCode = defaultFragmentShaderCode;

        observer = new ExternalFileObserver(folderPath);
        this.folderPath = folderPath;
        this.vertexShaderFile = vertexShaderFile;
        this.fragmentShaderFile = fragmentShaderFile;

        log("Observing: " + folderPath + " :: " + vertexShaderFile + " , " + fragmentShaderFile);
    }

    public void subscribe(@Nullable Context context) {
        if (context == null) {
            log("Context was null");
            return;
        }
        log("Subscribing to LiveShader: " + this);
        this.context = context;
        observer.subscribe(this);
        observer.startWatching();
    }

    public void unsubscribe() {
        this.context = null;
        observer.stopWatching();
        observer.unsubscribe(this);

    }

    @Override
    public void onEvent() {

        String updatedVertexShader = TextFiles.readTextFromSdcard(folderPath + vertexShaderFile, "");
        String updatedFragmentShader = TextFiles.readTextFromSdcard(folderPath + fragmentShaderFile, "");

        if (updatedVertexShader.isEmpty() || updatedFragmentShader.isEmpty()) {
            log("Updated vertex or fragment shader was empty");
            return;
        }

        currentVertexShaderCode = updatedVertexShader;
        currentFragmentShaderCode = updatedFragmentShader;

        dirty = true;

        log("Marked as dirty");
    }

    public boolean isDirty() {
        return dirty;
    }

    public void recompileShader(Context context) {
        vertexShaderCode = currentVertexShaderCode;
        fragmentShaderCode = currentFragmentShaderCode;
        boolean error = init(context);

        if (error) {
            log("Falling back to default shader");
            vertexShaderCode = defaultVertexShaderCode;
            fragmentShaderCode = defaultFragmentShaderCode;
            error = init(context);

            if (error) {
                log("Could not fallback to default shader");
            }
        }

        dirty = false;
    }

    private static void log(String msg) {
        Log.d("LiveShader", msg);
    }
}
