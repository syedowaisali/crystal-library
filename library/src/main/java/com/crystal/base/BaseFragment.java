package com.crystal.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.crystal.interfaces.OnWSResponse;

import org.json.JSONObject;

/**
 * Created by owais.ali on 5/4/2016.
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener, OnWSResponse<BaseModel> {

    protected View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }

        try {
            rootView = inflater.inflate(getLayout(), container, false);
        } catch (InflateException e) {
            e.printStackTrace();
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    public void onClick(final View v) {

    }

    @Override
    public void onData(final JSONObject data, final BaseModel dataModel, final int requestCode) {

    }

    @Override
    public void noData(final String message, final int requestCode) {
        toast(message);
    }

    @Override
    public void onError(final String error, final int requestCode) {
        toast(error);
    }

    @Override
    public void onCancel(final int requestCode) {

    }

    // call after fragment activity created
    public void init(){}

    public final void attachClickListener(final int... views){
        for(int i = 0; i < views.length; i++){
            rootView.findViewById(views[i]).setOnClickListener(this);
        }
    }

    // on back pressed
    public void onBackPressed(){}

    // replace fragment to current fragment
    public void changeFragment(short fragment_id){
        //getHolder().displayView(fragment_id);
    }

    public void fragmentChange(Fragment fragment, int fragment_id){ }

    // get current activity
    public BaseActivity getHolder(){
        return (BaseActivity)getActivity();
    }

    // force implement to this method to every child fragment
    public abstract int getLayout();

    // goto activity ----------------------------------------------->
    public void gotoActivity(final Class<?> activity){
        this.gotoActivity(activity, new Bundle());
    }

    public void gotoActivity(final Class<?> activity, final Bundle bundle){

        Intent intent = new Intent(getActivity(), activity);
        intent.putExtras(bundle);
        getActivity().startActivity(intent);
    }

    // toast ------------------------------------------------------->
    public void toast(final String message, final int length, final int gravity){
        Toast toast = Toast.makeText(getActivity(), message, length);
        //.setGravity(gravity | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    public void toast(final String message){
        this.toast(String.valueOf(message), Toast.LENGTH_LONG, Gravity.BOTTOM);
    }

    public void toast(final int message){
        this.toast(String.valueOf(message), Toast.LENGTH_LONG, Gravity.BOTTOM);
    }

    public void toast(final float message){
        this.toast(String.valueOf(message), Toast.LENGTH_LONG, Gravity.BOTTOM);
    }

    public void toast(final String message, final int length){
        toast(message, length, Gravity.BOTTOM);
    }

    public void toast(final int message, final int length){
        toast(String.valueOf(message), length, Gravity.BOTTOM);
    }

    public void toast(final float message, final int length){
        toast(String.valueOf(message), length, Gravity.BOTTOM);
    }

    // get views ---------------------------------------------------------

    public final <T extends View> T getView(final int resId){
        return getView(resId, false);
    }

    public final <T extends View> T getView(int resId, final boolean attachClickListener){
        final View v = rootView.findViewById(resId);
        if(attachClickListener) v.setOnClickListener(this);
        return (T)v;
    }

    public final <T extends View> T getView(int resId, final View.OnClickListener clickListener){
        final View v = rootView.findViewById(resId);
        v.setOnClickListener(clickListener);
        return (T)v;
    }
}
