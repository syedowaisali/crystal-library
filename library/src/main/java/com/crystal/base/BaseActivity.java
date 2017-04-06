package com.crystal.base;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crystal.androidtoolkit.managers.CrystalAlertDialog;
import com.crystal.helpers.PicassoTrustAll;
import com.crystal.interfaces.OnDialogListener;
import com.crystal.interfaces.OnPermissionResult;
import com.crystal.interfaces.OnRequestPermissionResult;
import com.crystal.interfaces.OnWSResponse;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.json.JSONObject;

import java.io.File;

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

    public final void bind(final int resId, Object obj, Class<?> type){
        bind(resId, obj, type, null, null);
    }

    public final void bind(final int resId, Object obj, Class<?> type, View.OnClickListener clickListener){
        bind(resId, obj, type, clickListener, null);
    }

    public final void bind(final int resId, Object obj, Class<?> type, Callback picassoCallback){
        bind(resId, obj, type, null, picassoCallback);
    }

    public final void bind(final int resId, Object obj, Class<?> type, View.OnClickListener clickListener, Callback picassoCallback){
        bind(getView(resId), obj, type, clickListener, picassoCallback);
    }

    public final void bind(final View view, Object obj, Class<?> type){
        bind(view, obj, type, null, null);
    }

    public final void bind(final View view, Object obj, Class<?> type, View.OnClickListener clickListener){
        bind(view, obj, type, clickListener, null);
    }

    public final void bind(final View view, Object obj, Class<?> type, Callback picassoCallback){
        bind(view, obj, type, null, picassoCallback);
    }

    public final void bind(final View v, Object obj, Class<?> type, View.OnClickListener clickListener, Callback picassoCallback){

        // set text on textview
        if(type == TextView.class){

            // get textview frm ref id
            final TextView view = (TextView) v;

            // set text if view is not null
            if(view != null && obj != null) view.setText(String.valueOf(obj));
            if(view != null && clickListener != null) view.setOnClickListener(clickListener);
        }

        // set text on edittext
        else if(type == EditText.class){

            // get edittext from ref id
            final EditText view = (EditText) v;

            // set text if view is not null
            if(view != null && obj != null) view.setText(String.valueOf(obj));
            if(view != null && clickListener != null) view.setOnClickListener(clickListener);
        }

        // set text on button
        else if(type == Button.class){

            // get edittext from ref id
            final Button view = (Button) v;

            // set text if view is not null
            if(view != null && obj != null) view.setText(String.valueOf(obj));
            if(view != null && clickListener != null) view.setOnClickListener(clickListener);
        }

        // set image on imageview
        else if(type == ImageView.class){

            // get imageview from ref id
            final ImageView view = (ImageView) v;

            // verify view is not null
            if(view == null) return;

            // attach click listener if view is not null
            if(clickListener != null) view.setOnClickListener(clickListener);

            // if obj data type is drawable
            if(obj instanceof Drawable){
                if(obj != null) view.setImageDrawable((Drawable) obj);
                return;
            }

            // if obj data type is bitmap
            if(obj instanceof Bitmap){
                if(obj != null) view.setImageBitmap((Bitmap) obj);
                return;
            }

            // get picasso
            final Picasso picasso = PicassoTrustAll.getInstance(this);
            RequestCreator requestCreator = null;

            // if obj data type is integer
            if(obj instanceof Integer){
                if(obj != null) requestCreator = picasso.load((int) obj);
            }

            // if obj data type is path or url
            else if(obj instanceof String){
                if(obj != null) requestCreator = picasso.load(String.valueOf(obj));
            }

            // if obj data type is uri
            else if(obj instanceof Uri){
                if(obj != null) requestCreator = picasso.load((Uri) obj);
            }

            // if data type is file
            else if(obj instanceof File){
                if(obj != null) requestCreator = picasso.load((File) obj);
            }

            // load image into imageview
            if(requestCreator != null) requestCreator.into(view, picassoCallback);
        }
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

    // alert ------------------------------------------------------->
    public final void alert(final String _message){
        this.alert("Title", _message);
    }

    public final void alert(final String _title, final String _message){
        CrystalAlertDialog cAlert = new CrystalAlertDialog(this);
        cAlert.setTitle(_title).setMessage(_message).alert();
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
