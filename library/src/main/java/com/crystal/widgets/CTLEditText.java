package com.crystal.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import com.crystal.R;
import com.crystal.helpers.CrystalLog;

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
    private ValidateListener textValidateListener;

    //////////////////////////////////////////
    // CONSTRUCTOR
    //////////////////////////////////////////

    public CTLEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initControl(context, attrs);
    }

    public CTLEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initControl(context, attrs);
    }

    //////////////////////////////////////////
    // INITIALIZING
    //////////////////////////////////////////

    private void initControl(Context context, AttributeSet attrs){
        TypedArray array  = context.obtainStyledAttributes(attrs, R.styleable.CTLEditText);
        TypedArray array1 = context.obtainStyledAttributes(attrs, R.styleable.CTLTextView);
        try{
            typeface = array1.getString(R.styleable.CTLTextView_ctl_tv_typeface);
            filter   = array.getInt(R.styleable.CTLEditText_ctl_et_filter, Filters.NO_FILTER);
        }
        finally {
            array.recycle();
        }

        init();
    }

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
                CrystalLog.e(ex.getMessage());
            }
        }
    }

    public final void setFilter(int filter){
        this.filter = filter;
    }

    public final boolean isValidate(){
        return isValid;
    }

    public final boolean compare(EditText editText){
        return getText().toString().equalsIgnoreCase(editText.getText().toString());
    }

    public final void setValidateListener(ValidateListener textValidateListener){
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
        final String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        final CharSequence inputStr = text;

        final Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        final Matcher matcher = pattern.matcher(inputStr);

        isValid = matcher.matches();
    }

    //////////////////////////////////////////
    // IMPLEMENTATION'S
    //////////////////////////////////////////

    //////////////////////////////////////////
    // PUBLIC INTERFACE
    //////////////////////////////////////////

    public interface ValidateListener{
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
