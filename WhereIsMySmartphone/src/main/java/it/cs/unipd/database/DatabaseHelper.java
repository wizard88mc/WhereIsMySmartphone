package it.cs.unipd.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Matteo on 04/06/2014.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_TABLE = "samples_accelerometer";
    private static final String DATABASE_TABLE_LINEAR = "samples_linear";
    private static final String DATABASE_NAME  = "whereismysmartphone.db";
    private static final int SCHEMA = 2;

    public static String getDatabaseTable() {
        return DATABASE_TABLE;
    }

    public static String getDatabaseTableLinear() {
        return DATABASE_TABLE_LINEAR;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS" + DATABASE_TABLE + " (\"timestamp\" REAL, " +
            "\"x\" REAL, \"y\" REAL, \"z\" REAL, \"rotationX\" REAL, \"rotationY\" REAL, " +
            "\"rotationZ\" REAL, \"proximity\" REAL, \"sex\" TEXT, \"age\" TEXT, \"height\" TEXT, " +
            "\"shoes\" TEXT, \"hand\" TEXT, \"action\" TEXT, \"origin\" TEXT, " +
            "\"destination\" TEXT, \"trunk\" INTEGER);");

        db.execSQL("CREATE TABLE IF NOT EXISTS" + DATABASE_TABLE_LINEAR + " (\"timestamp\" REAL, " +
                "\"x\" REAL, \"y\" REAL, \"z\" REAL, \"rotationX\" REAL, \"rotationY\" REAL, " +
                "\"rotationZ\" REAL, \"proximity\" REAL, \"sex\" TEXT, \"age\" TEXT, \"height\" TEXT, " +
                "\"shoes\" TEXT, \"hand\" TEXT, \"action\" TEXT, \"origin\" TEXT, " +
                "\"destination\" TEXT, \"trunk\" INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_LINEAR);
        onCreate(db);
    }
}
