package com.gplio.andlib.threading;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by goncalopalaio on 27/05/18.
 */

public class GHandlerThread<W> {
    private final String name;
    @Nullable
    private HandlerThread handlerThread;
    private Handler backgroundHandler;
    private Handler deliveryHandler;

    public GHandlerThread(String name) {
        this.name = name;

    }

    @SuppressWarnings("unchecked")
    public void resume(final Looper deliverToLooper, final DeliveryEvent deliveryEvent) {
        this.deliveryHandler = new Handler(deliverToLooper) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case DeliveryState.STARTED:
                        deliveryEvent.onStart();
                        break;
                    case DeliveryState.ERRORED:
                        deliveryEvent.onError();
                        break;
                    case DeliveryState.ENDED:
                        deliveryEvent.onEnd(msg.obj);
                        break;
                    default:
                        Log.d(getClass().getName(), "deliveryEvent handler: unknown message");
                }
            }
        };

        Log.d(getClass().getName(), "resume : starting handler thread");
        handlerThread = new HandlerThread(name);
        handlerThread.start();
        backgroundHandler = new Handler(handlerThread.getLooper());

    }
    private Runnable getJobRunnable(final Job<W> job) {
        return new Runnable() {
            @Override
            public void run() {
                deliveryHandler.sendMessage(deliveryHandler.obtainMessage(DeliveryState.STARTED));
                W output = job.work();
                if (output == null) {
                    deliveryHandler.sendMessage(deliveryHandler.obtainMessage(DeliveryState.ERRORED));
                } else {
                    Message message = deliveryHandler.obtainMessage(DeliveryState.ENDED);
                    message.obj = output;
                    deliveryHandler.sendMessage(message);
                }

            }
        };
    }
    public void post(final Job<W> job) {
        backgroundHandler.post(getJobRunnable(job));
    }

    public void postDelayed(final Job<W> job, long delayMillis) {
        backgroundHandler.postDelayed(getJobRunnable(job), delayMillis);
    }

    public void pause() {
        if (handlerThread != null) {
            handlerThread.quit();
        }

    }

    private static class DeliveryState {
        static final int STARTED = 1;
        static final int ERRORED = 2;
        static final int ENDED = 3;
    }

    public interface Job<O> {
        O work();
    }

    public interface DeliveryEvent<O> {
        void onStart();
        void onError();
        void onEnd(O obj);
    }
}
