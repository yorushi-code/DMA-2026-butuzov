package dev.yorushi.dma.task5;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Shows one item of the catalog: inflates its layout and, if the item has any,
 * attaches its behaviour. Most items are pure layout work and need no code.
 */
public class ScreenActivity extends AppCompatActivity {

    static final String EXTRA_INDEX = "index";

    public static Intent intent(Context context, int index) {
        return new Intent(context, ScreenActivity.class).putExtra(EXTRA_INDEX, index);
    }

    private Catalog.Entry entry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        entry = Catalog.ENTRIES.get(getIntent().getIntExtra(EXTRA_INDEX, 0));
        setTitle(getString(R.string.screen_title, entry.label, getString(entry.title)));
        ActionBar bar = getSupportActionBar();
        if (entry.immersive && bar != null) {
            // Full-screen items draw their own top bar over the content.
            bar.hide();
        }
        setContentView(entry.layout);
        dropHiddenWindowBackground();

        // T6.1: every screen that includes toolbar_common.xml gets a working back button.
        View back = findViewById(R.id.toolbar_back);
        if (back != null) {
            back.setOnClickListener(v -> finish());
        }
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        if (toolbarTitle != null) {
            toolbarTitle.setText(entry.title);
        }

        if (entry.behavior != null) {
            entry.behavior.attach(this);
        }
    }

    /**
     * Topic 7: if the layout's root fills the screen with an opaque background, every
     * pixel of the window background would be painted and then painted over at once.
     * Removing it saves one full-screen layer of overdraw (items 33, 39, 44, 50).
     */
    private void dropHiddenWindowBackground() {
        ViewGroup content = findViewById(android.R.id.content);
        View root = content.getChildAt(0);
        ViewGroup.LayoutParams lp = root.getLayoutParams();
        Drawable background = root.getBackground();
        boolean fills = lp.width == ViewGroup.LayoutParams.MATCH_PARENT
                && lp.height == ViewGroup.LayoutParams.MATCH_PARENT;
        if (entry.immersive && fills && background instanceof ColorDrawable
                && ((ColorDrawable) background).getAlpha() == 255) {
            getWindow().setBackgroundDrawable(null);
        }
    }

    public Catalog.Entry entry() {
        return entry;
    }
}
