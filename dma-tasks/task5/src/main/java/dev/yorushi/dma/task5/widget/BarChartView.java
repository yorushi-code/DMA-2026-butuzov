package dev.yorushi.dma.task5.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import androidx.core.content.ContextCompat;
import dev.yorushi.dma.task5.R;

/**
 * Item 24: the week's sales as seven rounded bars with day labels; the best day is
 * highlighted. Stands in for a chart library, which the layout work does not need.
 */
public final class BarChartView extends View {

    private static final float[] VALUES = {42, 58, 35, 71, 64, 90, 77};
    private static final int BEST = 5;

    private final Paint bar = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint label = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rect = new RectF();
    private final String[] days;
    private final float radius;

    public BarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        days = context.getResources().getStringArray(R.array.week_days);
        float density = context.getResources().getDisplayMetrics().density;
        radius = 6 * density;
        label.setTextSize(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 12, context.getResources().getDisplayMetrics()));
        label.setColor(ContextCompat.getColor(context, R.color.text_muted));
        label.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float labelHeight = label.getTextSize() * 1.8f;
        float chartHeight = getHeight() - getPaddingTop() - getPaddingBottom() - labelHeight;
        float width = getWidth() - getPaddingLeft() - getPaddingRight();
        float slot = width / VALUES.length;
        float barWidth = slot * 0.5f;
        float max = 0;
        for (float v : VALUES) {
            max = Math.max(max, v);
        }
        for (int i = 0; i < VALUES.length; i++) {
            float cx = getPaddingLeft() + slot * i + slot / 2;
            float top = getPaddingTop() + chartHeight * (1 - VALUES[i] / max);
            rect.set(cx - barWidth / 2, top, cx + barWidth / 2, getPaddingTop() + chartHeight);
            bar.setColor(ContextCompat.getColor(getContext(), i == BEST ? R.color.brand : R.color.brand_soft));
            canvas.drawRoundRect(rect, radius, radius, bar);
            canvas.drawText(days[i], cx, getHeight() - getPaddingBottom() - label.getTextSize() * 0.3f, label);
        }
    }
}
