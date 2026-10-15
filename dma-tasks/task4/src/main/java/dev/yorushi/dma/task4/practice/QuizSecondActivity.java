package dev.yorushi.dma.task4.practice;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;

/** P23: question 2, then the total. */
public class QuizSecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_second);

        RadioGroup rgOptions = findViewById(R.id.rgOptions);
        TextView tvResult = findViewById(R.id.tvResult);
        int first = getIntent().getIntExtra(QuizFirstActivity.EXTRA_SCORE, 0);
        findViewById(R.id.btnFinish).setOnClickListener(v -> {
            int checked = rgOptions.getCheckedRadioButtonId();
            if (checked == View.NO_ID) {
                tvResult.setText(R.string.p23_pick);
                return;
            }
            tvResult.setText(getString(R.string.p23_total, first + (checked == R.id.rbOption1 ? 1 : 0)));
        });
    }
}
