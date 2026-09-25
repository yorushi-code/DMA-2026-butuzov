package dev.yorushi.dma.task4.practice;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;

/** P24: the card; Call opens the dialer with the number (ACTION_DIAL). */
public class MasterCardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_card);

        String phone = getIntent().getStringExtra(MasterFormActivity.EXTRA_PHONE);
        ((TextView) findViewById(R.id.tvName)).setText(getIntent().getStringExtra(MasterFormActivity.EXTRA_NAME));
        ((TextView) findViewById(R.id.tvJob)).setText(getIntent().getStringExtra(MasterFormActivity.EXTRA_JOB));
        ((TextView) findViewById(R.id.tvPhone)).setText(phone);
        // ACTION_DIAL only fills in the dialer; it needs no CALL_PHONE permission.
        findViewById(R.id.btnCall).setOnClickListener(v ->
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone))));
    }
}
