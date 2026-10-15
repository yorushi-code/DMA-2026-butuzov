package dev.yorushi.dma.task5.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.google.android.material.button.MaterialButton;

/**
 * T1.1: a button exactly as tall as the width its weight gives it. With a 50% corner
 * shape the square becomes a circle, whatever the screen width.
 */
public class SquareButton extends MaterialButton {

    public SquareButton(Context context) {
        super(context);
    }

    public SquareButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        int side = getMeasuredWidth();
        setMeasuredDimension(side, side);
    }
}
