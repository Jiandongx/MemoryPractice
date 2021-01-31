package com.practice.jiandongxiao.memorypractice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by jiandongxiao on 6/7/15.
 */
public class CustomDBHandler extends SQLiteOpenHelper {

    private static final int DB_VERSION = 2;
    private static final String DB_NAME = "number_link.db";
    public static final String NUMBER_LINK_TABLE = "number_link";
    public static final String NUMBER_LINK_ID = "_id";
    public static final String NUMBER_LINK_NUMBER = "_number";

    public static final String HISTORY_RECORD_TABLE = "past_records";

    public static final String HISTORY_RECORD_ID = "_id";
    public static final String HISTORY_RECORD_DATE = "_date";
    public static final String HISTORY_RECORD_RECORD = "_record";

    public static String[] number_link_backup = new String[100];
    public static String[][] past_record_backup;

    public CustomDBHandler(Context context) {
        // Problem:
        // Database is created every time this is triggered.
        // Root Cause:
        // Database Name was null all the time, hence it will keep creating new database
        // Solution:
        // Fix the DB_NAME and DB_VERSION so that it will not keep creating new database
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("onCreate SQLITE", "Debug 1");

        String create_number_link_table = "CREATE TABLE " + NUMBER_LINK_TABLE + "("
                + NUMBER_LINK_ID + " INTEGER PRIMARY KEY, "
                + NUMBER_LINK_NUMBER + " TEXT)";
        try {
            db.execSQL(create_number_link_table);
        } catch (SQLException e) {
            Log.e("Create New Table", "SQL Error");
        }

        // Initialize database table with items
        ContentValues value = new ContentValues();
        for (int i=0; i<100;i++){
            value.put(NUMBER_LINK_NUMBER, MainActivity.sample_cn[i]);
            value.put(NUMBER_LINK_ID, i);
            db.insert(NUMBER_LINK_TABLE, null, value);
        }

        String create_past_record_table = "CREATE TABLE "+ HISTORY_RECORD_TABLE +"("
                + HISTORY_RECORD_ID +" INTEGER PRIMARY KEY, "
                + HISTORY_RECORD_DATE + " TEXT, "
                + HISTORY_RECORD_RECORD +" TEXT)";
        db.execSQL(create_past_record_table);

        Log.d("DB created", "Yes");
        // To close or not to close?
        // Do not need to close unless db is created from getWritableDatabase
        // db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("OnUpgrade", "Test");
        if (isTableExists(NUMBER_LINK_TABLE, db)){
            number_link_backup = BackUpNumberDB(db);
        }

        if (isTableExists(HISTORY_RECORD_TABLE,db)){
            past_record_backup = BackUpRecordDB(db);
        }

        db.execSQL("DROP TABLE IF EXISTS " + NUMBER_LINK_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + HISTORY_RECORD_TABLE);

        onRecreate(db);
    }

