package dev.yorushi.dma.task4.practice;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import dev.yorushi.dma.task4.R;

/** P30: the photo and three sections, each on its own screen. */
public class PortfolioActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    public static final String EXTRA_TEXT = "EXTRA_TEXT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);

        int[][] sections = {
                {R.id.btnAbout, R.string.p30_about, R.string.p30_about_text},
                {R.id.btnSkills, R.string.p30_skills, R.string.p30_skills_text},
                {R.id.btnContacts, R.string.p30_contacts, R.string.p30_contacts_text},
        };
        for (int[] section : sections) {
            findViewById(section[0]).setOnClickListener(v -> startActivity(new Intent(this, PortfolioDetailActivity.class)
                    .putExtra(EXTRA_TITLE, section[1])
                    .putExtra(EXTRA_TEXT, section[2])));
        }
    }
}
