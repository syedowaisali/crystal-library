package com.crystal.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by owais.ali on 5/4/2016.
 */
public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {

    //////////////////////////////////////////
    // PRIVATE VAR
    //////////////////////////////////////////

    private final Context context;
    private final List<T> data;
    private final int     resourceId;

    //////////////////////////////////////////
    // CONSTRUCTOR
    //////////////////////////////////////////

    public BaseAdapter(final Context context, final List<T> data, final int resourceId) {
        super();
        this.context    = context;
        this.data       = data;
        this.resourceId = resourceId;
    }

    //////////////////////////////////////////
    // IMPLEMENTATION'S
    //////////////////////////////////////////

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(final int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = View.inflate(getContext(), resourceId, null);
        }

        return getView(convertView, position);
    }

    public final <T> T get(final int position){
        return (T) data.get(position);
    }

    //////////////////////////////////////////
    // PROTECTED METHODS
    //////////////////////////////////////////

    protected Context getContext(){
        return this.context;
    }

    protected List<T> getData(){
        return this.data;
    }

    //////////////////////////////////////////
    // ABSTRACT METHODS BODY
    //////////////////////////////////////////

    protected abstract View getView(View view, int position);
}
