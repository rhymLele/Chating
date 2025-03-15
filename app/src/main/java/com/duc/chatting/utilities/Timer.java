package com.duc.chatting.utilities;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;


public class Timer {

    public interface OnTimerTickListener {
        void onTimerTick(String duration);
    }

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;
    private long duration = 0L;
    private long delay = 100L;

    public Timer(OnTimerTickListener listener) {
        runnable = new Runnable() {
            @Override
            public void run() {
                duration += delay;
                handler.postDelayed(runnable, delay);
                listener.onTimerTick(format());
            }
        };
    }

    @SuppressLint("DefaultLocale")
    public String format() {
        long mills = (duration % 1000) / 100;
        long seconds = (duration / 1000) % 60;
        return seconds + ":" + mills;
    }

    public void start() {
        handler.postDelayed(runnable, delay);
    }

    public void stop() {
        handler.removeCallbacks(runnable);
        duration = 0L;
    }
}
