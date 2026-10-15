package dev.yorushi.dma.task5.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import androidx.core.content.ContextCompat;
import dev.yorushi.dma.task5.R;

/**
 * Item 37: the amplitude of a recording as a row of vertical bars. Without a microphone
 * the amplitude is synthesised from a few sines; start() and stop() run the animation.
 */
public final class WaveformView extends View {

    private static final int BARS = 36;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final ValueAnimator animator = ValueAnimator.ofFloat(0f, (float) (2 * Math.PI));
    private float phase;

    public WaveformView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(ContextCompat.getColor(context, R.color.error));
        paint.setStrokeCap(Paint.Cap.ROUND);
        animator.setDuration(1600);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(a -> {
            phase = (float) a.getAnimatedValue();
            invalidate();
        });
    }

    public void start() {
        if (!animator.isStarted()) {
            animator.start();
        }
    }

    public void stop() {
        animator.cancel();
        phase = 0;
        invalidate();
    }

    public boolean isRunning() {
        return animator.isRunning();
    }

    /** Bar height as a share of the view's height, in 0.15..1. */
    static float amplitude(int bar, float phase) {
        double x = bar / (double) BARS * 2 * Math.PI;
        double v = Math.sin(x * 3 + phase) * 0.5 + Math.sin(x * 7 - phase * 2) * 0.3 + Math.sin(x * 13 + phase * 3) * 0.2;
        return (float) (0.15 + 0.85 * Math.abs(v));
    }

    @Override
    protected void onDetachedFromWindow() {
        animator.cancel();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float width = getWidth() - getPaddingLeft() - getPaddingRight();
        float slot = width / BARS;
        paint.setStrokeWidth(slot * 0.55f);
        float mid = getHeight() / 2f;
        float maxHalf = (getHeight() - getPaddingTop() - getPaddingBottom()) / 2f;
        for (int i = 0; i < BARS; i++) {
            float x = getPaddingLeft() + slot * i + slot / 2;
            float half = maxHalf * amplitude(i, phase);
            canvas.drawLine(x, mid - half, x, mid + half, paint);
        }
    }
}
