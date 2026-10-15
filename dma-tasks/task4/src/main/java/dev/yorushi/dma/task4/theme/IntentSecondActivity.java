package dev.yorushi.dma.task4.theme;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;

/** Theme 6, second screen. T6.3 null name shown as "Guest"; T6.5 browser via an implicit Intent. */
public class IntentSecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_second);

        Intent incoming = getIntent();
        String name = incoming.getStringExtra(IntentStartActivity.EXTRA_NAME);
        ((TextView) findViewById(R.id.tvHello)).setText(
                getString(R.string.t6_hello, name == null ? getString(R.string.guest) : name));

        // T6.2 and T6.4: the boolean and the double travel on to the third screen.
        findViewById(R.id.btnAbout).setOnClickListener(v -> {
            Intent about = new Intent(this, AboutActivity.class);
            about.putExtra(IntentStartActivity.EXTRA_AGREED,
                    incoming.getBooleanExtra(IntentStartActivity.EXTRA_AGREED, false));
            about.putExtra(IntentStartActivity.EXTRA_BALANCE,
                    incoming.getDoubleExtra(IntentStartActivity.EXTRA_BALANCE, 0));
            startActivity(about);
        });
        findViewById(R.id.btnBrowser).setOnClickListener(v ->
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://google.com"))));
    }
}
