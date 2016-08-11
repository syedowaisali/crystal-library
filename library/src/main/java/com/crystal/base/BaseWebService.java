package com.crystal.base;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import com.crystal.androidtoolkit.network.CrystalAsyncHttpClient;
import com.crystal.androidtoolkit.network.CrystalHttpResponseHandler;
import com.crystal.helpers.AppHelper;
import com.crystal.interfaces.OnRequestPermissionResult;
import com.crystal.interfaces.OnWSResponse;
import com.crystal.ui.dialogs.ProcessProgressDialog;
import com.crystal.utilities.Api;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by owais.ali on 5/4/2016.
 */
public abstract class BaseWebService <T extends BaseWebService<T>> implements OnRequestPermissionResult {

    //////////////////////////////////////////
    // PRIVATE VAR
    //////////////////////////////////////////

    protected RequestParams params;
    protected CrystalAsyncHttpClient request;
    private ProcessProgressDialog processProgressDialog;
    private OnWSResponse listener;
    private   final Context context;
    private   int requestCode;

    //////////////////////////////////////////
    // CONSTRUCTOR
    //////////////////////////////////////////

    public BaseWebService(final Context context){
        this.context          = context;
        this.requestCode      = -1;
        processProgressDialog = new ProcessProgressDialog(this.context, android.R.style.Theme_Light);
        ((BaseActivity) context).setRequestPermissionResultListener(this);
    }

    //////////////////////////////////////////
    // PUBLIC FUNCTIONS
    //////////////////////////////////////////

    public final Context getContext(){
        return this.context;
    }

    public final T setParameter(final RequestParams parameter){
        this.params = parameter;
        return getThis();
    }

    public final T setRequestCode(final int requestCode){
        this.requestCode = requestCode;
        return getThis();
    }

    protected int getTimeout(){
        return (request != null) ? request.getTimeout() : 20000;
    }

    protected boolean dataIsJSONObject(){
        return true;
    }

    protected boolean autoDismissProgressDialog(){
        return true;
    }

    protected boolean isShowProgressDialog(){
        return true;
    }

    protected boolean isProcessProgressDialog(){
        return false;
    }

    protected boolean isCancelable(){return false;}

    protected String getProgressMessage() {
        return "Please wait ...";
    }

    public final void dismissProcessProgressDialog(){
        if(processProgressDialog != null){
            if(processProgressDialog.isShowing()){
                processProgressDialog.dismiss();
            }
        }
    }

    public final void cancelRequest(){
        if(request != null){
            request.cancelRequests(context, true);
            if(listener != null) listener.onCancel(requestCode);
        }
    }

    //////////////////////////////////////////
    // OVERLOAD
    //////////////////////////////////////////

    public final void execute(){
        execute(requestCode);
    }

    public final void execute(final int requestCode){

        execute(new OnWSResponse() {
            @Override
            public void onData(JSONObject data, int requestCode) {

            }

            @Override
            public void noData(String message, int requestCode) {

            }

            @Override
            public void onError(String error, int requestCode) {

            }

            @Override
            public void onCancel(int requestCode) {

            }
        }, requestCode);
    }

    public final void execute(final OnWSResponse onWSResponse){
        execute(onWSResponse, requestCode);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public final void execute(final OnWSResponse onWSResponse, final int requestCode){
        this.requestCode = requestCode;
        listener         = onWSResponse;

        // create empty permission needed list
        final List<String> permissionsNeeded = new ArrayList<>();

        // get permission stats
        final int hasInternetPermission           = ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET);
        final int hasAccessNetworkStatePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE);

        // check permission is fully granted
        if(hasInternetPermission != PackageManager.PERMISSION_GRANTED){
            permissionsNeeded.add(Manifest.permission.INTERNET);
        }

        // check permission is fully granted
        if(hasAccessNetworkStatePermission != PackageManager.PERMISSION_GRANTED){
            permissionsNeeded.add(Manifest.permission.ACCESS_NETWORK_STATE);
        }

