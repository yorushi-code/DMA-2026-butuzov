package dev.yorushi.dma.task4.practice;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;

/** P25: large-print reading mode. */
public class NoteReadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_read);

        ((TextView) findViewById(R.id.tvNote)).setText(getIntent().getStringExtra(NoteEditActivity.EXTRA_NOTE));
        findViewById(R.id.btnEdit).setOnClickListener(v -> finish());
    }
}
