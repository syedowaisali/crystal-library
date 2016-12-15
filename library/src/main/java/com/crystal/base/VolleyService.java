package com.crystal.base;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.crystal.helpers.AppHelper;
import com.crystal.helpers.CrystalParams;
import com.crystal.interfaces.OnRequestPermissionResult;
import com.crystal.interfaces.OnWSResponse;
import com.crystal.models.ServiceInfo;
import com.crystal.ui.dialogs.CrystalProgressDialog;
import com.crystal.utilities.Api;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by owais.ali on 5/4/2016.
 */
public abstract class VolleyService<T extends VolleyService<T, M>, M extends BaseModel<M>> {

    private static final int TIMEOUT_IN_SECONDS = 20;

    //////////////////////////////////////////
    // PRIVATE VAR
    //////////////////////////////////////////

    private   CrystalParams                params;
    private   final VolleyBaseController   volleyController;
    private   final CrystalProgressDialog  crystalProgressDialog;
    private   OnWSResponse<M>              listener;
    private   OnWSResponse<M>              transparentListener;
    private   final Context                context;
    private   int                          requestCode;
    private   Mode                         serviceMode;
    private   final ProgressDialog         progressDialog;

    //////////////////////////////////////////
    // PUBLIC ENUM
    //////////////////////////////////////////

    public enum Mode{
        TRANSPARENT, NORMAL
    }

    //////////////////////////////////////////
    // CONSTRUCTOR
    //////////////////////////////////////////

