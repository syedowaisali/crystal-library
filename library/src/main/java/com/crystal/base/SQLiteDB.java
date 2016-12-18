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

    public synchronized void save(){

        SQLiteDatabase db = getReadableDatabase();
        db.insertOrThrow(getTableName(), null, getData());
        db.close();
    }

    private synchronized Cursor execQuery(int id, String customQuery){

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

    public synchronized void update(){
        final SQLiteDatabase db = this.getWritableDatabase();
        String[] whereArgs = new String[]{String.valueOf(getId())};
        db.update(getTableName(), getData(), getPrimaryColumn() + " = ?", whereArgs);
        db.close();
    }

    public synchronized void execSQL(final String query){
        final SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        db.close();
    }

    public synchronized void delete(){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] whereArgs = new String[]{String.valueOf(getId())};
        db.delete(getTableName(), getPrimaryColumn() + "=?", whereArgs);
        db.close();
    }

    public synchronized void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(getTableName(), null, null);
        db.close();
    }

    public synchronized final List<M> getRecords(final int id, final String customQuery){
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

    public synchronized final List<M> getRecordsById(final int id){
        return getRecords(id, null);
    }

    public synchronized final List<M> getRecordsByCustomQuery(final String customQuery){
        return getRecords(NO_ID, customQuery);
    }

    public synchronized final List<M> getRecords(){
        return getRecords(NO_ID, null);
    }

    public synchronized final String getStringValue(final Cursor cursor, final String columnName){
        final String val = cursor.getString(cursor.getColumnIndex(columnName));
        return (val == null) ? "" : val;
    }

    public synchronized final int getIntegerValue(final Cursor cursor, final String columnName){
        return cursor.getInt(cursor.getColumnIndex(columnName));
    }

    public synchronized final double getDoubleValue(final Cursor cursor, final String columnName){
        return cursor.getDouble(cursor.getColumnIndex(columnName));
    }

    public synchronized final T setDataList(final List<M> outData){
        this.outData = outData;
        return getThis();
    }

    //////////////////////////////////////////////
    // ABSTRACT METHOD BODY
    //////////////////////////////////////////////


    public abstract M getItem(Cursor cursor);
    public abstract String getTableName();
    public abstract int getId();
    public abstract String getPrimaryColumn();
    public abstract ContentValues getData();
    public abstract T getThis();
}
