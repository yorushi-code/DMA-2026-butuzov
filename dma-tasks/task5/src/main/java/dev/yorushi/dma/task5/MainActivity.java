package dev.yorushi.dma.task5;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import java.util.List;

/** The menu: one button per item, grouped by the topics and blocks of the assignment. */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout menu = findViewById(R.id.menu);
        List<Catalog.Entry> entries = Catalog.ENTRIES;
        int section = 0;
        for (int i = 0; i < entries.size(); i++) {
            Catalog.Entry entry = entries.get(i);
            if (entry.section != section) {
                section = entry.section;
                TextView header = (TextView) getLayoutInflater().inflate(R.layout.item_section, menu, false);
                header.setText(section);
                menu.addView(header);
            }
            MaterialButton button = (MaterialButton) getLayoutInflater().inflate(R.layout.item_entry, menu, false);
            button.setText(getString(R.string.menu_entry, entry.label, getString(entry.title)));
            int index = i;
            button.setOnClickListener(v -> startActivity(ScreenActivity.intent(this, index)));
            menu.addView(button);
        }
    }
}
