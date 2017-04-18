package com.crystal.base;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.crystal.sqlite.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by owais.ali on 5/4/2016.
 */
public abstract class SQLiteDB<T extends SQLiteDB, M extends BaseModel> extends SQLiteAssetHelper {

    //////////////////////////////////////////////
    // PUBLIC STATIC CONSTANTS
    //////////////////////////////////////////////

    public static final short NO_ID = -1;

    //////////////////////////////////////////////
    // DATABASE
    //////////////////////////////////////////////

    private static final int DATABASE_VERSION = 1;

    //////////////////////////////////////////////
    // DATABASE VERSION
    //////////////////////////////////////////////

    //private static final String DATABASE_NAME = "";

    //////////////////////////////////////////////
    // PRIVATE VAR
    //////////////////////////////////////////////

    private List<M> outData;
    private M model;
    private SQLiteDatabase sqLiteDatabase;

    //////////////////////////////////////////////
    // CONSTRUCTOR
    //////////////////////////////////////////////

    public SQLiteDB(Context ctx, final String databaseName){
        super(ctx, databaseName, null, DATABASE_VERSION);
    }

    //////////////////////////////////////////////
    // PUBLIC METHODS
    //////////////////////////////////////////////

    //////////////////////////////////////////////
    // SUPER CLASS METHOD
    //////////////////////////////////////////////

    public void save(){

        SQLiteDatabase db = getReadableDatabase();
        db.insertOrThrow(getTableName(), null, getData(new ContentValues()));
        db.close();
    }

    private Cursor execQuery(int id, String customQuery){

        // select all query
        String selectQuery = "SELECT * FROM " + getTableName() + " ORDER BY " + getPrimaryColumn() + " ASC";

        if(id != NO_ID){
            selectQuery = "SELECT * FROM " + getTableName() + " WHERE " + getPrimaryColumn() + "='" + id + "' ORDER BY " + getPrimaryColumn() + " ASC";
        }else if(customQuery != null){
            selectQuery = customQuery;
        }

        sqLiteDatabase = this.getReadableDatabase();
        return sqLiteDatabase.rawQuery(selectQuery, null);
    }

    public void update(){
        final SQLiteDatabase db = this.getWritableDatabase();
        String[] whereArgs = new String[]{String.valueOf(getId())};
        db.update(getTableName(), getData(new ContentValues()), getPrimaryColumn() + " = ?", whereArgs);
        db.close();
    }

    public void execSQL(final String query){
        final SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        db.close();
    }

    public void delete(){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] whereArgs = new String[]{String.valueOf(getId())};
        db.delete(getTableName(), getPrimaryColumn() + "=?", whereArgs);
        db.close();
    }

    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(getTableName(), null, null);
        db.close();
    }

    public List<M> getRecords(final int id, final String customQuery){
        final Cursor cursor = execQuery(id, customQuery);
        if(outData == null) outData = new ArrayList<>();

        // clear any previous records
        outData.clear();

        if(cursor.moveToFirst()){
            do{
                outData.add(getItem(cursor));
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        sqLiteDatabase.close();

        return outData;
    }

    public List<M> getRecords(final int id){
        return getRecords(id, null);
    }

    public List<M> getRecords(final String customQuery){
        return getRecords(NO_ID, customQuery);
    }

    public List<M> getAllRecords(){
        return getRecords(NO_ID, null);
    }

    public M getRecord(final int id){
        final List<M> records = getRecords(id);
        if (records.size() > 0) {
            return records.get(0);
        }
        return null;
    }

    public M getRecord(final String customQuery){
        final List<M> records = getRecords(customQuery);
        if(records.size() > 0) {
            return records.get(0);
        }
        return null;
    }

    public int getCount(){
        return getCount("SELECT COUNT(*) AS count FROM " + getTableName());
    }

    public int getCount(final String column, final Object value){
        return getCount("SELECT COUNT(*) AS count FROM " + getTableName() + " WHERE " + column  + "=" + value.toString());
    }

    public int getCount(final String query){

        int count = 0;

        sqLiteDatabase = this.getReadableDatabase();
        final Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if(cursor.moveToFirst()){
            count = getIntValue(cursor, "count");
        }
        cursor.close();
        sqLiteDatabase.close();

        return count;
    }

    public String getStringValue(final Cursor cursor, final String columnName){
        final String val = cursor.getString(cursor.getColumnIndex(columnName));
        return (val == null) ? "" : val;
    }

    public int getIntValue(final Cursor cursor, final String columnName){
        return cursor.getInt(cursor.getColumnIndex(columnName));
    }

    public double getDoubleValue(final Cursor cursor, final String columnName){
        return cursor.getDouble(cursor.getColumnIndex(columnName));
    }

    public T setDataList(final List<M> outData){
        this.outData = outData;
        return getThis();
    }

    public T setModel(final M model){
        this.model = model;
        return getThis();
    }

    public M getModel(){
        return this.model;
    }

    //////////////////////////////////////////////
    // ABSTRACT METHOD BODY
    //////////////////////////////////////////////

    public abstract M getItem(Cursor cursor);
    public abstract String getTableName();
    public abstract int getId();
    public abstract String getPrimaryColumn();
    public abstract ContentValues getData(final ContentValues params);
    public abstract T getThis();
}
