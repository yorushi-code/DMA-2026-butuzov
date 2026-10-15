package dev.yorushi.dma.task5.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import androidx.core.content.ContextCompat;
import dev.yorushi.dma.task5.R;

/**
 * Item 44: darkens the camera preview everywhere except a centred square window and
 * draws a glowing frame around the window. The scrim is one path with an even-odd hole,
 * so the window is genuinely transparent rather than painted over.
 */
public final class ScannerOverlayView extends View {

    private final Paint scrim = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint frame = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint glow = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path = new Path();
    private final RectF window = new RectF();
    private final float density;

    public ScannerOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        density = context.getResources().getDisplayMetrics().density;
        scrim.setColor(ContextCompat.getColor(context, R.color.scrim));
        int accent = ContextCompat.getColor(context, R.color.online);
        frame.setColor(accent);
        frame.setStyle(Paint.Style.STROKE);
        frame.setStrokeWidth(4 * density);
        frame.setStrokeCap(Paint.Cap.ROUND);
        glow.setColor(accent);
        glow.setAlpha(70);
        glow.setStyle(Paint.Style.STROKE);
        glow.setStrokeWidth(12 * density);
    }

    /** The transparent window, in this view's coordinates. */
    public RectF window() {
        return window;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        float side = Math.min(w, h) * 0.68f;
        float left = (w - side) / 2;
        float top = (h - side) / 2;
        window.set(left, top, left + side, top + side);
        float r = 20 * density;
        path.reset();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.addRect(0, 0, w, h, Path.Direction.CW);
        path.addRoundRect(window, r, r, Path.Direction.CW);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path, scrim);
        float r = 20 * density;
        canvas.drawRoundRect(window, r, r, glow);
        float len = window.width() * 0.18f;
        float l = window.left;
        float t = window.top;
        float rr = window.right;
        float b = window.bottom;
        // Four corner brackets.
        canvas.drawLine(l, t + len, l, t + r, frame);
        canvas.drawLine(l + r, t, l + len, t, frame);
        canvas.drawLine(rr - len, t, rr - r, t, frame);
        canvas.drawLine(rr, t + r, rr, t + len, frame);
        canvas.drawLine(l, b - len, l, b - r, frame);
        canvas.drawLine(l + r, b, l + len, b, frame);
        canvas.drawLine(rr - len, b, rr - r, b, frame);
        canvas.drawLine(rr, b - r, rr, b - len, frame);
        canvas.drawArc(l, t, l + 2 * r, t + 2 * r, 180, 90, false, frame);
        canvas.drawArc(rr - 2 * r, t, rr, t + 2 * r, 270, 90, false, frame);
        canvas.drawArc(l, b - 2 * r, l + 2 * r, b, 90, 90, false, frame);
        canvas.drawArc(rr - 2 * r, b - 2 * r, rr, b, 0, 90, false, frame);
    }
}
