package dev.yorushi.dma.task3.activity;

import android.app.SearchManager;
import dev.yorushi.dma.R;

/** P27 and T7.3: receives ACTION_SEARCH with the query typed by the user. */
public final class SearchActivity extends IntentEchoActivity {

    @Override
    protected String itemId() {
        return "P27";
    }

    @Override
    protected int description() {
        return R.string.desc_search;
    }

    @Override
    protected String extraDetails() {
        String query = getIntent().getStringExtra(SearchManager.QUERY);
        return "\nsearch query: " + (query == null ? "-" : "\"" + query + "\"");
    }
}
