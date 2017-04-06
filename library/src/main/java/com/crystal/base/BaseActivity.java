package com.crystal.base;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.crystal.interfaces.OnDialogListener;
import com.crystal.interfaces.OnPermissionResult;
import com.crystal.interfaces.OnRequestPermissionResult;
import com.crystal.interfaces.OnWSResponse;

import org.json.JSONObject;

/**
 * Created by owais.ali on 5/4/2016.
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener, OnWSResponse<BaseModel>, OnDialogListener, OnPermissionResult {

    //////////////////////////////////////////
    // PRIVATE VAR
    //////////////////////////////////////////

    private Intent intent;
    private OnRequestPermissionResult onRequestPermissionResult;
    private OnPermissionResult onPermissionResult;

    //////////////////////////////////////////
    // OVERRIDE METHODS
    //////////////////////////////////////////

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setup(savedInstanceState);
    }

    //////////////////////////////////////////
    // IMPLEMENTATION'S
    //////////////////////////////////////////

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onData(JSONObject jsonData, BaseModel dataModel, int requestCode) {

    }

    @Override
    public void noData(String message, int requestCode) {
        toast(message);
    }

    @Override
    public void onError(String error, int requestCode) {
        toast(error);
    }

    @Override
    public void onCancel(int requestCode) {

    }

    @Override
    public void done() {

    }

    @Override
    public void cancel() {

    }

    @Override
    public void onPermissionSuccess(int requestCode) {

    }

    @Override
    public void onPermissionFailure(int requestCode) {

    }

    //////////////////////////////////////////
    // PROTECTED FUNCTIONS
    //////////////////////////////////////////

    protected void setup(Bundle savedInstanceState){

        // before set layout
        beforeInit(savedInstanceState);

        // set content view
        setContentView(getLayout());

        // call init
        init(savedInstanceState);
    }

    protected void beforeInit(Bundle savedInstanceState){}

    protected void init(Bundle savedInstanceState){}

    //////////////////////////////////////////
    // PUBLIC FUNCTIONS
    //////////////////////////////////////////

    public final <T extends View> T getView(final int resId){
        return getView(resId, false);
    }

    public final <T extends View> T getView(int resId, final boolean attachClickListener){
        final View v = findViewById(resId);
        if(attachClickListener) v.setOnClickListener(this);
        return (T)v;
    }

    public final <T extends View> T getView(int resId, final View.OnClickListener clickListener){
        final View v = findViewById(resId);
        v.setOnClickListener(clickListener);
        return (T)v;
    }

    public final <T> T getSystemsService(String name){
        return (T) super.getSystemService(name);
    }

    public final void bindClickListener(final int resId, final View.OnClickListener clickListener){
        View v = getView(resId);
        bindClickListener(v, clickListener);
    }

    public final void bindClickListener(final View v, final View.OnClickListener clickListener){
        if(v != null) v.setOnClickListener(clickListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(onRequestPermissionResult != null) onRequestPermissionResult.permissionResult(requestCode, permissions, grantResults);
        if(onPermissionResult != null){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                onPermissionResult.onPermissionSuccess(requestCode);
            }
            else{
                onPermissionResult.onPermissionFailure(requestCode);
            }
        }
    }

    public final void attachClickListener(final int... views){
        for(int i = 0; i < views.length; i++){
            findViewById(views[i]).setOnClickListener(this);
        }
    }

    public final void setRequestPermissionResultListener(OnRequestPermissionResult listener){
        this.onRequestPermissionResult = listener;
    }

    // runtime permission request ----------------------------------------------->
    public final void permissionRequest(final String... permissions){
        permissionRequest(0, permissions);
    }

    public final void permissionRequest(final int requestCode, final String... permissions){
        permissionRequest(this, permissions);
    }

    public final void permissionRequest(final OnPermissionResult onPermissionResult, final String... permissions){
        permissionRequest(0, onPermissionResult, permissions);
    }

    public final void permissionRequest(final int requestCode, final OnPermissionResult onPermissionResult, final String... permissions){
        if(permissions.length > 0){
            try{
                ActivityCompat.requestPermissions(this, permissions, requestCode);
                this.onPermissionResult = onPermissionResult;
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    // goto activity ----------------------------------------------->
    public final void gotoActivity(final Class<?> activity){
        this.gotoActivity(activity, false, new Bundle());
    }

    public final void gotoActivity(final Class<?> activity, final boolean finish){
        this.gotoActivity(activity, finish, new Bundle());
    }

    public final void gotoActivity(final Class<?> activity, final Bundle bundle){
        this.gotoActivity(activity, false, bundle);
    }

    public final void gotoActivity(final Class<?> activity, final boolean finish, final Bundle bundle){

        intent = new Intent(this, activity);
        intent.putExtras(bundle);
        startActivity(intent);

        if(finish) finish();
    }

    // toast ------------------------------------------------------->
    public final void toast(final String message, final int length, final int gravity){
        try{
            Toast toast = Toast.makeText(this, message, length);
            //toast.setGravity(gravity| Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public final void toast(final String message){
        this.toast(String.valueOf(message), Toast.LENGTH_LONG, Gravity.BOTTOM);
    }

    public final void toast(final int message){
        this.toast(String.valueOf(message), Toast.LENGTH_LONG, Gravity.BOTTOM);
    }

    public final void toast(final float message){
        this.toast(String.valueOf(message), Toast.LENGTH_LONG, Gravity.BOTTOM);
    }

    public final void toast(final String message, final int length){
        toast(message, length, Gravity.BOTTOM);
    }

    public final void toast(final int message, final int length){
        toast(String.valueOf(message), length, Gravity.BOTTOM);
    }

    public final void toast(final float message, final int length){
        toast(String.valueOf(message), length, Gravity.BOTTOM);
    }

    //////////////////////////////////////////
    // ABSTRACT FUNCTIONS
    //////////////////////////////////////////

    public abstract int getLayout();
}
