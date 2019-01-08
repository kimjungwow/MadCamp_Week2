package com.cs496.cs496_week2;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Parsania Hardik on 11-May-17.
 */
public class Tab3HorseViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<HorseModel> horseModelArrayList;

    public Tab3HorseViewAdapter(Context context, ArrayList<HorseModel> horseModelArrayList) {

        this.context = context;
        this.horseModelArrayList = horseModelArrayList;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public int getCount() {
        return horseModelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return horseModelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.horselv_item, null, true);

            holder.tvname = (TextView) convertView.findViewById(R.id.name);
            holder.tvspeed = (TextView) convertView.findViewById(R.id.speed);
            holder.tvacceleration = (TextView) convertView.findViewById(R.id.acceleration);
            holder.tvdividendRate = (TextView) convertView.findViewById(R.id.dividendRate);
            holder.tvfallOff = (TextView) convertView.findViewById(R.id.fallOff);

            holder.tvmaxSpeed = (TextView) convertView.findViewById(R.id.maxSpeed);




            convertView.setTag(holder);
        } else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvname.setText(horseModelArrayList.get(position).getName());
        holder.tvspeed.setText(horseModelArrayList.get(position).getSpeed().toString());
        holder.tvacceleration.setText(horseModelArrayList.get(position).getAcceleration().toString());
        holder.tvdividendRate.setText(horseModelArrayList.get(position).getDividendRate().toString());
        holder.tvfallOff.setText(horseModelArrayList.get(position).getFallOff().toString());

        holder.tvmaxSpeed.setText(horseModelArrayList.get(position).getMaxSpeed().toString());




        return convertView;
    }

    private class ViewHolder {

        protected TextView tvname, tvspeed, tvlocation, tvacceleration, tvmaxSpeed, tvfallOff, tvdividendRate ;


    }
}