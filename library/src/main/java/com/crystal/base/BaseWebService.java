package com.crystal.base;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
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
public abstract class BaseWebService <T extends BaseWebService<T, M>, M extends BaseModel<M>> implements OnRequestPermissionResult {

    private static final int TIMEOUT_IN_SECONDS = 20;

    //////////////////////////////////////////
    // PRIVATE VAR
    //////////////////////////////////////////

    protected RequestParams              params;
    protected CrystalAsyncHttpClient     request;
    private final ProcessProgressDialog  processProgressDialog;
    private   OnWSResponse<M>            listener;
    private   OnWSResponse<M>            transparentListener;
    private   final Context              context;
    private   int                        requestCode;
    private   boolean                    cancelService;
    private   Mode                       serviceMode;

    //////////////////////////////////////////
    // PUBLIC ENUM
    //////////////////////////////////////////

    public enum Mode{
        TRANSPARENT, NORMAL
    }

    //////////////////////////////////////////
    // CONSTRUCTOR
    //////////////////////////////////////////

    public BaseWebService(final Context context){
        this.context          = context;
        this.requestCode      = -1;
        processProgressDialog = new ProcessProgressDialog(this.context, android.R.style.Theme_Light);
        ((BaseActivity) context).setRequestPermissionResultListener(this);
        serviceMode = Mode.NORMAL;
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

    public final void dismissProcessProgressDialog(){
        if(processProgressDialog != null){
            if(processProgressDialog.isShowing()){
                processProgressDialog.dismiss();
            }
        }
    }

    public final void cancelRequest(){
        if(request != null){
            cancelService = true;
            request.cancelRequests(context, true);
            if(listener != null) listener.onCancel(requestCode);
        }
    }

    public final OnWSResponse getListener(){
        return this.listener;
    }

    public final void callService(final Mode serviceMode){

        // check network connection
        if(! AppHelper.isNetworkAvailable(context)){
            listener.onError("No network connection.", requestCode);
            return;
        }

        if(params == null) params = new RequestParams();

        request = new CrystalAsyncHttpClient(getContext());
        request.setProgressMessage(getProgressMessage());
        request.setTimeout(getTimeout());
        request.isCancelable(isCancelable());

        // modify request client
        request = modifyClient(request);

        // show progress dialog
        if(serviceMode == Mode.NORMAL) showProgressDialog();

        if(getMethodType() == Api.MethodType.GET){
            request.get(getApiUrl(), params, serviceMode == Mode.NORMAL ? responseHandler : transparentResponseHandler);
        }
        else{
            request.post(getApiUrl(), params, serviceMode == Mode.NORMAL ? responseHandler : transparentResponseHandler);
        }
    }

    public final void callTransparentService(final OnWSResponse<M> transparentListener){
        this.transparentListener = transparentListener;
        callService(Mode.TRANSPARENT);
    }

    //////////////////////////////////////////
    // PROTECTED FUNCTIONS
    //////////////////////////////////////////

    protected int getTimeout(){
        return (request != null) ? request.getTimeout() : TIMEOUT_IN_SECONDS * 1000;
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

    protected String getStatusKey(){
        return Api.Status.STATUS;
    }

    protected String getMessageKey(){
        return Api.Status.MESSAGE;
    }

    protected String getDataKey(){
        return Api.DATA;
    }

    protected String getStatusSuccess(){
        return Api.Status.SUCCESS;
    }

    protected CrystalAsyncHttpClient modifyClient(final CrystalAsyncHttpClient client){
        return client;
    }

    protected void callService(){
        callService(serviceMode);
    }

    protected void showProgressDialog(){
        if(isProcessProgressDialog() && isShowProgressDialog()){
            processProgressDialog.show();
        }
        else if(isShowProgressDialog()){
            request.showProgressBar();
        }
    }

    protected void dismissDialog(){

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

    protected void dataReceived(M dataModel){}

    //////////////////////////////////////////
    // OVERLOAD
    //////////////////////////////////////////

    public final void execute(){
        execute(requestCode);
    }

    public final void execute(final int requestCode){

        execute(new OnWSResponse<M>() {

            @Override
            public void onData(JSONObject jsonData, M dataModel, int requestCode) {

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
                        grant = false;
                    }
                }

                if(grantResults.length > 1){
                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        grant = true;
                    }
                    else{
                        listener.onError(permissions[0] + " permission denied.", requestCode);
                        grant = false;
                    }
                }

                if(grant) callService();

                break;
        }
    }

    //////////////////////////////////////////
    // ABSTRACT FUNCTIONS
    //////////////////////////////////////////

    public abstract M getDataModel(JSONObject jsonData);
    public abstract String getApiUrl();
    public abstract Api.MethodType getMethodType();
    public abstract T getThis();

    //////////////////////////////////////////
    // PRIVATE RESPONSE HANDLER CLASS
    //////////////////////////////////////////

    private final CrystalHttpResponseHandler responseHandler = new CrystalHttpResponseHandler() {
        @Override
        public void onCancel() {
            super.onCancel();
            if(cancelService){
                cancelService = false;
                return;
            }
            listener.onCancel(requestCode);
            cancelService = false;
        }

        @Override
        public void onResponse(int arg0, Header[] arg1, String response) {
            super.onResponse(arg0, arg1, response);
            if(cancelService){
                cancelService = false;
                return;
            }

            try {

                // create response to json object
                final JSONObject jsonObject = new JSONObject(response);

                // check status key exists on response
                if(jsonObject.has(getStatusKey())){

                    // check if status is success
                    if (jsonObject.getString(getStatusKey()).equalsIgnoreCase(getStatusSuccess())) {

                        // trying to create response to json
                        try {

                            // check data is json object or other format
                            if (dataIsJSONObject()) {

                                // check data key exist on data
                                if(jsonObject.has(getDataKey())){

                                    // fire on data method to caller
                                    final JSONObject data = jsonObject.getJSONObject(getDataKey());
                                    listener.onData(data, getDataModel(data), requestCode);
                                    dataReceived(getDataModel(data));
                                }
                                else{

                                    // no data key exist on response send all data to caller
                                    listener.onData(jsonObject, getDataModel(jsonObject), requestCode);
                                    dataReceived(getDataModel(jsonObject));
                                }
                            }

                            // if data is another form json array or string or etc
                            else {

                                // create empty json object and get json array from response and send to caller
                                JSONObject dataWrapper = new JSONObject();
                                dataWrapper.put("data", jsonObject.getJSONArray("data"));
                                listener.onData(dataWrapper, getDataModel(dataWrapper), requestCode);
                                dataReceived(getDataModel(dataWrapper));
                            }
                        }

                        // exception occur notify to caller with message
                        catch (JSONException e) {
                            e.printStackTrace();
                            listener.onError(e.getMessage(), requestCode);
                        }
                    }

                    // if status is not success
                    else {

                        // check if message key exist on data
                        if(jsonObject.has(getMessageKey())){

                            // notify to caller with message
                            listener.noData(jsonObject.getString(getMessageKey()), requestCode);
                        }

                        // opp's not message key exist on data
                        else{

                            // notify to caller there no message key exist in response
                            listener.noData(getMessageKey() + " key not exists.", requestCode);
                            Log.w(Api.TAG, getMessageKey()+ " key not exists.");
                        }
                    }
                }

                // opp's not status key exist on data
                else{

                    // notify to caller there is not status key exist in response
                    listener.onData(jsonObject, getDataModel(jsonObject), requestCode);
                    dataReceived(getDataModel(jsonObject));
                    Log.w(Api.TAG, getStatusKey()+ " key not exists.");
                }
            }

            // exception occur notify to caller with message
            catch (JSONException e) {
                e.printStackTrace();
                listener.onError(e.getMessage(), requestCode);
            }

            // dismiss dialog
            dismissDialog();
            cancelService = false;
        }

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable throwable) {
            super.onFailure(arg0, arg1, arg2, throwable);

            if(cancelService){
                cancelService = false;
                return;
            }

            try {

                // notify to caller with error
                listener.onError((throwable.getMessage() == null) ? "Error contacting to server." : throwable.getMessage(), requestCode);
                dismissDialog();

            }

            // exception occur notify to caller with message
            catch (Exception ex) {
                ex.printStackTrace();
                listener.onError(ex.getMessage(), requestCode);
                dismissDialog();
            }

            cancelService = false;
        }
    };

    private final CrystalHttpResponseHandler transparentResponseHandler = new CrystalHttpResponseHandler() {

        @Override
        public void onCancel() {
            super.onCancel();
            if(cancelService){
                cancelService = false;
                return;
            }
            transparentListener.onCancel(requestCode);
            cancelService = false;
        }

        @Override
        public void onResponse(int arg0, Header[] arg1, String response) {
            super.onResponse(arg0, arg1, response);
            if(cancelService){
                cancelService = false;
                return;
            }

            try {

                // create response to json object
                final JSONObject jsonObject = new JSONObject(response);

                // check status key exists on response
                if(jsonObject.has(getStatusKey())){

                    // check if status is success
                    if (jsonObject.getString(getStatusKey()).equalsIgnoreCase(getStatusSuccess())) {

                        // trying to create response to json
                        try {

                            // check data is json object or other format
                            if (dataIsJSONObject()) {

                                // check data key exist on data
                                if(jsonObject.has(getDataKey())){

                                    // fire on data method to caller
                                    final JSONObject data = jsonObject.getJSONObject(getDataKey());
                                    transparentListener.onData(data, getDataModel(data), requestCode);
                                }
                                else{

                                    // no data key exist on response send all data to caller
                                    transparentListener.onData(jsonObject, getDataModel(jsonObject), requestCode);
                                }
                            }

                            // if data is another form json array or string or etc
                            else {

                                // create empty json object and get json array from response and send to caller
                                JSONObject dataWrapper = new JSONObject();
                                dataWrapper.put("data", jsonObject.getJSONArray("data"));
                                transparentListener.onData(dataWrapper, getDataModel(dataWrapper), requestCode);
                            }
                        }

                        // exception occur notify to caller with message
                        catch (JSONException e) {
                            e.printStackTrace();
                            transparentListener.onError(e.getMessage(), requestCode);
                        }
                    }

                    // if status is not success
                    else {

                        // check if message key exist on data
                        if(jsonObject.has(getMessageKey())){

                            // notify to caller with message
                            transparentListener.noData(jsonObject.getString(getMessageKey()), requestCode);
                        }

                        // opp's not message key exist on data
                        else{

                            // notify to caller there no message key exist in response
                            transparentListener.noData(getMessageKey() + " key not exists.", requestCode);
                            Log.w(Api.TAG, getMessageKey()+ " key not exists.");
                        }
                    }
                }

                // opp's not status key exist on data
                else{

                    // notify to caller there is not status key exist in response
                    transparentListener.onData(jsonObject, getDataModel(jsonObject), requestCode);
                    Log.w(Api.TAG, getStatusKey()+ " key not exists.");
                }
            }

            // exception occur notify to caller with message
            catch (JSONException e) {
                e.printStackTrace();
                transparentListener.onError(e.getMessage(), requestCode);
            }

            cancelService = false;
        }

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable throwable) {
            super.onFailure(arg0, arg1, arg2, throwable);

            if(cancelService){
                cancelService = false;
                return;
            }

            try {

                // notify to caller with error
                transparentListener.onError((throwable.getMessage() == null) ? "Error contacting to server." : throwable.getMessage(), requestCode);

            }

            // exception occur notify to caller with message
            catch (Exception ex) {
                ex.printStackTrace();
                transparentListener.onError(ex.getMessage(), requestCode);
            }

            cancelService = false;
        }
    };
}
