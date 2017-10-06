package com.henriquebecker.geocoordenadas;


import android.content.DialogInterface;
import android.location.Location;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;


/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_location)
public class LocationFragment extends Fragment{
    @ViewById(R.id.location_latitude)
    TextView textLatitude;
    @ViewById(R.id.location_longitude)
    TextView textLongitude;
    @ViewById(R.id.location_elevation)
    TextView textElevation;
    @ViewById(R.id.location_speed)
    TextView textSpeed;
    @ViewById(R.id.location_accurancy)
    TextView textAccurancy;
    @ViewById(R.id.location_progressBar)
    ProgressBar progressBar;

    private Location mCurrentLocation;

    @AfterViews
    void init() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void updateUI(Location location) {
        mCurrentLocation = location;
        String coord[] = Utils.formatCoordinates(location, getContext());
        textLatitude.setText(coord[0]);
        textLongitude.setText(coord[1]);
        textElevation.setText(Utils.formatSize(location.getAltitude(),getContext()));
        textAccurancy.setText(Utils.formatSize(location.getAccuracy(),getContext()));
        textSpeed.setText(Utils.formatSpeed(location.getSpeed(),getContext()));
        if(location.getAccuracy() > 500.0F){
            progressBar.setVisibility(View.VISIBLE);
        }
        else{
            progressBar.setVisibility(View.GONE);
        }
    }


    @Click(R.id.location_save)
    void saveLocation(){
        Dialogs.ShowSaveDialog(getContext(),getActivity(),mCurrentLocation);
    }
}
