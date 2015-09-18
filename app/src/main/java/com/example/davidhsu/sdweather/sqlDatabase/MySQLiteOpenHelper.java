package com.example.davidhsu.sdweather.sqlDatabase;

/**
 * Created by David Hsu on 2015/9/15.
 */
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "TravelSpots";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "Spot";
    private static final String COL_id = "id";
    private static final String COL_clothes = "clothes";
    private static final String COL_type = "type";
    private static final String COL_sleeve = "sleeve";
    private static final String COL_material = "material";
    private static final String COL_image = "image";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " ( " +
                    COL_id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_clothes + " TEXT NOT NULL, " +
                    COL_type + " TEXT, " +
                    COL_sleeve + " TEXT, " +
                    COL_material + " TEXT, " +
                    COL_image + " BLOB ); ";

    public MySQLiteOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    public List<Spot> getAllSpots() {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {
                COL_id, COL_clothes, COL_type, COL_sleeve, COL_material, COL_image
        };
        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null,
                null);
        List<Spot> spotList = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String clothes = cursor.getString(1);
            String type = cursor.getString(2);
            String sleeve = cursor.getString(3);
            String material = cursor.getString(4);
            byte[] image = cursor.getBlob(5);
            Spot spot = new Spot(id, clothes, type, sleeve, material, image);
            spotList.add(spot);
        }
        cursor.close();
        return spotList;
    }

    public List<Spot> getShortSpots() {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {
                COL_id, COL_clothes, COL_type, COL_sleeve, COL_material, COL_image
        };
        String selection = COL_sleeve + " = ?;";
        String[] selectionArgs = {String.valueOf("Short")};
        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null,
                null);
        List<Spot> spotList1 = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String clothes = cursor.getString(1);
            String type = cursor.getString(2);
            String sleeve = cursor.getString(3);
            String material = cursor.getString(4);
            byte[] image = cursor.getBlob(5);
            Spot spot = new Spot(id, clothes, type, sleeve, material, image);
            spotList1.add(spot);
        }
        cursor.close();
        return spotList1;
    }

    public List<Spot> getLongSpots() {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {
                COL_id, COL_clothes, COL_type, COL_sleeve, COL_material, COL_image
        };
        String selection = COL_sleeve + " = ?;";
        String[] selectionArgs = {String.valueOf("Long")};
        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null,
                null);
        List<Spot> spotList2 = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String clothes = cursor.getString(1);
            String type = cursor.getString(2);
            String sleeve = cursor.getString(3);
            String material = cursor.getString(4);
            byte[] image = cursor.getBlob(5);
            Spot spot = new Spot(id, clothes, type, sleeve, material, image);
            spotList2.add(spot);
        }
        cursor.close();
        return spotList2;
    }

    public Spot findById(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String[] columns = {
                COL_clothes, COL_type, COL_sleeve, COL_material, COL_image
        };
        String selection = COL_id + " = ?;";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs,
                null, null, null);
        Spot spot = null;
        if (cursor.moveToNext()) {
            String clothes = cursor.getString(0);
            String type = cursor.getString(1);
            String sleeve = cursor.getString(2);
            String material = cursor.getString(3);
            byte[] image = cursor.getBlob(4);
            spot = new Spot(id, clothes, type, sleeve, material, image);
        }
        cursor.close();
        return spot;
    }

    public long insert(Spot spot) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_clothes, spot.getClothes());
        values.put(COL_type, spot.getType());
        values.put(COL_sleeve, spot.getSleeve());
        values.put(COL_material, spot.getMaterial());
        values.put(COL_image, spot.getImage());
        return db.insert(TABLE_NAME, null, values);
    }

    public int update(Spot spot) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_clothes, spot.getClothes());
        values.put(COL_type, spot.getType());
        values.put(COL_sleeve, spot.getSleeve());
        values.put(COL_material, spot.getMaterial());
        values.put(COL_image, spot.getImage());
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {Integer.toString(spot.getId())};
        return db.update(TABLE_NAME, values, whereClause, whereArgs);
    }

    public int deleteById(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {String.valueOf(id)};
        return db.delete(TABLE_NAME, whereClause, whereArgs);
    }
}
