package com.example.mygolfbag;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyBagDatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "clubs.sqlite";
    public static final int DB_VERSION = 1;
    public static final String TABLE_ID = "_id";
    public static final String TABLE = "clubs";
    public static final String TYPE = "type";
    public static final String LOFT = "loft";
    public static final String BRAND = "brand";
    public static final String SHAFT = "shaft";
    public static final String FLEX = "flex";
    public static final String YARDS = "yards";
    public static final String DESC = "description";
    public static final String IMAGE = "image";
    public static final String OWNER = "owner";

    public static final String USERTABLE = "users";
    public static final String USER = "username";
    public static final String PASS = "password";
    public static final String NAME = "name";


    public MyBagDatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public int getCount(int id) {
        SQLiteDatabase sql = getReadableDatabase();
        Cursor c = sql.rawQuery("SELECT COUNT(*) FROM " + TABLE + " WHERE owner = " + id, null);
        c.moveToFirst();
        return c.getInt(0);
    }

    public int getCountUsers() {
        SQLiteDatabase sql = getReadableDatabase();
        Cursor c = sql.rawQuery("SELECT COUNT(*) FROM " + USERTABLE, null);
        c.moveToFirst();
        return c.getInt(0);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE clubs( "
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "type INTEGER, "
                + "loft FLOAT, "
                + "brand STRING, "
                + "shaft STRING, "
                + "flex INTEGER, "
                + "yards INTEGER, "
                + "description STRING, "
                + "image STRING, "
                + "owner INTEGER);";
        sqLiteDatabase.execSQL(query);
        String query2 = "CREATE TABLE users( "
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username STRING, "
                + "name STRING, "
                + "password STRING);";
        sqLiteDatabase.execSQL(query2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}