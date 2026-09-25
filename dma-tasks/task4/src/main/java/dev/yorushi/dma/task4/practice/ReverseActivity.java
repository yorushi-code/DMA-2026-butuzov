package dev.yorushi.dma.task4.practice;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;
import dev.yorushi.dma.task4.Ui;
import dev.yorushi.dma.task4.logic.Texts;

/** P05: StringBuilder.reverse(). */
public class ReverseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reverse);

        EditText etText = findViewById(R.id.etText);
        TextView tvResult = findViewById(R.id.tvResult);
        findViewById(R.id.btnReverse).setOnClickListener(v -> {
            if (Ui.required(etText)) {
                tvResult.setText(Texts.reverse(Ui.text(etText)));
            }
        });
    }
}
