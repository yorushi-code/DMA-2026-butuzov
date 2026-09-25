package dev.yorushi.dma.task4.practice;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import dev.yorushi.dma.task4.R;
import dev.yorushi.dma.task4.Ui;
import dev.yorushi.dma.task4.logic.Texts;

/** P03: "Correct!" in green or "Wrong!" in red. */
public class QuizOneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_one);

        EditText etAnswer = findViewById(R.id.etAnswer);
        TextView tvVerdict = findViewById(R.id.tvVerdict);
        findViewById(R.id.btnCheck).setOnClickListener(v -> {
            boolean ok = Texts.isCorrectAnswer(Ui.text(etAnswer), getString(R.string.p03_answer));
            tvVerdict.setText(ok ? R.string.p03_correct : R.string.p03_wrong);
            tvVerdict.setTextColor(ContextCompat.getColor(this, ok ? R.color.ok : R.color.error));
        });
    }
}
