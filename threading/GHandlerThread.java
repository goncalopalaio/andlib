package com.gplio.andlib.threading;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * Created by goncalopalaio on 27/05/18.
 */

public class GHandlerThread<O> {
    private final String name;
    private HandlerThread handlerThread;
    private Handler backgroundHandler;
    private Handler deliveryHandler;

    public GHandlerThread(String name) {
        this.name = name;

    }

    @SuppressWarnings("unchecked")
    public void resume(final Looper deliverToLooper, final Delivery delivery) {
        this.deliveryHandler = new Handler(deliverToLooper) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case DeliveryState.STARTED:
                        delivery.onStart();
                        break;
                    case DeliveryState.ERRORED:
                        delivery.onError();
                        break;
                    case DeliveryState.ENDED:
                        delivery.onEnd(msg.obj);
                        break;
                    default:
                        Log.d(getClass().getName(), "delivery handler: unknown message");
                }
            }
        };

        handlerThread = new HandlerThread(name);
        handlerThread.start();
        backgroundHandler = new Handler(handlerThread.getLooper());
    }
    private Runnable getJobRunnable(final Job<O> job) {
        return new Runnable() {
            @Override
            public void run() {
                deliveryHandler.sendMessage(deliveryHandler.obtainMessage(DeliveryState.STARTED));
                O output = job.work();
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
    public void post(final Job<O> job) {
        backgroundHandler.post(getJobRunnable(job));
    }

    public void postDelayed(final Job<O> job, long delayMillis) {
        backgroundHandler.postDelayed(getJobRunnable(job), delayMillis);
    }

    public void pause() {
        handlerThread.quit();
    }

    private static class DeliveryState {
        static final int STARTED = 1;
        static final int ERRORED = 2;
        static final int ENDED = 3;
    }

    public interface Job<O> {
        O work();
    }

    public interface Delivery<O> {
        void onStart();
        void onError();
        void onEnd(O obj);
    }
}