    public VolleyService(final Context context){
        this.context          = context;
        this.requestCode      = -1;
        serviceMode = Mode.NORMAL;
        volleyController = VolleyBaseController.getInstance(context);

        // setup custom progress dialog
        crystalProgressDialog = getCustomProgressDialog(context, android.R.style.Theme_Light);
        crystalProgressDialog.setCanceledOnTouchOutside(isCancelable());
        crystalProgressDialog.setCancelable(isCancelable());


        // setup progress dialog
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(getProgressMessage());
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(isCancelable());
        progressDialog.setCancelable(isCancelable());
        if(! TextUtils.isEmpty(getProgressTitle())) progressDialog.setTitle(getProgressTitle());

        if(isCancelable()){
            progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancelRequest();
                }
            });
        }
    }

    //////////////////////////////////////////
    // PUBLIC FUNCTIONS
    //////////////////////////////////////////

    public final Context getContext(){
        return this.context;
    }

    public final T setParameter(final CrystalParams parameter){
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
        if(volleyController != null){
            volleyController.cancelPendingRequests(getTAG());
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

        if(params == null) params = new CrystalParams();

        params = getParams(params);

        // show progress dialog
        if(serviceMode == Mode.NORMAL) showProgressDialog();

        final int methodType = (getMethodType() == Api.MethodType.GET) ? Request.Method.GET : Request.Method.POST;

        String url = getApiUrl();

        if(getMethodType() == Api.MethodType.GET){
            url += "?";
            for(Map.Entry<String, String> entry : params.getMapParams().entrySet()){
                url += entry.getKey() + "=" + entry.getValue() + "&";
            }
        }

        final Request<String> jsonObjectRequest = new StringRequest(
                methodType,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(serviceMode == Mode.NORMAL){
                            normalResponse(response);
                        }
                        else{
                            transparentResponse(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(serviceMode == Mode.NORMAL){
                            normalError(error);
                        }
                        else{
                            transparentError(error);
                        }
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params.getMapParams();
            }

            @Override
            public String getCacheKey() {
                String temp = super.getCacheKey();
                for (Map.Entry<String, String> entry : params.getMapParams().entrySet())
                    temp += entry.getKey() + "=" + entry.getValue();
                return temp;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders();
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
                    if (cacheEntry == null) {
                        cacheEntry = new Cache.Entry();
                    }
                    final long cacheHitButRefreshed = cacheRefreshInMinute() * 60 * 1000; // in default minutes cache will be hit, but also refreshed on background
                    final long cacheExpired = cacheExpireInMinute() * 60 * 1000; // cache entry expires completely
                    long now = System.currentTimeMillis();
                    final long softExpire = now + cacheHitButRefreshed;
                    final long ttl = now + cacheExpired;
                    cacheEntry.data = response.data;
                    cacheEntry.softTtl = softExpire;
                    cacheEntry.ttl = ttl;
                    String headerValue;
                    headerValue = response.headers.get("Date");
                    if (headerValue != null) {
                        cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    headerValue = response.headers.get("Last-Modified");
                    if (headerValue != null) {
                        cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    cacheEntry.responseHeaders = response.headers;
                    final String string = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(string, cacheEntry);
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                }
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(getTimeout(), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjectRequest.setShouldCache(cacheEnable());
        volleyController.addToRequestQueue(jsonObjectRequest, getTAG());
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
        return TIMEOUT_IN_SECONDS * 1000;
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

    protected CrystalParams getParams(CrystalParams params){
        return params;
    }

    protected String getProgressTitle(){
        return "";
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

    protected boolean isSkipCondition(){
        return false;
    }

    protected String getTAG(){
        return Api.TAG;
    }

    protected void callService(){
        callService(serviceMode);
    }

    protected void showProgressDialog(){
        if(isShowCustomProgressDialog() && isShowProgressDialog()){
            crystalProgressDialog.show();
        }
        else if(isShowProgressDialog()){
            progressDialog.show();
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
        else if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    protected void dataReceived(M dataModel){}

    protected void normalResponse(String response){
        try {

            // create service info
            final ServiceInfo serviceInfo = new ServiceInfo();

            // create response to json object
            final JSONObject jsonObject = new JSONObject(response);

            // check status key exists on response
            if(jsonObject.has(getStatusKey()) || isSkipCondition()){

                // fill status
                if(! isSkipCondition()){

                    serviceInfo.setStatus(jsonObject.getString(getStatusKey()).equalsIgnoreCase(getStatusSuccess()));
                    serviceInfo.setStatusCode(jsonObject.optInt(getStatusCodeKey()));
                    serviceInfo.setMessage(jsonObject.optString(getMessageKey()));

                    onServiceInfo(serviceInfo);
                }

                // check if status is success
                if (serviceInfo.getStatus() || isSkipCondition()) {

                    // trying to create response to json
                    try {

                        // check data is json object or other format
                        if (dataIsJSONObject()) {

                            // check data key exist on data
                            if(jsonObject.has(getDataKey())){

                                // fire on data method to caller
                                final JSONObject data = jsonObject.getJSONObject(getDataKey());
                                final M dataModel = getDataModel(data, serviceInfo);
                                listener.onData(data, dataModel, requestCode);
                                dataReceived(dataModel);
                            }
                            else{

                                // no data key exist on response send all data to caller
                                final M dataModel = getDataModel(jsonObject, serviceInfo);
                                listener.onData(jsonObject, dataModel, requestCode);
                                dataReceived(dataModel);
                            }
                        }

                        // if data is another form json array or string or etc
                        else {

                            // create empty json object and get json array from response and send to caller
                            JSONObject dataWrapper = new JSONObject();
                            dataWrapper.put("data", jsonObject.getJSONArray("data"));
                            final M dataModel = getDataModel(dataWrapper, serviceInfo);
                            listener.onData(dataWrapper, dataModel, requestCode);
                            dataReceived(dataModel);
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
    }

    protected void transparentResponse(String response){

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
                            }
                            else{

                                // no data key exist on response send all data to caller
                                transparentListener.onData(jsonObject, getDataModel(jsonObject, serviceInfo), requestCode);
                            }
                        }

                        // if data is another form json array or string or etc
                        else {

                            // create empty json object and get json array from response and send to caller
                            JSONObject dataWrapper = new JSONObject();
                            dataWrapper.put("data", jsonObject.getJSONArray("data"));
                            transparentListener.onData(dataWrapper, getDataModel(dataWrapper, serviceInfo), requestCode);
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
    }

    protected void normalError(VolleyError volleyError){

        try {
            // notify to caller with error
            listener.onError((volleyError.getMessage() == null) ? "Error contacting to server." : volleyError.getMessage(), requestCode);
        }

        // exception occur notify to caller with message
        catch (Exception ex) {
            ex.printStackTrace();
            listener.onError(ex.getMessage(), requestCode);
        }

        dismissDialog();
    }

    protected void transparentError(VolleyError volleyError){

        try {
            // notify to caller with error
            transparentListener.onError((volleyError.getMessage() == null) ? "Error contacting to server." : volleyError.getMessage(), requestCode);
        }

        // exception occur notify to caller with message
        catch (Exception ex) {
            ex.printStackTrace();
            transparentListener.onError(ex.getMessage(), requestCode);
        }
    }

    protected int cacheRefreshInMinute(){
        return 2;
    }

    protected int cacheExpireInMinute(){
        return 24 * 30;
    }

    protected boolean cacheEnable(){
        return true;
    }

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

}
