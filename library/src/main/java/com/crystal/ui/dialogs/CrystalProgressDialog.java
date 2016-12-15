package com.crystal.ui.dialogs;

import android.content.Context;

import com.crystal.R;
import com.crystal.base.BaseDialog;


/**
 * Created by owais.ali on 5/4/2016.
 */
public class CrystalProgressDialog extends BaseDialog<CrystalProgressDialog> {

    //////////////////////////////////////////////
    // PRIVATE STATIC INSTANCE
    //////////////////////////////////////////////

    private static CrystalProgressDialog INSTANCE;

    //////////////////////////////////////////////
    // PRIVATE VAR
    //////////////////////////////////////////////

    //////////////////////////////////////////////
    // FACTORY METHOD
    //////////////////////////////////////////////

    public static CrystalProgressDialog getInstance(Context context){
        return new CrystalProgressDialog(context);
    }

    public static CrystalProgressDialog getInstance(Context context, int style){
        return new CrystalProgressDialog(context, style);
    }

    //////////////////////////////////////////////
    // CONSTRUCTOR
    //////////////////////////////////////////////

    public CrystalProgressDialog(Context context) {
        super(context);
    }

    public CrystalProgressDialog(Context context, int style) {
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
    public CrystalProgressDialog getThis() {
        return this;
    }
}
