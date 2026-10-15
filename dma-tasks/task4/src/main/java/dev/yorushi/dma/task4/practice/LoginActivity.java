package dev.yorushi.dma.task4.practice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;
import dev.yorushi.dma.task4.Ui;

/** P21: admin / 1234 opens the account screen. */
public class LoginActivity extends AppCompatActivity {

    public static final String EXTRA_USER = "EXTRA_USER";
    // The assignment's hardcoded demo account.
    private static final String LOGIN = "admin";
    private static final String PASSWORD = "1234";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText etLogin = findViewById(R.id.etLogin);
        EditText etPassword = findViewById(R.id.etPassword);
        TextView tvError = findViewById(R.id.tvError);
        findViewById(R.id.btnLogin).setOnClickListener(v -> {
            String login = Ui.text(etLogin);
            if (LOGIN.equals(login) && PASSWORD.equals(etPassword.getText().toString())) {
                tvError.setText("");
                startActivity(new Intent(this, CabinetActivity.class).putExtra(EXTRA_USER, login));
            } else {
                tvError.setText(R.string.p21_wrong);
            }
        });
    }
}
