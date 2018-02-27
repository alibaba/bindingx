package com.alibaba.android.qrcode.extension.core;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DBOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "QRCode_Scan";
    private static final int DATABASE_VERSION = 2;

    static SimpleDateFormat sDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private Context mContext;
    private SQLiteDatabase mDb;


    static final String TABLE_SCAN = "scan_history";
    static final String COLUMN_ITEM = "item";
    static final String COLUMN_TIMESTAMP = "timestamp";

    private static final int SLEEP_TIME_MS = 30;

    private static final String STATEMENT_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_SCAN + " ("
            + COLUMN_ITEM
            + " TEXT PRIMARY KEY,"
            + COLUMN_TIMESTAMP
            + " TEXT NOT NULL"
            + ")";


    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    /**
     * retrieve sqlite database
     *
     * @return a {@link SQLiteDatabase} instance or null if retrieve fails.
     * */
    public @Nullable
    SQLiteDatabase getDatabase() {
        ensureDatabase();
        return mDb;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(STATEMENT_CREATE_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            deleteDB();
            onCreate(db);
        }
    }



    synchronized void ensureDatabase() {
        if (mDb != null && mDb.isOpen()) {
            return;
        }
        // Sometimes retrieving the database fails. We do 2 retries: first without database deletion
        // and then with deletion.
        for (int tries = 0; tries < 2; tries++) {
            try {
                if (tries > 0) {
                    //delete db and recreate
                    deleteDB();
                }
                mDb = getWritableDatabase();
                break;
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
            // Wait before retrying.
            try {
                Thread.sleep(SLEEP_TIME_MS);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
        if(mDb == null){
            return;
        }

        createTableIfNotExists(mDb);

    }

    private boolean deleteDB() {
        closeDatabase();
        return mContext.deleteDatabase(DATABASE_NAME);
    }

    public void closeDatabase() {
        if (mDb != null && mDb.isOpen()) {
            mDb.close();
            mDb = null;
        }
    }

    private void createTableIfNotExists(@NonNull SQLiteDatabase db) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT DISTINCT tbl_name FROM sqlite_master WHERE tbl_name = '"+ TABLE_SCAN +"'", null);
            if(cursor != null && cursor.getCount() > 0) {
                return;
            }
            db.execSQL(STATEMENT_CREATE_TABLE);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cursor != null){
                cursor.close();
            }
        }
    }


}
