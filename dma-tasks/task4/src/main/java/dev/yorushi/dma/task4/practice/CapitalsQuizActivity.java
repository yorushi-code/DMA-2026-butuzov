package dev.yorushi.dma.task4.practice;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;

/** P18: a country, three capitals, one point per right answer. */
public class CapitalsQuizActivity extends AppCompatActivity {

    /** Each row: country, then the three options. */
    private static final int[][] QUESTIONS = {
            new int[] {R.string.p18_q0, R.string.p18_q0_o0, R.string.p18_q0_o1, R.string.p18_q0_o2},
            new int[] {R.string.p18_q1, R.string.p18_q1_o0, R.string.p18_q1_o1, R.string.p18_q1_o2},
            new int[] {R.string.p18_q2, R.string.p18_q2_o0, R.string.p18_q2_o1, R.string.p18_q2_o2},
            new int[] {R.string.p18_q3, R.string.p18_q3_o0, R.string.p18_q3_o1, R.string.p18_q3_o2}
    };
    private static final int[] ANSWERS = {0, 1, 2, 0};

    private Button[] buttons;
    private TextView tvProgress;
    private TextView tvCountry;
    private TextView tvFeedback;
    private TextView tvScore;
    private Button btnRestart;
    private int question;
    private int score;

    private void show() {
        boolean finished = question >= QUESTIONS.length;
        for (Button b : buttons) {
            b.setVisibility(finished ? View.GONE : View.VISIBLE);
        }
        btnRestart.setVisibility(finished ? View.VISIBLE : View.GONE);
        if (finished) {
            tvProgress.setText("");
            tvCountry.setText(getString(R.string.p18_final, score, QUESTIONS.length));
            tvScore.setText("");
            return;
        }
        tvProgress.setText(getString(R.string.p18_progress, question + 1, QUESTIONS.length));
        tvCountry.setText(QUESTIONS[question][0]);
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setText(QUESTIONS[question][i + 1]);
        }
        tvScore.setText(getString(R.string.p18_score, score));
    }

    private void answer(int choice) {
        int right = ANSWERS[question];
        if (choice == right) {
            score++;
            tvFeedback.setText(R.string.p18_right);
        } else {
            tvFeedback.setText(getString(R.string.p18_wrong, getString(QUESTIONS[question][right + 1])));
        }
        question++;
        show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capitals);

        buttons = new Button[] {findViewById(R.id.btnOption1), findViewById(R.id.btnOption2), findViewById(R.id.btnOption3)};
        tvProgress = findViewById(R.id.tvProgress);
        tvCountry = findViewById(R.id.tvCountry);
        tvFeedback = findViewById(R.id.tvFeedback);
        tvScore = findViewById(R.id.tvScore);
        btnRestart = findViewById(R.id.btnRestart);
        for (int i = 0; i < buttons.length; i++) {
            int choice = i;
            buttons[i].setOnClickListener(v -> answer(choice));
        }
        btnRestart.setOnClickListener(v -> {
            question = 0;
            score = 0;
            tvFeedback.setText("");
            show();
        });
        show();
    }
}
