package dev.yorushi.dma.task4.practice;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;

/** P21: the account screen, greeting the user by name. */
public class CabinetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cabinet);

        ((TextView) findViewById(R.id.tvWelcome)).setText(
                getString(R.string.p21_welcome, getIntent().getStringExtra(LoginActivity.EXTRA_USER)));
        findViewById(R.id.btnLogout).setOnClickListener(v -> finish());
    }
}
