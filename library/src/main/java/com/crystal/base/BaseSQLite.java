package com.crystal.base;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;


/**
 * Created by owais.ali on 5/4/2016.
 */
public abstract class BaseSQLite<T extends BaseSQLite<T>> extends SQLiteAssetHelper {

    //////////////////////////////////////////////
    // DATABASE
    //////////////////////////////////////////////

    private static final int DATABASE_VERSION = 2;

    //////////////////////////////////////////////
    // DATABASE VERSION
    //////////////////////////////////////////////

    private static final String DATABASE_NAME = "navytrivia.db";

    //////////////////////////////////////////////
    // PRIVATE VAR
    //////////////////////////////////////////////

    private boolean only_last_row = false;

    //////////////////////////////////////////////
    // CONSTRUCTOR
    //////////////////////////////////////////////

    public BaseSQLite(final Context ctx){
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //////////////////////////////////////////////
    // SUPER CLASS METHOD
    //////////////////////////////////////////////

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {

        // drop older table if exist
        if(oldVersion != newVersion){
            //db.execSQL("DROP TABLE IF EXISTS " + getTableName());
        }

        // create table again
        onCreate(db);
    }

    public void save(){

        final SQLiteDatabase db = this.getReadableDatabase();
        db.insert(getTableName(), null, getData());
        db.close();
    }

    public Cursor execQuery(final int id){

        // select all query
        String selectQuery = "SELECT * FROM " + getTableName() + " ORDER BY " + getPrimaryColumn() + " ASC";

        if(only_last_row && id > 0){
            selectQuery = "SELECT * FROM " + getTableName() + " WHERE " + getPrimaryColumn() + "='" + id + "' ORDER BY " + getSecondaryColumn() + " DESC LIMIT 1";
        }else if(only_last_row){
            selectQuery = "SELECT * FROM " + getTableName() + " ORDER BY " + getPrimaryColumn() + " DESC LIMIT 1";
        }else if(id > 0){
            selectQuery = "SELECT * FROM " + getTableName() + " WHERE " + getPrimaryColumn() + "='" + id + "' ORDER BY " + getPrimaryColumn() + " ASC";
        }

        final SQLiteDatabase db = this.getReadableDatabase();
        final Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public Cursor execQuery(){
        return execQuery(0);
    }

    public T onlyLastRow(final boolean flag){
        only_last_row = flag;
        return getThis();
    }

    public void update(){
        final SQLiteDatabase db = this.getWritableDatabase();
        final String[] whereArgs = new String[]{getId()};
        db.update(getTableName(), getData(), getPrimaryColumn() + " = ?", whereArgs);
    }

    public void delete(){
        final SQLiteDatabase db = this.getWritableDatabase();
        final String[] whereArgs = new String[]{getId()};
        db.delete(getTableName(), getPrimaryColumn() + "=?", whereArgs);
        db.close();
    }

    public void deleteAll(){
        final SQLiteDatabase db = this.getWritableDatabase();
        db.delete(getTableName(), null, null);
        db.close();
    }

    //////////////////////////////////////////////
    // ABSTRACT METHOD BODY
    //////////////////////////////////////////////

    public abstract ContentValues getData();
    public abstract String getTableName();
    public abstract String getId();
    public abstract String getPrimaryColumn();
    public abstract String getSecondaryColumn();
    public abstract T getThis();
}
