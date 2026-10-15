package dev.yorushi.dma.task5.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import dev.yorushi.dma.task5.R;

/**
 * T6.2: a compound card. It is the LinearLayout itself; view_icon_card.xml has merge as
 * its root, so inflating it with attachToRoot adds the icon and the text directly here.
 */
public final class IconCardView extends LinearLayout {

    public IconCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.view_icon_card, this, true);

        ImageView icon = findViewById(R.id.card_icon);
        TextView text = findViewById(R.id.card_text);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IconCardView);
        try {
            icon.setImageDrawable(a.getDrawable(R.styleable.IconCardView_cardIcon));
            text.setText(a.getText(R.styleable.IconCardView_cardText));
        } finally {
            a.recycle();
        }
    }
}
