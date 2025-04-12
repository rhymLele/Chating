package com.duc.chatting.utilities.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.text.TextPaint;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class GradientTextView  extends AppCompatTextView {

    public GradientTextView(Context context) {
        super(context);
    }

    public GradientTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GradientTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        TextPaint paint = getPaint();
        float width = paint.measureText(getText().toString());
        @SuppressLint("DrawAllocation") Shader shader = new LinearGradient(
                0, 0, width, getTextSize(),
                new int[]{  Color.parseColor("#1565C0"),
                        Color.parseColor("#0F4888")},  // startColor, endColor
                null,
                Shader.TileMode.CLAMP
        );
        paint.setShader(shader);

        super.onDraw(canvas);
    }
}
