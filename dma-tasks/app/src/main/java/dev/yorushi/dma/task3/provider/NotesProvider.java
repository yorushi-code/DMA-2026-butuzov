package dev.yorushi.dma.task3.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * P13/P14: notes provider guarded by separate read and write signature
 * permissions; only paths under /shared_docs/ can be granted to other apps.
 */
public final class NotesProvider extends ContentProvider {

    private static final String[] COLUMNS = {"_id", "path", "text"};
    private final List<Object[]> rows = new ArrayList<>();

    @Override
    public boolean onCreate() {
        rows.add(new Object[] {1L, "/notes/1", "Buy milk"});
        rows.add(new Object[] {2L, "/shared_docs/contract.txt", "Rental contract draft"});
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
            @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        MatrixCursor cursor = new MatrixCursor(COLUMNS);
        synchronized (rows) {
            for (Object[] row : rows) {
                if (((String) row[1]).startsWith(uri.getPath() == null ? "/" : uri.getPath())) {
                    cursor.addRow(row);
                }
            }
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return "vnd.android.cursor.dir/vnd.dev.yorushi.note";
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        String text = values == null ? "" : values.getAsString("text");
        synchronized (rows) {
            long id = rows.size() + 1L;
            rows.add(new Object[] {id, uri.getPath() + "/" + id, text});
            return Uri.withAppendedPath(uri, Long.toString(id));
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
            @Nullable String[] selectionArgs) {
        return 0;
    }
}