        // request permission is permission needed size above 0
        if(permissionsNeeded.size() > 0){
            try{
                ActivityCompat.requestPermissions((BaseActivity) context, permissionsNeeded.toArray(new String[permissionsNeeded.size()]), 0);
            }
            catch (Exception ex){
                ex.printStackTrace();
                listener.onError(ex.getMessage(), requestCode);
            }
        }
        else{
            callService();
        }
    }

    //////////////////////////////////////////
    // PRIVATE FUNCTIONS
    //////////////////////////////////////////

    private void callService(){

        // check network connection
        if(! AppHelper.isNetworkAvailable(context)){
            listener.onError("No network connection.", requestCode);
            return;
        }

        if(params == null) params = new RequestParams();

        request = new CrystalAsyncHttpClient(getContext());
        //request.setBasicAuth("", "");
        request.setProgressMessage(getProgressMessage());
        request.setTimeout(getTimeout());
        request.isCancelable(isCancelable());

        // show progress dialog
        showProgressDialog();

        request.post(getApiUrl(), params, new CrystalHttpResponseHandler() {
            @Override
            public void onCancel() {
                super.onCancel();
                listener.onCancel(requestCode);
            }

            @Override
            public void onResponse(int arg0, Header[] arg1, String response) {
                super.onResponse(arg0, arg1, response);

                try {
                    final JSONObject jsonObject = new JSONObject(response);

                    if(jsonObject.has(Api.Status.STATUS)){
                        if (jsonObject.getString(Api.Status.STATUS).equals(Api.Status.SUCCESS)) {

                            try {
                                if (dataIsJSONObject()) {
                                    listener.onData(jsonObject.getJSONObject("data"), requestCode);
                                } else {
                                    JSONObject dataWrapper = new JSONObject();
                                    dataWrapper.put("data", jsonObject.getJSONArray("data"));
                                    listener.onData(dataWrapper, requestCode);
                                }
                            } catch (JSONException e) {
                                listener.onData(new JSONObject(), requestCode);
                            }
                        } else {
                            if(jsonObject.has(Api.Status.MESSAGE)){
                                listener.noData(jsonObject.getString(Api.Status.MESSAGE), requestCode);
                            }
                            else{
                                listener.noData(Api.Status.MESSAGE + " key not exists.", requestCode);
                                Log.w(Api.TAG, Api.Status.MESSAGE + " key not exists.");
                            }
                        }
                    }
                    else{
                        listener.onData(jsonObject, requestCode);
                        Log.w(Api.TAG, Api.Status.STATUS + " key not exists.");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onError(e.getMessage(), requestCode);
                }

                // dismiss dialog
                dismissDialog();
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable throwable) {

                super.onFailure(arg0, arg1, arg2, throwable);
                try {
                    listener.onError((throwable.getMessage() == null) ? "Error contacting to server." : throwable.getMessage(), requestCode);
                    dismissDialog();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    listener.onError(ex.getMessage(), requestCode);
                    dismissDialog();
                }

            }
        });
    }

    private void showProgressDialog(){
        if(isProcessProgressDialog() && isShowProgressDialog()){
            processProgressDialog.show();
        }
        else if(isShowProgressDialog()){
            request.showProgressBar();
        }
    }

    private void dismissDialog(){

        // dismiss custom progress dialog
        if (isProcessProgressDialog() && isShowProgressDialog()) {
            if (processProgressDialog.isShowing()) {
                if (autoDismissProgressDialog()) {
                    processProgressDialog.dismiss();
                }
            }
        }

        // dismiss built-in progress dialog
        else if (isShowProgressDialog()) {
            request.dismissProgress();
        }
    }

    //////////////////////////////////////////
    // IMPLEMENTATION
    //////////////////////////////////////////

    @Override
    public void permissionResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case 0:
                boolean grant = false;
                if(grantResults.length > 0){
                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        grant = true;
                    }
                    else{
                        listener.onError(permissions[0] + " permission denied.", requestCode);
                    }
                }

                if(grantResults.length > 1){
                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        grant = (grant) ? grant : false;
                    }
                    else{
                        listener.onError(permissions[0] + " permission denied.", requestCode);
                    }
                }

                if(grant) callService();

                break;
        }
    }

    //////////////////////////////////////////
    // ABSTRACT FUCTIONS
    //////////////////////////////////////////

    public abstract T getThis();
    public abstract String getApiUrl();
}
