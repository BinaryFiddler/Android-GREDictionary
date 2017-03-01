package com.ucsd.chenyu.dictionary;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by chenyu on 3/1/17.
 */

public class Database {
    SQLiteDatabase db;
    public static final String TABLE_COMMENT = "wordlist";
    public static final String COLUMN_WORD = "word";
    public static final String COLUMN_DEFINITION = "definition";
    public static final String COLUMN_ATTEMPT = "attempt";
    public static final String COLUMN_WRONG_ATTMPT = "wrong";

    private static final String DATABASE_CREATE = "create table IF NOT EXISTS "
            + TABLE_COMMENT + "( " + COLUMN_WORD
            + " varchar primary key, " + COLUMN_DEFINITION
            + " varchar, "+ COLUMN_ATTEMPT
            + " integer, " + COLUMN_WRONG_ATTMPT
            + " integer);";

    Map<String, String> pickWords(){
        Cursor cursor = db.rawQuery("SELECT * FROM wordlist ORDER BY RANDOM() LIMIT 5;", null);
        cursor.moveToFirst();
        Map<String, String> map = new HashMap<>();
        while (cursor.moveToNext()){
            String word = cursor.getString(cursor.getColumnIndex(COLUMN_WORD));
            String def = cursor.getString(cursor.getColumnIndex(COLUMN_DEFINITION));
            map.put(word, def);
        }
        return map;
    }

    void updateAttempts(String word, Boolean isRightAnswer){
        Cursor cursor = db.rawQuery("SELECT * FROM wordlist WHERE " + COLUMN_WORD + "=" + word + ";", null);
        int updatedAttempts = cursor.getInt(cursor.getColumnIndex(COLUMN_ATTEMPT)) + 1;
        int updatedWrongAttempts = isRightAnswer ? cursor.getInt(cursor.getColumnIndex(COLUMN_WRONG_ATTMPT)) : cursor.getInt(cursor.getColumnIndex(COLUMN_WRONG_ATTMPT)) + 1;
        cursor.close();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ATTEMPT, updatedAttempts);
        values.put(COLUMN_WRONG_ATTMPT, updatedWrongAttempts);
        db.update(TABLE_COMMENT, values, COLUMN_WORD +"="+word, null);
    }


    void openDB(Activity activity){
        db = activity.openOrCreateDatabase("Dictionary", MODE_PRIVATE, null);
    }

    void closeDB(){
        db.close();
    }

}
