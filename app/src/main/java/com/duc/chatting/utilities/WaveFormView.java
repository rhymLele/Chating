package com.duc.chatting.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WaveFormView extends View {

    public WaveFormView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(Color.rgb(244, 81, 30));
        sw = (float) getResources().getDisplayMetrics().widthPixels;
        maxSpike = (int) (sw / (w + d));
    }

    private final Paint paint = new Paint();
    private final ArrayList<Float> amplitudes = new ArrayList<>();
    private final ArrayList<RectF> spikes = new ArrayList<>();
    private final float radius = 6f;
    private final float w = 9f;
    private final float sw;
    private final float d = 6f;
    private final int maxSpike;

    public void addAmplitude(Float amp) {
        float norm = Math.min(amp / 50, 190f);
        amplitudes.add(norm);
        spikes.clear();
        List<Float> amps = amplitudes.subList(Math.max(amplitudes.size() - maxSpike, 0), amplitudes.size());
        for (int i = 0; i < amps.size(); i++) {
            float left = sw - i * (w + d);
            float sh = 200f;
            float top = sh / 2 - amps.get(i) / 2;
            float right = left + w;
            float bottom = top + amps.get(i);
            spikes.add(new RectF(left, top, right, bottom));
            invalidate((int) left, (int) top, (int) right, (int) bottom);
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        spikes.forEach(it -> {
            canvas.drawRoundRect(it, radius, radius, paint);
        });

    }
}
