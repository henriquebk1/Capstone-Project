package com.henriquebecker.geocoordenadas;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Henrique Becker on 29/09/2017.
 */

public class LocationsAdapter extends ArrayAdapter<GeoLocation> {
    Activity activity;
    public LocationsAdapter(@NonNull Context context, Activity activity, ArrayList<GeoLocation> locations) {
        super(context, 0, locations);
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GeoLocation location = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.location_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.name.setText(location.getName());
        viewHolder.latitude.setText(Utils.formatLatitude(location.getLat(),getContext()));
        viewHolder.longitude.setText(Utils.formatLongitude(location.getLng(),getContext()));
        viewHolder.edit.setTag(location);
        viewHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeoLocation geoLocation = (GeoLocation) v.getTag();
                Dialogs.ShowEditDialog(getContext(), activity, geoLocation);
            }
        });
        viewHolder.delete.setTag(location);
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeoLocation geoLocation = (GeoLocation) v.getTag();
                Dialogs.ShowDeleteDialog(getContext(), activity, geoLocation);
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        TextView name;
        TextView latitude;
        TextView longitude;
        ImageView edit;
        ImageView delete;
        public ViewHolder(View view){
            name = (TextView)view.findViewById(R.id.list_item_name);
            latitude = (TextView)view.findViewById(R.id.list_item_latitude);
            longitude = (TextView)view.findViewById(R.id.list_item_longitude);
            edit = (ImageView)view.findViewById(R.id.list_item_edit);
            delete = (ImageView)view.findViewById(R.id.list_item_delete);
        }
    }
}
