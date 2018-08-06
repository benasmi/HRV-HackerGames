package com.mabe.productions.hrv_madison;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    public int getItemViewType(int position) {
        return data.get(position).getViewType();
    }

    @Override
    public void onBindViewHolder(final MyAdapterHolder holder, final int position) {

        final int card_type = data.get(position).getViewType();
        final RecyclerViewDataHolder item = data.get(position);


        switch (card_type){
            //Workout
            case 0:
                holder.recycler_workout_txt_duration.setText((int)(item.getWorkout_duration()/1000/60) + " min running");
                holder.recycler_workout_txt_date.setText(showDate(item.getDate()));
                setFadeAnimation(holder.recycler_view_workout_cardview);
                break;

            //Measurement
            case 1:
                holder.recycler_measurement_txt_duration.setText("Duration: " + String.valueOf(item.getDuration())+ " min");
                holder.recycler_measurement_txt_date.setText(showDate(item.getDate()));
                setFadeAnimation(holder.recycler_view_measurement_cardview);
                break;
        }



    }


    private String showDate(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        String men = Utils.convertNumberToMonth(month);
        return String.valueOf(new StringBuilder().append(day).append(" ").append(men)) ;
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1000);
        view.startAnimation(anim);
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

        //Workout
        private TextView recycler_workout_txt_card_title;
        private TextView recycler_workout_txt_duration;
        private TextView recycler_workout_txt_date;
        private CardView recycler_view_workout_cardview;

        //Measurement
        private TextView recycler_measurement_txt_card_title;
        private TextView recycler_measurement_txt_duration;
        private TextView recycler_measurement_txt_date;
        private CardView recycler_view_measurement_cardview;


        public MyAdapterHolder(View itemView, int viewType) {
            super(itemView);



            switch (viewType){

                //Workout
                case 0:
                    recycler_workout_txt_card_title = itemView.findViewById(R.id.recycler_workout_card_title);
                    recycler_workout_txt_duration = itemView.findViewById(R.id.recycler_workout_txt_duration);
                    recycler_workout_txt_date = itemView.findViewById(R.id.recycler_workout_txt_date);
                    recycler_view_workout_cardview = itemView.findViewById(R.id.recycler_view_workout_cardview);

                   recycler_view_workout_cardview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.i("recycler", "DataRec: " + String.valueOf(data.get(getAdapterPosition()).getWorkout_duration()));

                            context.startActivity(new Intent(context, AdvancedWorkoutHistoryActivity.class).putExtra("Workout", data.get(getAdapterPosition())));
                        }
                    });
                    break;


                //Measurement
                case 1:

                    recycler_measurement_txt_card_title = itemView.findViewById(R.id.recycler_measurement_txt_card_title);
                    recycler_measurement_txt_duration = itemView.findViewById(R.id.recycler_measurement_txt_measurent_duration);
                    recycler_measurement_txt_date = itemView.findViewById(R.id.recycler_measurement_txt_date);
                    recycler_view_measurement_cardview = itemView.findViewById(R.id.recycler_view_measurement_cardview);
                    recycler_view_measurement_cardview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            context.startActivity(new Intent(context, AdvancedMeasurementHistoryActivity.class).putExtra("Measurement",data.get(getAdapterPosition())));
                        }
                    });
                    break;
            }

        }
    }




}





