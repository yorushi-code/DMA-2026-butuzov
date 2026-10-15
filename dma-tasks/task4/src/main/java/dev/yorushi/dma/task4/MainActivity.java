package dev.yorushi.dma.task4;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

/** The menu: one button per screen, grouped by the sections of the assignment. */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout menu = findViewById(R.id.menu);
        int section = 0;
        for (Catalog.Entry entry : Catalog.ENTRIES) {
            if (entry.section != section) {
                section = entry.section;
                TextView header = (TextView) getLayoutInflater().inflate(R.layout.item_section, menu, false);
                header.setText(section);
                menu.addView(header);
            }
            MaterialButton button = (MaterialButton) getLayoutInflater().inflate(R.layout.item_entry, menu, false);
            button.setText(getString(R.string.menu_entry, entry.items, getString(entry.title)));
            button.setOnClickListener(v -> startActivity(new Intent(this, entry.activity)));
            menu.addView(button);
        }
    }
}
