package dev.yorushi.dma.task3.activity;

import android.app.PictureInPictureParams;
import android.content.res.Configuration;
import android.util.Rational;
import dev.yorushi.dma.R;

/** P05: shrinks into a Picture-in-Picture window when the user leaves the screen. */
public final class PipPlayerActivity extends IntentEchoActivity {

    @Override
    protected String itemId() {
        return "P05";
    }

    @Override
    protected int description() {
        return R.string.desc_pip;
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        enterPictureInPictureMode(new PictureInPictureParams.Builder()
                .setAspectRatio(new Rational(16, 9))
                .build());
    }

    @Override
    public void onPictureInPictureModeChanged(boolean inPictureInPicture, Configuration newConfig) {
        super.onPictureInPictureModeChanged(inPictureInPicture, newConfig);
        recreateDetails();
    }

    private void recreateDetails() {
        onNewIntent(getIntent());
    }

    @Override
    protected String extraDetails() {
        return "\nPicture-in-Picture: " + (isInPictureInPictureMode() ? "active" : "ready (press Home)");
    }
}
