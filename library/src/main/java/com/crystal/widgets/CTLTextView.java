package com.crystal.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.crystal.R;
import com.crystal.utilities.Api;

/**
 * Created by owais.ali on 5/4/2016.
 */
public class CTLTextView extends TextView {

    //////////////////////////////////////////
    // PRIVATE VAR
    //////////////////////////////////////////

    private String typeface;

    //////////////////////////////////////////
    // CONSTRUCTOR
    //////////////////////////////////////////

    public CTLTextView(Context context) {
        this(context, null);
    }

    public CTLTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CTLTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CTLTextView);
        try{
            typeface = array.getString(R.styleable.CTLTextView_ctl_tv_typeface);
        }
        finally {
            array.recycle();
        }

        init();
    }

    //////////////////////////////////////////
    // INITIALIZING
    //////////////////////////////////////////

    private void init(){
        setFontface(typeface);
    }

    //////////////////////////////////////////
    // PUBLIC METHODS
    //////////////////////////////////////////

    public void setFontface(String typeface){
        if(typeface != null){
            try {
                setTypeface(Typeface.createFromAsset(getResources().getAssets(), typeface));
            }
            catch (Exception ex){
                Log.e(Api.TAG, ex.getMessage());
            }
        }
    }
}
