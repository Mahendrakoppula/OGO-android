package com.customerogo.app.utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.customerogo.app.model.Address;

import java.util.ArrayList;
import java.util.List;


public class MyHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "mydatabase.db";
    public static final String TABLE_NAME = "address";
    public static final String ICOL_1 = "ID";
    public static final String ICOL_2 = "title";
    public static final String ICOL_3 = "address";
    public static final String ICOL_4 = "lats";
    public static final String ICOL_5 = "lons";

    Context contextA;


    public List<Address> getCData() {
        List<Address> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            Cursor c = db.rawQuery("select * from address", null);
            if (c.getCount() != -1) { //if the row exist then return the id
                while (c.moveToNext()) {
                    Address item = new Address();
                    item.setId(c.getString(0));
                    item.setTitle(c.getString(1));
                    item.setAddres(c.getString(2));
                    item.setLats(c.getDouble(3));
                    item.setLongs(c.getDouble(3));

                    list.add(item);
                }

            }
        } catch (Exception e) {
            Log.e("Error ", "-->" + e.toString());

        }
        return list;
    }


    public MyHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        contextA = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT , address TEXT , lats Double , lons Doubleō )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(Address rModel) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(ICOL_2, rModel.getTitle());
        contentValues.put(ICOL_3, rModel.getAddres());
        contentValues.put(ICOL_4, rModel.getLats());
        contentValues.put(ICOL_5, rModel.getLongs());

        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }

    }










}