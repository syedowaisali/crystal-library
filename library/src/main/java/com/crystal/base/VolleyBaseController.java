package com.crystal.base;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.crystal.utilities.Api;

/**
 * Created by owais.ali on 9/5/2016.
 */
public class VolleyBaseController {

    // context
    private final Context mContext;

    // global request queue for volley
    private RequestQueue requestQueue;

    // a singleton instance of the application class for easy access in other places
    private static VolleyBaseController mInstance;

    private VolleyBaseController(Context mContext){
        this.mContext = mContext;
    }

    // return application controller singleton instance
    public static synchronized VolleyBaseController getInstance(Context context){
        return (mInstance == null) ? mInstance = new VolleyBaseController(context) : mInstance;
    }

    // return the volley request queue, the queue will be created if it is null
    public RequestQueue getRequestQueue(){

        // lazy initialize the request queue, the queue instance will be created when it is accessed for the first time.
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }

        return requestQueue;
    }

    // adds the specified request to the global queue, it tag is specified then it is used else default TAG is sued.
    public <T> void addToRequestQueue(Request<T> request, String tag){

        // set the default tag if tag is empty
        request.setTag(TextUtils.isEmpty(tag) ? Api.TAG : tag);

        VolleyLog.d("Adding request to queue: %s", request.getUrl());
        getRequestQueue().add(request);
    }

    // adds the specified request to the global queue using the default TAG.
    public <T> void addToRequestQueue(Request<T> request){
        addToRequestQueue(request, Api.TAG);
    }

    // cancels all pending requests by the specified TAG, is is important to specify a TAG so that the pending/ongoing request can be cancelled.
    public void cancelPendingRequests(Object tag){
        if(requestQueue != null){
            requestQueue.cancelAll(tag);
        }
    }
}
