package dev.yorushi.dma.task4.practice;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Random;

/** P27: the boarding pass with today's date and a generated seat. */
public class TicketActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        ((TextView) findViewById(R.id.tvRoute)).setText(getString(R.string.p27_route,
                getIntent().getStringExtra(TicketFormActivity.EXTRA_FROM),
                getIntent().getStringExtra(TicketFormActivity.EXTRA_TO)));
        ((TextView) findViewById(R.id.tvDate)).setText(getString(R.string.p27_date,
                LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))));
        Random random = new Random();
        ((TextView) findViewById(R.id.tvSeat)).setText(
                getString(R.string.p27_seat, random.nextInt(12) + 1, random.nextInt(54) + 1));
        ((TextView) findViewById(R.id.tvNumber)).setText(getString(R.string.p27_number,
                String.format(Locale.ROOT, "%010d", Math.abs(random.nextLong() % 10_000_000_000L))));
    }
}
