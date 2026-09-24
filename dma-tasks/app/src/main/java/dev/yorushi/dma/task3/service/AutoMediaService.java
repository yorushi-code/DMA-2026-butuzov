package dev.yorushi.dma.task3.service;

import android.media.browse.MediaBrowser;
import android.os.Bundle;
import android.service.media.MediaBrowserService;
import java.util.Collections;
import java.util.List;

/** P43: media browser the Android Auto head unit connects to. */
public final class AutoMediaService extends MediaBrowserService {

    @Override
    public BrowserRoot onGetRoot(String clientPackageName, int clientUid, Bundle rootHints) {
        return new BrowserRoot("root", null);
    }

    @Override
    public void onLoadChildren(String parentId, Result<List<MediaBrowser.MediaItem>> result) {
        result.sendResult(Collections.emptyList());
    }
}
