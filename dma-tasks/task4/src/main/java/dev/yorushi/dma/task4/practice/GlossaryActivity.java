package dev.yorushi.dma.task4.practice;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;

/** P29: three terms, each opening its description. */
public class GlossaryActivity extends AppCompatActivity {

    public static final String EXTRA_TERM = "EXTRA_TERM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glossary);

        int[] buttons = {R.id.btnTerm0, R.id.btnTerm1, R.id.btnTerm2};
        for (int i = 0; i < buttons.length; i++) {
            int term = i;
            findViewById(buttons[i]).setOnClickListener(v ->
                    startActivity(new Intent(this, TermActivity.class).putExtra(EXTRA_TERM, term)));
        }
    }
}
