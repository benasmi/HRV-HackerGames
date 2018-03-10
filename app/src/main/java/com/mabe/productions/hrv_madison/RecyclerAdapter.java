package com.mabe.productions.hrv_madison;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mabe.productions.hrv_madison.measurements.Measurement;

import java.util.ArrayList;

/**
 * Created by Benas on 3/10/2018.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>  {

    private Context context;
    private ArrayList<Measurement> measurementArrayList;
    private LayoutInflater layoutInflater;


    public RecyclerAdapter(Context context, ArrayList<Measurement> measurementArrayList) {
        this.context = context;
        this.measurementArrayList = measurementArrayList;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return measurementArrayList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //TODO: create layout for history
        /*
        View view = layoutInflater.inflate(R.layout.list_item, parent, false);
        ViewHolder holder = new ViewHolder(view, 0);
        return holder;
        */
        return null;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Measurement data = measurementArrayList.get(position);
        //TODO: populate history data
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
