package com.crystal.base;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.crystal.R;
import com.crystal.interfaces.OnDialogListener;


/**
 * Created by owais.ali on 5/4/2016.
 */
public abstract class BaseDialog<T extends BaseDialog<T>> extends Dialog implements View.OnClickListener {

    //////////////////////////////////////////////
    // PUBLIC STATIC ANIMATION CONSTANTS
    //////////////////////////////////////////////

    public static int SLIDE_IN_UP;
    public static int SLIDE_IN_BOTTOM;
    public static int ZOOM_IN_CENTER;
    public static int SLIDE_IN_LEFT_OUT_RIGHT;
    public static int FADE_IN_OUT;

    //////////////////////////////////////////////
    // PUBLIC CONFIG
    //////////////////////////////////////////////

    public static final class Config{
        public static final int FULL_SCREEN = android.R.style.Theme_Light;
    }

    //////////////////////////////////////////////
    // PRIVATE VAR
    //////////////////////////////////////////////

    protected OnDialogListener onDialogListener;

    //////////////////////////////////////////////
    // CONSTRUCTOR
    //////////////////////////////////////////////

    public BaseDialog(final Context context, int style){
        super(context, style);
        initDialog();
    }

    public BaseDialog(final Context context) {
        super(context);
        initDialog();
    }

    //////////////////////////////////////////////
    // FUNCTIONS
    //////////////////////////////////////////////

    private void initDialog(){

        SLIDE_IN_UP             = R.style.DialogSlideInUpAnimation;
        SLIDE_IN_BOTTOM         = R.style.DialogSlideInDownAnimation;
        ZOOM_IN_CENTER          = R.style.DialogZoomInCenterAnimation;
        SLIDE_IN_LEFT_OUT_RIGHT = R.style.DialogSlideInLeftOutRight;
        FADE_IN_OUT             = R.style.DialogFadeInOutAnimation;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(getLayout());
        final WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.x = 20;
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
        getWindow().setAttributes(params);
    }

    @Override
    public void show() {
        try{
            super.show();

            setCancelable(isCancelable());
            setCanceledOnTouchOutside(isCancelOnTouchOutside());
            init();

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void init(){}
    public boolean isCancelable(){return false;}
    public boolean isCancelOnTouchOutside(){return false;}

    public T setDialogListener(final OnDialogListener onDialogListener){
        this.onDialogListener = onDialogListener;
        return getThis();
    }

    public T setAnimation(int anim){
        getWindow().getAttributes().windowAnimations = anim;
        return getThis();
    }

    @Override
    public void onClick(View v) {}

    //////////////////////////////////////////////
    // PROTECTED METHODS
    //////////////////////////////////////////////

    protected void attachClickListeners(int... views){
        final int viewsLength = views.length;
        for(int i = 0; i < viewsLength; i++){
            findViewById(views[i]).setOnClickListener(this);
        }
    }

    //////////////////////////////////////////////
    // ABSTRACT FUNCTION
    //////////////////////////////////////////////

    public abstract int getLayout();
    public abstract T getThis();
}