    public boolean isTableExists(String tableName, SQLiteDatabase db) {
        try {
            Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.close();
                    return true;
                }
                cursor.close();
            }
        } catch (SQLException e) {
            Log.e("Error", "SQL ERROR");
        }
        return false;
    }

    public void onRecreate(SQLiteDatabase db) {
        Log.d("onRecreate", "Test");
        String query = "CREATE TABLE "+ NUMBER_LINK_TABLE +"("
                + NUMBER_LINK_ID +" INTEGER PRIMARY KEY, "
                + NUMBER_LINK_NUMBER +" TEXT)";
        db.execSQL(query);

        // Initialize database table with items
        // For the DB version = 2, will re-initialize the value to fit the image function
        ContentValues value = new ContentValues();
        for (int i=0; i<100;i++){
            value.put(NUMBER_LINK_NUMBER, MainActivity.sample_cn[i]);
            value.put(NUMBER_LINK_ID, i);
            db.insert(NUMBER_LINK_TABLE, null, value);
        }

        String query2 = "CREATE TABLE "+ HISTORY_RECORD_TABLE +"("
                + HISTORY_RECORD_ID +" INTEGER PRIMARY KEY, "
                + HISTORY_RECORD_DATE + " TEXT, "
                + HISTORY_RECORD_RECORD +" TEXT)";
        db.execSQL(query2);

        // Initialize database table with items
        if (past_record_backup != null) {
            ContentValues value2 = new ContentValues();
            for (int i = 0; i < past_record_backup.length; i++) {
                value2.put(HISTORY_RECORD_DATE, past_record_backup[i][0]);
                value2.put(HISTORY_RECORD_RECORD, past_record_backup[i][1]);
                value2.put(HISTORY_RECORD_ID, i);
                db.insert(HISTORY_RECORD_TABLE, null, value2);
            }
        }

        Log.d("DB recreated", "Yes");
        // To close or not to close?
        // Do not need to close unless db is created from getWritableDatabase
        // db.close();
    }

    public String[] DatabaseToString(){
        Log.d("DatabaseToString", "Test");
        // Return database records in string arrays
        String[] items = new String[100];
        // items[0] = "0-11及22以形状相似的东西为引子，其余的皆以谐音为引子，方便记忆。\n你可点击更改联想列表。";

        int counter = 0;

        String query = "Select * from " + NUMBER_LINK_TABLE;
        SQLiteDatabase db = getWritableDatabase();

        Cursor c = db.rawQuery(query,null);
        c.moveToFirst();

        while(!c.isAfterLast()){
            if (c.getString(c.getColumnIndex(NUMBER_LINK_ID))!=null){
                items[counter] = c.getString(c.getColumnIndex("_number"));
                counter++;
            }
            c.moveToNext();
        }

        c.close();
        db.close();
        return items;
    }

    public String[] BackUpNumberDB(SQLiteDatabase db){
        Log.d("BackUpNumberDB", "Test");
        // Return database records in string arrays
        String[] items = new String[100];

        int counter = 0;

        String query = "Select * from " + NUMBER_LINK_TABLE;

        Cursor c = db.rawQuery(query,null);
        c.moveToFirst();

        while(!c.isAfterLast()){
            if (c.getString(c.getColumnIndex(NUMBER_LINK_ID))!=null){
                items[counter] = c.getString(c.getColumnIndex("_number"));
                counter++;
            }
            c.moveToNext();
        }

        c.close();
        return items;
    }

    public String[][] RecordsToString(){
        Log.d("recordstostring","yes");
        SQLiteDatabase db = getWritableDatabase();
        if (!isTableExists(HISTORY_RECORD_TABLE,db)){
            String query = "CREATE TABLE "+ HISTORY_RECORD_TABLE +"("
                    + HISTORY_RECORD_ID +" INTEGER PRIMARY KEY, "
                    + HISTORY_RECORD_DATE +" TEXT, "
                    + HISTORY_RECORD_RECORD +" TEXT)";
            db.execSQL(query);
        }

        int counter = 0;

        String query = "Select * from " + HISTORY_RECORD_TABLE;

        Cursor c = db.rawQuery(query,null);
        c.moveToFirst();
        String[][] temp = new String[c.getCount()][2];
        // temp[0][0] = "你可保存以前的联想过的数字以便练习长时间记忆。";
        if (c.getCount() == 0){
            return temp;
        }

        while(!c.isAfterLast()){
            if (c.getString(c.getColumnIndex(HISTORY_RECORD_ID))!=null){
                // Concatenate the strings before returning to Record.class
                temp[counter][0] = c.getString(c.getColumnIndex("_date"));
                temp[counter][1] = c.getString(c.getColumnIndex("_record"));
                counter++;
            }
            c.moveToNext();
        }

        c.close();
        db.close();
        return temp;
    }

    public String[][] BackUpRecordDB(SQLiteDatabase db){
        Log.d("BackUpRecordDB", "Test");
        int counter = 0;

        String query = "Select * from " + HISTORY_RECORD_TABLE;

        Cursor c = db.rawQuery(query,null);
        c.moveToFirst();

        String[][] temp = new String[c.getCount()][2];

        if (c.getCount() == 0){
            return temp;
        }

        while(!c.isAfterLast()){
            if (c.getString(c.getColumnIndex(HISTORY_RECORD_ID))!=null){
                temp[counter][0] = c.getString(c.getColumnIndex("_date"));
                temp[counter][1] = c.getString(c.getColumnIndex("_record"));
                counter++;
            }
            c.moveToNext();
        }

        c.close();
        return temp;
    }
}
