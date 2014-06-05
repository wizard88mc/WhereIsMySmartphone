package it.cs.unipd.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

/**
 * Created by Matteo on 04/06/2014.
 */
public class DBAdapter {

    private Context context;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;
    private int trunkAccelerometer = 1;
    private int trunkLinear = 1;

    private static String DATABASE_TABLE;
    private static String DATABASE_TABLE_LINEAR;

    private final String KEY_timestamp = "timestamp";
    private final String KEY_x = "x";
    private final String KEY_y = "y";
    private final String KEY_z = "z";
    private final String KEY_rotationX = "rotationX";
    private final String KEY_rotationY = "rotationY";
    private final String KEY_rotationZ = "rotationZ";
    private final String KEY_proximity = "proximity";
    private final String KEY_sex = "sex";
    private final String KEY_age = "age";
    private final String KEY_height = "height";
    private final String KEY_shoes = "shoes";
    private final String KEY_hand = "hand";
    private final String KEY_action = "action";
    private final String KEY_origin = "origin";
    private final String KEY_destination = "destination";
    private final String KEY_trunk = "trunk";

    public DBAdapter(Context context) {
        this.context = context;
    }

    public DBAdapter open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        DATABASE_TABLE = DatabaseHelper.getDatabaseTable();
        DATABASE_TABLE_LINEAR = DatabaseHelper.getDatabaseTableLinear();
        this.getNewTrunkIdAccelerometer();
        this.getNewTrunkIdLinear();
        return this;
    }

    public void close() {
        database.close();
    }

    public String getDBPath() {
        return database.getPath();
    }

    private ContentValues createContentValues(long timestamp, float x, float y, float z,
            float rotationX, float rotationY, float rotationZ, float proximity, String sex, String age,
            String height, String shoes, String hand, String action, String origin,
            String destination, int trunk) {

        ContentValues values = new ContentValues();
        values.put(KEY_timestamp, timestamp);
        values.put(KEY_x, x);
        values.put(KEY_y, y);
        values.put(KEY_z, z);
        values.put(KEY_rotationX, rotationX);
        values.put(KEY_rotationY, rotationY);
        values.put(KEY_rotationZ, rotationZ);
        values.put(KEY_proximity, proximity);
        values.put(KEY_sex, sex);
        values.put(KEY_age, age);
        values.put(KEY_height, height);
        values.put(KEY_shoes, shoes);
        values.put(KEY_hand, hand);
        values.put(KEY_action, action);
        values.put(KEY_origin, origin);
        values.put(KEY_destination, destination);

        return values;
    }

    public void getNewTrunkIdAccelerometer() {
        this.trunkAccelerometer=1;
        if (this.getCount(false)>0) {
            this.trunkAccelerometer=(int)database.compileStatement(
                    "SELECT MAX(trunk)+1 FROM " + DATABASE_TABLE).simpleQueryForLong();
        }
    }

    public void getNewTrunkIdLinear() {
        this.trunkLinear = 1;
        if (this.getCount(true)>0) {
            this.trunkLinear = (int)database.compileStatement(
                    "SELECT MAX(trunk)+1 FROM " + DATABASE_TABLE_LINEAR).simpleQueryForLong();
        }
    }

    public long getCount(boolean linear) {
        if (!linear) {
            return database.compileStatement("SELECT COUNT(*) FROM " + DATABASE_TABLE).simpleQueryForLong();
        }
        else {
            return database.compileStatement("SELECT COUNT(*) FROM " + DATABASE_TABLE_LINEAR).simpleQueryForLong();
        }
    }

    private int getLastTrunkId() {
        return (int)database.compileStatement("SELECT MAX(trunk) FROM " + DATABASE_TABLE)
                    .simpleQueryForLong();
    }

    private int getLastTrunkIdLinear() {
        return (int)database.compileStatement("SELECT MAX(trunk) FROM " + DATABASE_TABLE_LINEAR)
                    .simpleQueryForLong();
    }

    public void saveSampleAccelerometer(long timestamp, float x, float y, float z, float rotationX,
                float rotationY, float rotationZ, float proximity, String sex, String age, String height,
                String shoes, String hand, String action, String origin, String destination) {

        database.insertOrThrow(DATABASE_TABLE, null, createContentValues(timestamp, x, y, z, rotationX,
                rotationY, rotationZ, proximity, sex, age, height, shoes, hand, action, origin,
                destination, trunkAccelerometer));
    }

    public void saveSampleLinear(long timestamp, float x, float y, float z, float rotationX,
                float rotationY, float rotationZ, float proximity, String sex, String age, String height,
                String shoes, String hand, String action, String origin, String destination) {

        database.insertOrThrow(DATABASE_TABLE_LINEAR, null, createContentValues(timestamp, x, y, z,
                rotationX, rotationY, rotationZ, proximity, sex, age, height, shoes, hand, action, origin,
                destination, trunkLinear));
    }

    public void cleanDB() {
        database.execSQL("DELETE FROM " + DATABASE_TABLE);
        database.execSQL("DELETE FROM " + DATABASE_TABLE_LINEAR);
        database.execSQL("VACUUM");
    }
}
