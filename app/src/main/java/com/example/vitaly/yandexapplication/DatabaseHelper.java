package com.example.vitaly.yandexapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Vitaly on 19.04.2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "notes.db";
    private static final int VERSION_X = 10;
    private static final int VERSION = VERSION_X;
    private static final String TABLE_NOTES = "notes";

    public static final String COLUMN_ID = BaseColumns._ID;
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_COLOR = "color";
    public static final String COLUMN_CREATED = "created";
    public static final String COLUMN_EDITED = "edited";
    public static final String COLUMN_VIEWED = "viewed";

    public static HashMap<SortOption, String> sortOptions = new HashMap<SortOption, String>()
    {
        {
            put(SortOption.CREATING_DATE, COLUMN_CREATED);
            put(SortOption.EDITION_DATE, COLUMN_EDITED);
            put(SortOption.VIEW_DATE, COLUMN_VIEWED);
            put(SortOption.NAME, COLUMN_TITLE);
        }
    };

    public static HashMap<SortMode, String> sortModes = new HashMap<SortMode, String>()
    {
        {
            put(SortMode.ASCENDING, "ASC");
            put(SortMode.DESCENDING, "DESC");
        }
    };

    private String[] columns =
            {
                    COLUMN_TITLE,
                    COLUMN_DESCRIPTION,
                    COLUMN_COLOR,
                    COLUMN_CREATED,
                    COLUMN_VIEWED,
                    COLUMN_EDITED
            };

    private static final String SQL_CREATE_TABLE_NOTES =
            "CREATE TABLE " + TABLE_NOTES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_CREATED + " TEXT, " +
                    COLUMN_EDITED + " TEXT, " +
                    COLUMN_VIEWED + " TEXT, " +
                    COLUMN_COLOR + " INTEGER)";

    private static final String SQL_DROP_TABLE_NOTES =
            "DROP TABLE IF EXISTS " + TABLE_NOTES;

    private static final String SQL_CLEAR_NOTES = "DELETE FROM " + TABLE_NOTES;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_NOTES);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    private void addNote(@NonNull SQLiteDatabase db,
                         @NonNull String title,
                         @NonNull String description,
                         int color,
                         @NonNull String created) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_COLOR, color);
        values.put(COLUMN_CREATED, created);
        values.put(COLUMN_EDITED, created);
        values.put(COLUMN_VIEWED, created);

        db.insert(TABLE_NOTES, null, values);
    }

    private void addNote(@NonNull SQLiteDatabase db, ListNote listNote) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_DESCRIPTION, listNote.getDescription());
        values.put(COLUMN_TITLE, listNote.getCaption());
        values.put(COLUMN_COLOR, listNote.getColor());
        values.put(COLUMN_CREATED, listNote.getCreationDateString());
        values.put(COLUMN_EDITED, listNote.getEditingDateString());
        values.put(COLUMN_VIEWED, listNote.getViewingDateString());

        db.insert(TABLE_NOTES, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == VERSION_X) {}
        db.execSQL(SQL_DROP_TABLE_NOTES);
        onCreate(db);
    }

    @NonNull
    public List<ListNote> getAllNodes() throws ParseException {
        return getAllSortedAndSelected(
                SortOption.CREATING_DATE,
                SortMode.ASCENDING,
                FiltrationMode.NONE,
                null,
                null
        );
    }


    @NonNull
    public List<ListNote> getAllSorted(SortOption sortOption, SortMode sortMode) throws ParseException {
        return getAllSortedAndSelected(
                sortOption,
                sortMode,
                FiltrationMode.NONE,
                null,
                null
        );
    }


    public List<ListNote> getAllSelected(
            FiltrationMode filtrationMode,
            SortOption sortOption,
            String[] filtrationArgs) throws ParseException {
        return getAllSortedAndSelected(sortOption, SortMode.ASCENDING, filtrationMode, sortOption, filtrationArgs);
    }


    @NonNull
    public List<ListNote> getAllSortedAndSelected(
            SortOption sortOption,
            SortMode sortMode,
            FiltrationMode filtrationMode,
            SortOption filterBy,
            String[] filtrationArgs) throws ParseException {
        SQLiteDatabase database = getReadableDatabase();
        List<ListNote> allNotes = new ArrayList<>();
        String sortOrder = sortOptions.get(sortOption) + " " + sortModes.get(sortMode);

        String selection = null;
        String[] selectionArgs = null;

        if (filtrationMode != FiltrationMode.NONE) {
            if (filtrationMode == FiltrationMode.DATE_RANGE) {
                selection = sortOptions.get(filterBy) + " >= ? AND " + sortOptions.get(filterBy) + " <= ?";
                selectionArgs = filtrationArgs;
            }
            if (filtrationMode == FiltrationMode.DATE) {
                //selection = sortOptions.get(filterBy) + " LIKE " + "'" + filtrationArgs[0].substring(0, 10) + "%'";
                selection = sortOptions.get(filterBy) + " LIKE ?";
                selectionArgs = new String[]{ filtrationArgs[0].substring(0, 10) + "%" };
            }
        }

        Cursor cursor = null;
        try {
            cursor = database.query(
                    TABLE_NOTES,
                    columns,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );

            if (cursor.moveToFirst()) {
                do {

                    ListNote listNote = new ListNote(
                            cursor.getInt(2),
                            cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(3),
                            cursor.getString(5),
                            cursor.getString(4)
                            );
                    allNotes.add(listNote);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            database.close();
        }
        return allNotes;
    }

    public int updateNote(@NonNull ListNote listNote) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_DESCRIPTION, listNote.getDescription());
        values.put(COLUMN_VIEWED, listNote.getViewingDateString());
        values.put(COLUMN_EDITED, listNote.getEditingDateString());
        values.put(COLUMN_COLOR, listNote.getColor());

        String selection = COLUMN_TITLE + "=?";
        String[] selectionArgs = { listNote.getCaption() };

        try {
            int affected = database.update(TABLE_NOTES, values, selection, selectionArgs);
            return affected;
        } finally {
            database.close();
        }
    }

    public int deleteNote(@NonNull ListNote listNote) {
        SQLiteDatabase database = getWritableDatabase();
        String selection = COLUMN_TITLE + "=?";
        String[] selectionsArgs = { listNote.getCaption() };

        try {
            return database.delete(TABLE_NOTES, selection, selectionsArgs);
        } finally {
            database.close();
        }
    }

    public void deleteNotes(boolean useSQL) {
        SQLiteDatabase database = getWritableDatabase();
        try {
            if (useSQL) {
                database.execSQL(SQL_CLEAR_NOTES);
            } else {
                database.delete(TABLE_NOTES, "1", null);
            }
        } finally {
            database.close();
        }
    }

    public void addNotes(@NonNull List<ListNote> notes) {
        SQLiteDatabase database = getWritableDatabase();
        try {
            database.beginTransaction();
            for (ListNote note : notes) {
                addNote(database, note);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
            database.close();
        }
    }

    public void addNote(@NonNull ListNote note) {
        SQLiteDatabase database = getWritableDatabase();
        try {
            database.beginTransaction();
            addNote(database, note);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
            database.close();
        }
    }
}
