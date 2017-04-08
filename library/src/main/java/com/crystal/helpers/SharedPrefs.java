package com.crystal.helpers;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by owais.ali on 9/6/2016.
 */
public final class SharedPrefs {

    ////////////////////////////////////
    // PRIVATE CONSTANTS
    ////////////////////////////////////

    ////////////////////////////////////
    // PRIVATE VAR
    ////////////////////////////////////

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    ////////////////////////////////////
    // SINGLETON
    ////////////////////////////////////

    ////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////

    public SharedPrefs(Context context){
        this(context, SharedPrefs.class.getSimpleName());
    }

    public SharedPrefs(Context context, String name){
        this(context, name, Context.MODE_PRIVATE);
    }

    public SharedPrefs(Context context, int mode){
        this(context, SharedPrefs.class.getSimpleName(), mode);
    }

    public SharedPrefs(Context context, String name, int mode){
        sharedPreferences = context.getSharedPreferences(name, mode);
        editor = sharedPreferences.edit();
    }

    ////////////////////////////////////
    // SETTER'S
    ////////////////////////////////////

    public <T> void save(String key, T val, Class<T> type){

        if(type == String.class){
            editor.putString(key, (String)val);
        }
        else if(type == Float.class){
            editor.putFloat(key, (Float)val);
        }
        else if(type == Integer.class){
            editor.putInt(key, (Integer) val);
        }
        else if(type == Long.class){
            editor.putLong(key, (Long)val);
        }
        else if(type == Boolean.class){
            editor.putBoolean(key, (Boolean)val);
        }

        editor.apply();
    }

    ////////////////////////////////////
    // GETTER'S
    ////////////////////////////////////
    public <T> T get(String key, Class<T> type){
        if(type == String.class){
            return type.cast(sharedPreferences.getString(key, ""));
        }
        else if(type == Float.class){
            return type.cast(sharedPreferences.getFloat(key, 0F));
        }
        else if(type == Integer.class){
            return type.cast(sharedPreferences.getInt(key, 0));
        }
        else if(type == Long.class){
            return type.cast(sharedPreferences.getLong(key, 0L));
        }
        else if(type == Boolean.class){
            return type.cast(sharedPreferences.getBoolean(key, false));
        }
        else{
            return type.cast(sharedPreferences.getString(key, ""));
        }
    }

    public <T> T get(String key, Class<T> type, T d){
        if(type == String.class){
            return type.cast(sharedPreferences.getString(key, (String)d));
        }
        else if(type == Float.class){
            return type.cast(sharedPreferences.getFloat(key, (Float)d));
        }
        else if(type == Integer.class){
            return type.cast(sharedPreferences.getInt(key, (Integer)d));
        }
        else if(type == Long.class){
            return type.cast(sharedPreferences.getLong(key, (Long)d));
        }
        else if(type == Boolean.class){
            return type.cast(sharedPreferences.getBoolean(key, (Boolean)d));
        }
        else{
            return type.cast(sharedPreferences.getString(key, (String)d));
        }
    }
}
