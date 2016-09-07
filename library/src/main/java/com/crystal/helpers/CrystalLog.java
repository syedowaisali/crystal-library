package com.crystal.helpers;

import android.util.Log;

import com.crystal.utilities.Api;

/**
 * Created by owais.ali on 9/7/2016.
 */
public class CrystalLog {

    public static final void d(Object obj){
        Log.d(Api.TAG, obj.toString());
    }

    public static final void w(Object obj){
        Log.w(Api.TAG, obj.toString());
    }

    public static final void e(Object obj){
        Log.e(Api.TAG, obj.toString());
    }
}
