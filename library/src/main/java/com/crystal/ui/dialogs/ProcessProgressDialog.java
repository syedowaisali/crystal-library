package com.crystal.ui.dialogs;

import android.content.Context;

import com.crystal.R;
import com.crystal.base.BaseDialog;


/**
 * Created by owais.ali on 5/4/2016.
 */
public class ProcessProgressDialog extends BaseDialog<ProcessProgressDialog> {

    //////////////////////////////////////////////
    // PRIVATE STATIC INSTANCE
    //////////////////////////////////////////////

    private static ProcessProgressDialog INSTANCE;

    //////////////////////////////////////////////
    // PRIVATE VAR
    //////////////////////////////////////////////

    //////////////////////////////////////////////
    // FACTORY METHOD
    //////////////////////////////////////////////

    public static ProcessProgressDialog getInstance(Context context){
        return new ProcessProgressDialog(context);
    }

    public static ProcessProgressDialog getInstance(Context context, int style){
        return new ProcessProgressDialog(context, style);
    }

    //////////////////////////////////////////////
    // CONSTRUCTOR
    //////////////////////////////////////////////

    public ProcessProgressDialog(Context context) {
        super(context);
    }

    public ProcessProgressDialog(Context context, int style) {
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
    public ProcessProgressDialog getThis() {
        return this;
    }
}
