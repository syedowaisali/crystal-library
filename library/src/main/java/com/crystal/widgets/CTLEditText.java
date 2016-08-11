package com.crystal.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

import com.crystal.R;
import com.crystal.utilities.Api;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by owais.ali on 6/30/2016.
 */
public class CTLEditText extends EditText {

    //////////////////////////////////////////
    // PUBLIC CLASS CONSTANT
    //////////////////////////////////////////

    public static final class Filters{
        public static final int NO_FILTER = -1;
        public static final int EMAIL = 0;
    }

    //////////////////////////////////////////
    // PRIVATE VAR
    //////////////////////////////////////////

    private String typeface;
    private int filter;
    private boolean isValid;
    private TextValidateListener textValidateListener;

    //////////////////////////////////////////
    // CONSTRUCTOR
    //////////////////////////////////////////

    public CTLEditText(Context context) {
        super(context);
        init();
    }

    public CTLEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CTLEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CTLEditText);
        try{
            typeface = array.getString(R.styleable.CTLEditText_ctl_et_typeface);
            filter   = array.getInt(R.styleable.CTLEditText_ctl_et_filter, Filters.NO_FILTER);
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
        addTextChangedListener(textWatcher);
        isValid = Boolean.FALSE;
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

    public boolean isValidate(){
        return isValid;
    }

    public void setTextValidateListener(TextValidateListener textValidateListener){
        this.textValidateListener = textValidateListener;
    }

    //////////////////////////////////////////
    // PRIVATE METHODS
    //////////////////////////////////////////

    private void applyFilter(String text){
        switch (filter){
            case Filters.EMAIL: applyEmailFilter(text); break;
        }

        if(textValidateListener != null) textValidateListener.onValidate(isValid);
    }

    private void applyEmailFilter(final String text){
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = text;

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        isValid = matcher.matches();
    }

    //////////////////////////////////////////
    // IMPLEMENTATION'S
    //////////////////////////////////////////

    //////////////////////////////////////////
    // PUBLIC INTERFACE
    //////////////////////////////////////////

    public interface TextValidateListener{
        void onValidate(boolean isValid);
    }

    //////////////////////////////////////////
    // LISTENER'S
    //////////////////////////////////////////

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            applyFilter(s.toString());
        }
    };
}
