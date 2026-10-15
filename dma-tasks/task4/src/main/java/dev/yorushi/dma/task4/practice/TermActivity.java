package dev.yorushi.dma.task4.practice;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;

/** P29: the description of one term. */
public class TermActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term);

        int[][] terms = {
                {R.string.p29_term0, R.string.p29_term0_text},
                {R.string.p29_term1, R.string.p29_term1_text},
                {R.string.p29_term2, R.string.p29_term2_text},
        };
        int[] term = terms[getIntent().getIntExtra(GlossaryActivity.EXTRA_TERM, 0)];
        ((TextView) findViewById(R.id.tvTerm)).setText(term[0]);
        ((TextView) findViewById(R.id.tvDescription)).setText(term[1]);
    }
}
