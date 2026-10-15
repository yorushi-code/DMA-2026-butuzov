package dev.yorushi.dma.task5.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import java.util.Random;

/**
 * Item 46: a layer of confetti over the whole result screen. A fixed seed keeps the
 * picture the same between runs (and in the screenshots). It draws only: it is not
 * clickable, so touches go through to the buttons under it.
 */
public final class ConfettiView extends View {

    private static final int[] COLORS = {0xFFFF6D00, 0xFF3D5AFE, 0xFF00C853, 0xFFF9A825, 0xFFF06292, 0xFF26C6DA};
    private static final int PIECES = 90;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Random random = new Random();
    private final float density;

    public ConfettiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        density = context.getResources().getDisplayMetrics().density;
        setClickable(false);
        setFocusable(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        random.setSeed(46);
        for (int i = 0; i < PIECES; i++) {
            float x = random.nextFloat() * getWidth();
            // Denser at the top, thinning out towards the bottom.
            float y = (float) Math.pow(random.nextFloat(), 1.6) * getHeight();
            float w = (4 + random.nextFloat() * 6) * density;
            float h = (8 + random.nextFloat() * 8) * density;
            paint.setColor(COLORS[random.nextInt(COLORS.length)]);
            canvas.save();
            canvas.rotate(random.nextFloat() * 360, x, y);
            if (random.nextBoolean()) {
                canvas.drawRect(x - w / 2, y - h / 2, x + w / 2, y + h / 2, paint);
            } else {
                canvas.drawCircle(x, y, w / 2 + density, paint);
            }
            canvas.restore();
        }
    }
}
