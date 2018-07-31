package com.mabe.productions.hrv_madison;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benas on 7/31/2018.
 */

public class AdapterRecyclerView extends RecyclerView.Adapter<AdapterRecyclerView.MyAdapterHolder> {

    private ArrayList<RecyclerViewDataHolder> data;
    private Context context;

    public AdapterRecyclerView(ArrayList<RecyclerViewDataHolder> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(MyAdapterHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).getViewType();
    }

    @Override
    public void onBindViewHolder(MyAdapterHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public MyAdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        AdapterRecyclerView.MyAdapterHolder holder;
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        switch (viewType){
            //post layout
            case 0:
                view = layoutInflater.inflate(R.layout.recyclerview_running, parent, false);
                holder = new MyAdapterHolder(view, viewType);
                return holder;

            case 1:
                view = layoutInflater.inflate(R.layout.recyclerview_measurement, parent, false);
                holder = new MyAdapterHolder(view, viewType);
                return holder;

        }

        return null;
    }

    public class MyAdapterHolder extends RecyclerView.ViewHolder{

        public MyAdapterHolder(View itemView, int viewType) {
            super(itemView);
        }
    }


}





