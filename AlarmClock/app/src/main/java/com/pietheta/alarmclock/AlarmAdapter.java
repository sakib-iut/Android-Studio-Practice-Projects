package com.pietheta.alarmclock;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {
    Context context;
    ArrayList<AlarmItem> alarmList;
    AlarmDbHelper helper;
    Calendar currentTime;

    public AlarmAdapter(ArrayList<AlarmItem> alarmList, Context context) {
        this.alarmList = alarmList;
        this.context = context;
    }

    public OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onClickSwitch(int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    public static class AlarmViewHolder extends RecyclerView.ViewHolder {

        TextView time;
        TextView remainingTime;
        ImageView missionImg;
        Switch alarmSwitch;

        public AlarmViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            time = itemView.findViewById(R.id.alarm_time);
            remainingTime = itemView.findViewById(R.id.remaining_time);
            missionImg = itemView.findViewById(R.id.mission_img);
            alarmSwitch = itemView.findViewById(R.id.switch_alarm);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });

            alarmSwitch.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onClickSwitch(position);

                    }
                }
            });

        }


    }


    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View alarmItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_item, parent, false);

        return new AlarmViewHolder(alarmItemView, listener);
    }


    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        AlarmItem currentItem = alarmList.get(position);
        holder.time.setText(currentItem.getTime());
        holder.time.setTextColor(getTextColor(position));
        holder.remainingTime.setText(getRemainingTime(position));
        holder.missionImg.setImageResource(currentItem.getImageResource());
        holder.missionImg.setColorFilter(getImageColor(position));
        holder.alarmSwitch.setChecked(currentItem.isSwitchOn());

    }

    private int getImageColor(int position) {
        if (alarmList.get(position).isSwitchOn())
            return context.getResources().getColor(R.color.colorAccent);
        else
            return context.getResources().getColor(R.color.grayColor);
    }

    private int getTextColor(int position) {
        if (alarmList.get(position).isSwitchOn())
            return Color.BLACK;
        else
            return context.getResources().getColor(R.color.grayColor);
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    //methods
    private String getRemainingTime(int position) {
        boolean isOn = alarmList.get(position).isSwitchOn();
        if (isOn) {
            currentTime = Calendar.getInstance();
            int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);//returns 24format
            int currentMin = currentTime.get(Calendar.MINUTE);

            int alarmHour = alarmList.get(position).getHour();
            int alarmMin = alarmList.get(position).getMinute();

            int hour;
            int minute;

            if (alarmMin >= currentMin) {
                minute = alarmMin - currentMin;
            } else {
                minute = 60 + alarmMin - currentMin;
                ++currentHour;
            }
            if (alarmHour >= currentHour) {
                hour = alarmHour - currentHour;
            } else {
                hour = 24 + alarmHour - currentHour;
            }

            StringBuilder builder = new StringBuilder();
            builder.append("Alarm in ");
            if(hour==1)builder.append(hour+" hour ");
            else if(hour>1)builder.append(hour+" hours ");
            if(hour==0 && minute<2) builder.append("less than 1 minute");
            else if(minute==1) builder.append(" 1 minute");
            else if(minute!=0) builder.append(minute+" minutes");

            return builder.toString();
        }
        else{
            return new AlarmDbHelper(context).getRepeatType(position);
        }
    }
}



