package com.crystal.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by owais.ali on 6/2/2016.
 */
public abstract class BaseRecyclerAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private final Context context;
    private final List<T> data;
    private final int     layout;

    public BaseRecyclerAdapter(final Context context, final List<T> data, final int layout){
        this.context = context;
        this.data    = data;
        this.layout  = layout;
    }

    // add object to the end of list
    public void add(final T item){
        data.add(item);
        notifyItemInserted(getItemCount() - 1);
    }

    // add object to the end of list
    public void add(final T item, final int position){
        data.add(position, item);
        notifyItemInserted(position);
    }

    // remove object to the specified location
    public void remove(final T item){
        final int position = data.indexOf(item);
        data.remove(item);
        notifyItemInserted(position);
    }

    // get object by position
    public T getItem(final int position){
        return data.get(position);
    }

    // remove all objects in list
    public void clear(){
        final int dataLength = getItemCount();
        data.clear();
        notifyItemRangeRemoved(0, dataLength);
    }

    // sorts the content of this adapter using the specified comparator.
    public void sort(Comparator<? super T> comparator) {
        Collections.sort(data, comparator);
        notifyItemRangeChanged(0, getItemCount());
    }

    public Context getContext(){
        return this.context;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return getThis(view);
    }

    public abstract VH getThis(View v);
}
