package com.primetech.android.sensor;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.primetech.android.sensor.annotation.LocationModel;

import java.util.List;

import butterknife.BindView;

/**
 * Created by TD-Android on 1/14/2018.
 */

public class LocAdapter extends RecyclerView.Adapter<LocAdapter.ViewHolder> {

    List<LocationModel> locationList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_accuracy, tv_latitude, tv_longitude;

        public ViewHolder(View view) {
            super(view);
            tv_accuracy = (TextView) view.findViewById(R.id.tv_accuracy);
            tv_latitude = (TextView) view.findViewById(R.id.tv_lati);
            tv_longitude = (TextView) view.findViewById(R.id.tv_long);
        }
    }

    public LocAdapter(List<LocationModel> locationList) {
        this.locationList = locationList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_location, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LocationModel model = locationList.get(position);
        holder.tv_accuracy.setText(model.getAccuracy());
        holder.tv_latitude.setText(model.getLatitude());
        holder.tv_longitude.setText(model.getLongitude());
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }
}
