package dev.yorushi.dma.task3.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.R;
import java.util.Set;
import java.util.TreeSet;

/**
 * Base for the activities that exist to demonstrate a manifest declaration. It
 * shows which assignment item the screen implements and the intent that
 * actually reached it, which proves the filter routed the intent here.
 */
public abstract class IntentEchoActivity extends AppCompatActivity {

    /** Assignment item id, e.g. {@code T4.1} or {@code P25}. */
    protected abstract String itemId();

    @StringRes
    protected abstract int description();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_echo);
        ((TextView) findViewById(R.id.itemId)).setText(getString(R.string.echo_item, itemId()));
        ((TextView) findViewById(R.id.title)).setText(getClass().getSimpleName());
        ((TextView) findViewById(R.id.description)).setText(description());
        render(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        render(intent);
    }

    private void render(Intent intent) {
        StringBuilder text = new StringBuilder();
        line(text, R.string.echo_component, getComponentName().flattenToShortString());
        line(text, R.string.echo_action, intent.getAction());
        Uri data = intent.getData();
        line(text, R.string.echo_data, data == null ? null : data.toString());
        line(text, R.string.echo_type, intent.getType());
        line(text, R.string.echo_categories, intent.getCategories() == null ? null
                : String.join(", ", new TreeSet<>(intent.getCategories())));
        Bundle extras = intent.getExtras();
        if (extras != null && !extras.isEmpty()) {
            StringBuilder values = new StringBuilder();
            Set<String> keys = new TreeSet<>(extras.keySet());
            for (String key : keys) {
                values.append("\n  ").append(key).append(" = ").append(extras.get(key));
            }
            line(text, R.string.echo_extras, values.toString());
        } else {
            line(text, R.string.echo_extras, null);
        }
        text.append(extraDetails());
        ((TextView) findViewById(R.id.details)).setText(text.toString().trim());
    }

    /** Screen-specific facts appended below the intent, empty by default. */
    protected String extraDetails() {
        return "";
    }

    private void line(StringBuilder text, @StringRes int label, String value) {
        text.append(getString(label)).append(": ")
                .append(value == null || value.isEmpty() ? getString(R.string.echo_none) : value)
                .append('\n');
    }
}
