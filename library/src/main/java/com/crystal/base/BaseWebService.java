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
import com.crystal.models.ServiceInfo;
import com.crystal.ui.dialogs.CrystalProgressDialog;
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
public abstract class BaseWebService <T extends BaseWebService<T, M>, M extends BaseModel<M>>  {

    private static final int TIMEOUT_IN_SECONDS = 20;

    //////////////////////////////////////////
    // PRIVATE VAR
    //////////////////////////////////////////

    protected RequestParams              params;
    protected CrystalAsyncHttpClient     request;
    private final CrystalProgressDialog  crystalProgressDialog;
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

        serviceMode = Mode.NORMAL;

        // setup custom progress dialog
        crystalProgressDialog = getCustomProgressDialog(context, android.R.style.Theme_Light);
        crystalProgressDialog.setCanceledOnTouchOutside(isCancelable());
        crystalProgressDialog.setCancelable(isCancelable());
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
        if(crystalProgressDialog != null){
            if(crystalProgressDialog.isShowing()){
                crystalProgressDialog.dismiss();
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

        params = getParams(params);

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

    protected CrystalProgressDialog getCustomProgressDialog(Context context, int theme){
        return new CrystalProgressDialog(context, theme);
    }

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

    protected boolean isShowCustomProgressDialog(){
        return false;
    }

    protected boolean isCancelable(){return false;}

    protected RequestParams getParams(RequestParams params){
        return params;
    }

    protected String getProgressMessage() {
        return "Please wait ...";
    }

    protected String getStatusKey(){
        return Api.Status.STATUS;
    }

    protected String getStatusCodeKey(){
        return Api.Status.STATUS_CODE;
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
        if(isShowCustomProgressDialog() && isShowProgressDialog()){
            crystalProgressDialog.show();
        }
        else if(isShowProgressDialog()){
            request.showProgressBar();
        }
    }

    protected void dismissDialog(){

        // dismiss custom progress dialog
        if (isShowCustomProgressDialog() && isShowProgressDialog()) {
            if (crystalProgressDialog.isShowing()) {
                if (autoDismissProgressDialog()) {
                    crystalProgressDialog.dismiss();
                }
            }
        }

        // dismiss built-in progress dialog
        else if (isShowProgressDialog()) {
            request.dismissProgress();
        }
    }

    protected void dataReceived(M dataModel){}

    protected boolean onServiceInfo(ServiceInfo serviceInfo){
        return serviceInfo.getStatus();
    }

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

    //////////////////////////////////////////
    // ABSTRACT FUNCTIONS
    //////////////////////////////////////////

    public abstract M getDataModel(JSONObject jsonData, ServiceInfo serviceInfo);
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

                // create service info
                final ServiceInfo serviceInfo = new ServiceInfo();

                // create response to json object
                final JSONObject jsonObject = new JSONObject(response);

                // check status key exists on response
                if(jsonObject.has(getStatusKey())){

                    // fill status
                    serviceInfo.setStatus(jsonObject.getString(getStatusKey()).equalsIgnoreCase(getStatusSuccess()));
                    serviceInfo.setStatusCode(jsonObject.optInt(getStatusCodeKey()));
                    serviceInfo.setMessage(jsonObject.optString(getMessageKey()));

                    onServiceInfo(serviceInfo);

                    // check if status is success
                    if (serviceInfo.getStatus()) {

                        // trying to create response to json
                        try {

                            // check data is json object or other format
                            if (dataIsJSONObject()) {

                                // check data key exist on data
                                if(jsonObject.has(getDataKey())){

                                    // fire on data method to caller
                                    final JSONObject data = jsonObject.getJSONObject(getDataKey());
                                    listener.onData(data, getDataModel(data, serviceInfo), requestCode);
                                    dataReceived(getDataModel(data, serviceInfo));
                                }
                                else{

                                    // no data key exist on response send all data to caller
                                    listener.onData(jsonObject, getDataModel(jsonObject, serviceInfo), requestCode);
                                    dataReceived(getDataModel(jsonObject, serviceInfo));
                                }
                            }

                            // if data is another form json array or string or etc
                            else {

                                // create empty json object and get json array from response and send to caller
                                JSONObject dataWrapper = new JSONObject();
                                dataWrapper.put("data", jsonObject.getJSONArray("data"));
                                listener.onData(dataWrapper, getDataModel(dataWrapper, serviceInfo), requestCode);
                                dataReceived(getDataModel(dataWrapper, serviceInfo));
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
                    listener.noData(getStatusKey() + " key not exists.", requestCode);
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

                // create service info
                final ServiceInfo serviceInfo = new ServiceInfo();

                // create response to json object
                final JSONObject jsonObject = new JSONObject(response);

                // check status key exists on response
                if(jsonObject.has(getStatusKey())){

                    // fill status
                    serviceInfo.setStatus(jsonObject.getString(getStatusKey()).equalsIgnoreCase(getStatusSuccess()));
                    serviceInfo.setStatusCode(jsonObject.optInt(getStatusCodeKey()));
                    serviceInfo.setMessage(jsonObject.optString(getMessageKey()));

                    onServiceInfo(serviceInfo);

                    // check if status is success
                    if (serviceInfo.getStatus()) {

                        // trying to create response to json
                        try {

                            // check data is json object or other format
                            if (dataIsJSONObject()) {

                                // check data key exist on data
                                if(jsonObject.has(getDataKey())){

                                    // fire on data method to caller
                                    final JSONObject data = jsonObject.getJSONObject(getDataKey());
                                    transparentListener.onData(data, getDataModel(data, serviceInfo), requestCode);
                                    dataReceived(getDataModel(data, serviceInfo));
                                }
                                else{

                                    // no data key exist on response send all data to caller
                                    transparentListener.onData(jsonObject, getDataModel(jsonObject, serviceInfo), requestCode);
                                    dataReceived(getDataModel(jsonObject, serviceInfo));
                                }
                            }

                            // if data is another form json array or string or etc
                            else {

                                // create empty json object and get json array from response and send to caller
                                JSONObject dataWrapper = new JSONObject();
                                dataWrapper.put("data", jsonObject.getJSONArray("data"));
                                transparentListener.onData(dataWrapper, getDataModel(dataWrapper, serviceInfo), requestCode);
                                dataReceived(getDataModel(dataWrapper, serviceInfo));
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
                    transparentListener.noData(getStatusKey() + " key not exists.", requestCode);
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
