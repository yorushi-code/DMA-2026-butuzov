package dev.yorushi.dma.task4.practice;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import dev.yorushi.dma.task4.R;
import dev.yorushi.dma.task4.logic.Texts;

/** P20: rated by length as it is typed. */
public class PasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        EditText etPassword = findViewById(R.id.etPassword);
        TextView tvStrength = findViewById(R.id.tvStrength);
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    tvStrength.setText("");
                    return;
                }
                int text;
                int color;
                switch (Texts.passwordStrength(s.toString())) {
                    case WEAK:
                        text = R.string.p20_weak;
                        color = R.color.error;
                        break;
                    case MEDIUM:
                        text = R.string.p20_medium;
                        color = R.color.warning;
                        break;
                    default:
                        text = R.string.p20_strong;
                        color = R.color.ok;
                }
                tvStrength.setText(text);
                tvStrength.setTextColor(ContextCompat.getColor(PasswordActivity.this, color));
            }
        });
    }
}
