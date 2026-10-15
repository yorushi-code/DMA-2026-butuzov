package dev.yorushi.dma.task5.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/** A FrameLayout whose height equals its width: grid tiles (items 26, 29, 38). */
public class SquareFrameLayout extends FrameLayout {

    public SquareFrameLayout(Context context) {
        super(context);
    }

    public SquareFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
