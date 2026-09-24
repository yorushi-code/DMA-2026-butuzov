package dev.yorushi.dma.task3.activity;

import android.net.Uri;
import dev.yorushi.dma.R;

/** P29: receives the OAuth redirect and extracts the authorisation code. */
public final class OAuthCallbackActivity extends IntentEchoActivity {

    @Override
    protected String itemId() {
        return "P29";
    }

    @Override
    protected int description() {
        return R.string.desc_oauth;
    }

    @Override
    protected String extraDetails() {
        Uri data = getIntent().getData();
        if (data == null) {
            return "";
        }
        return "\nauthorization code: " + data.getQueryParameter("code")
                + "\nstate: " + data.getQueryParameter("state");
    }
}
