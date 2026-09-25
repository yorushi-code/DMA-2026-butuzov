package dev.yorushi.dma.task4.practice;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;
import dev.yorushi.dma.task4.logic.Texts;

/** P07: updated on every keystroke. */
public class CharCountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_char_count);

        EditText etText = findViewById(R.id.etText);
        TextView tvCount = findViewById(R.id.tvCount);
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                tvCount.setText(getString(R.string.p07_count, Texts.countLetters(s.toString()), s.length()));
            }
        };
        etText.addTextChangedListener(watcher);
        watcher.afterTextChanged(etText.getText());
    }
}
