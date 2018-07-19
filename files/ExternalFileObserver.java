package com.gplio.andlib.files;

import android.os.FileObserver;
import android.support.annotation.Nullable;
import android.util.Log;

import com.gplio.fibrewallpaper.BuildConfig;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by goncalopalaio on 19/07/18.
 */

public class ExternalFileObserver extends FileObserver {
    private static final String TAG = "ExternalFileObserver";
    private final List<Listener> listeners;

    public ExternalFileObserver(String path) {
        super(path, FileObserver.ALL_EVENTS);
        listeners = new LinkedList<>();

        if (BuildConfig.DEBUG) log("New ExternalFileObserver" + path);
    }

    public void subscribe(Listener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
            return;
        }

        if (BuildConfig.DEBUG) log("subscribe ignored for " + listener);
    }

    public void unsubscribe(Listener listener) {
        listeners.remove(listener);
    }

    @Override
    public void onEvent(int event, @Nullable String path) {
        if (event == MODIFY || event == CREATE) {
            log("Received MODIFY or CREATE");
            for (Listener listener : listeners) {
                listener.onEvent();
            }
        }
    }

    public interface Listener {
        void onEvent();
    }

    @Override
    public void startWatching() {
        super.startWatching();

        log("startWatching");
    }

    @Override
    public void stopWatching() {
        super.stopWatching();

        log("stopWatching");
    }

    private static void log(String msg) {
        Log.d(TAG, msg);
    }
}
