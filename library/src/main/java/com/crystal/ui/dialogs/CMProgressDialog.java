package com.crystal.ui.dialogs;

import android.content.Context;

import com.crystal.R;
import com.crystal.base.BaseDialog;


/**
 * Created by owais.ali on 5/4/2016.
 */
public class CMProgressDialog extends BaseDialog<CMProgressDialog> {

    //////////////////////////////////////////////
    // PRIVATE STATIC INSTANCE
    //////////////////////////////////////////////

    private static CMProgressDialog INSTANCE;

    //////////////////////////////////////////////
    // PRIVATE VAR
    //////////////////////////////////////////////

    //////////////////////////////////////////////
    // FACTORY METHOD
    //////////////////////////////////////////////

    public static CMProgressDialog getInstance(Context context){
        return new CMProgressDialog(context);
    }

    public static CMProgressDialog getInstance(Context context, int style){
        return new CMProgressDialog(context, style);
    }

    //////////////////////////////////////////////
    // CONSTRUCTOR
    //////////////////////////////////////////////

    public CMProgressDialog(Context context) {
        super(context);
    }

    public CMProgressDialog(Context context, int style) {
        super(context, style);
    }

    //////////////////////////////////////////////
    // OVERRIDE FUNCTIONS
    //////////////////////////////////////////////

    @Override
    public int getLayout() {
        return R.layout.progress_dialog;
    }

    @Override
    public CMProgressDialog getThis() {
        return this;
    }
}
