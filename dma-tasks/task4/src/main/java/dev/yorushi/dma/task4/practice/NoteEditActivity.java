package dev.yorushi.dma.task4.practice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;
import dev.yorushi.dma.task4.Ui;

/** P25: the note stays in the field, so Edit on the second screen simply returns here. */
public class NoteEditActivity extends AppCompatActivity {

    public static final String EXTRA_NOTE = "EXTRA_NOTE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);

        EditText etNote = findViewById(R.id.etNote);
        findViewById(R.id.btnRead).setOnClickListener(v -> {
            if (Ui.required(etNote)) {
                startActivity(new Intent(this, NoteReadActivity.class).putExtra(EXTRA_NOTE, etNote.getText().toString()));
            }
        });
    }
}
