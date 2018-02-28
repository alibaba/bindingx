package com.alibaba.android.qrcode.extension.core;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoryManager {

    private DBOpenHelper mDatabaseSupplier;

    public HistoryManager(Context context) {
        this.mDatabaseSupplier = new DBOpenHelper(context);
    }



    ///////////////////

    public boolean setItem(String item) {
        SQLiteDatabase database = mDatabaseSupplier.getDatabase();
        if (database == null) {
            return false;
        }

        String sql = "INSERT OR REPLACE INTO " + DBOpenHelper.TABLE_SCAN + " VALUES (?,?);";
        SQLiteStatement statement = null;
        String timeStamp = DBOpenHelper.sDateFormatter.format(new Date());
        try {
            statement = database.compileStatement(sql);
            statement.clearBindings();
            statement.bindString(1, item);
            statement.bindString(2, timeStamp);
            statement.execute();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if(statement != null) {
                statement.close();
            }
        }
    }

    public boolean removeItem(String item) {
        SQLiteDatabase database = mDatabaseSupplier.getDatabase();
        if (database == null) {
            return false;
        }

        int count = 0;
        try {
            count = database.delete(DBOpenHelper.TABLE_SCAN,
                    DBOpenHelper.COLUMN_ITEM + "=?",
                    new String[]{item});
        } catch (Exception e) {
            return false;
        }
        return count == 1;
    }

    public long getLength() {
        SQLiteDatabase database = mDatabaseSupplier.getDatabase();
        if (database == null) {
            return 0;
        }

        String sql = "SELECT count(" + DBOpenHelper.COLUMN_ITEM + ") FROM " + DBOpenHelper.TABLE_SCAN;
        SQLiteStatement statement = null;
        try {
            statement = database.compileStatement(sql);
            return statement.simpleQueryForLong();
        } catch (Exception e) {
            return 0;
        } finally {
            if(statement != null) {
                statement.close();
            }
        }
    }


    public List<HistoryItem> getAllItems() {
        SQLiteDatabase database = mDatabaseSupplier.getDatabase();
        if (database == null) {
            return null;
        }

        List<HistoryItem> result = new ArrayList<>();
        Cursor c = database.query(DBOpenHelper.TABLE_SCAN, new String[]{DBOpenHelper.COLUMN_ITEM, DBOpenHelper.COLUMN_TIMESTAMP}, null, null, null, null, null);
        try {
            while (c.moveToNext()) {
                HistoryItem historyItem = new HistoryItem();
                historyItem.item =  c.getString(c.getColumnIndex(DBOpenHelper.COLUMN_ITEM));
                historyItem.timeStamp = c.getString(c.getColumnIndex(DBOpenHelper.COLUMN_TIMESTAMP));
                result.add(historyItem);
            }
            return result;
        } catch (Exception e) {
            return result;
        } finally {
            c.close();
        }
    }


    public static class HistoryItem {
        public String item;
        public String timeStamp;
    }

}
