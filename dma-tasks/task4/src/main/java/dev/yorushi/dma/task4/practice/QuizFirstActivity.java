package dev.yorushi.dma.task4.practice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;

/** P23: question 1; its point travels to the second screen. */
public class QuizFirstActivity extends AppCompatActivity {

    public static final String EXTRA_SCORE = "EXTRA_SCORE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_first);

        RadioGroup rgOptions = findViewById(R.id.rgOptions);
        TextView tvResult = findViewById(R.id.tvResult);
        findViewById(R.id.btnNext).setOnClickListener(v -> {
            int checked = rgOptions.getCheckedRadioButtonId();
            if (checked == View.NO_ID) {
                tvResult.setText(R.string.p23_pick);
                return;
            }
            tvResult.setText("");
            startActivity(new Intent(this, QuizSecondActivity.class)
                    .putExtra(EXTRA_SCORE, checked == R.id.rbOption0 ? 1 : 0));
        });
    }
}
