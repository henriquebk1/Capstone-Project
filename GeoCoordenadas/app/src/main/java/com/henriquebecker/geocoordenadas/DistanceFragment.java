package com.henriquebecker.geocoordenadas;


import android.location.Location;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmChangeListener;


/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_distance)
public class DistanceFragment extends Fragment {
    @ViewById(R.id.dGeoLocation1)
    Spinner spinnerPlace1;
    @ViewById(R.id.dLat1)
    TextView textLat1;
    @ViewById(R.id.dLng1)
    TextView textLng1;
    @ViewById(R.id.dAcc1)
    TextView textAcc1;
    @ViewById(R.id.dGeoLocation2)
    Spinner spinnerPlace2;
    @ViewById(R.id.dLat2)
    TextView textLat2;
    @ViewById(R.id.dLng2)
    TextView textLng2;
    @ViewById(R.id.dAcc2)
    TextView textAcc2;
    @ViewById(R.id.dDistance)
    TextView textDistance;
    @ViewById(R.id.dDistance1)
    TextView textDistance1;
    @ViewById(R.id.maxError)
    TextView textMaxError;

    private Location mCurrentLocation;

    ArrayList<String> locationsName;
    ArrayList<GeoLocation> geoLocations;
    ArrayAdapter<String> adapter;
    private Realm realm;
    private RealmChangeListener realmListener;

    int position1=-1,position2=-1;

    @AfterViews
    void init(){
        mCurrentLocation = ((MainActivity_)getActivity()).getCurrentLocation();
        realm = ((MainActivity_)getActivity()).realm;
        RealmHelper helper=new RealmHelper(realm,getContext());
        geoLocations=helper.retrieveAll();
        locationsName=helper.retrieveNames();
        locationsName.add(0,getString(R.string.current_location));
        adapter= new ArrayAdapter<>(getContext(),R.layout.spinner_item, locationsName);
        spinnerPlace1.setAdapter(adapter);
        spinnerPlace2.setAdapter(adapter);
        spinnerPlace1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                position1=position;
                updateUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                position1=-1;
                updateUI();
            }
        });
        spinnerPlace2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                position2=position;
                updateUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                position2=-1;
                updateUI();
            }
        });
    }

    private void updateUI(){
        LatLng place1=null, place2=null;
        float maxError=0.0f;
        if(position1==0 && mCurrentLocation != null){//current position
            String coordinates[] = Utils.formatCoordinates(mCurrentLocation,getContext());
            textLat1.setText(coordinates[0]);
            textLng1.setText(coordinates[1]);
            textAcc1.setText(Utils.formatSize(mCurrentLocation.getAccuracy(),getContext()));
            maxError = mCurrentLocation.getAccuracy();
            place1 = new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
        }
        else if(position1>0 && position1 <= geoLocations.size()){
            GeoLocation geoLocation = geoLocations.get(position1-1);
            textLat1.setText(Utils.formatLatitude(geoLocation.getLat(),getContext()));
            textLng1.setText(Utils.formatLongitude(geoLocation.getLng(),getContext()));
            textAcc1.setText(Utils.formatSize(geoLocation.getAccuracy(),getContext()));
            maxError = geoLocation.getAccuracy();
            place1 = new LatLng(geoLocation.getLat(),geoLocation.getLng());
        }
        else {//invalid position
            textLat1.setText(getString(R.string.empty_text));
            textLng1.setText(getString(R.string.empty_text));
            textAcc1.setText(getString(R.string.empty_text));
        }
        if(position2==0 && mCurrentLocation != null){//current position
            String coordinates[] = Utils.formatCoordinates(mCurrentLocation,getContext());
            textLat2.setText(coordinates[0]);
            textLng2.setText(coordinates[1]);
            textAcc2.setText(Utils.formatSize(mCurrentLocation.getAccuracy(),getContext()));
            maxError += mCurrentLocation.getAccuracy();
            place2 = new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
        }
        else if(position2>0 && position2 <= geoLocations.size()){
            GeoLocation geoLocation = geoLocations.get(position2-1);
            textLat2.setText(Utils.formatLatitude(geoLocation.getLat(),getContext()));
            textLng2.setText(Utils.formatLongitude(geoLocation.getLng(),getContext()));
            textAcc2.setText(Utils.formatSize(geoLocation.getAccuracy(),getContext()));
            maxError += geoLocation.getAccuracy();
            place2 = new LatLng(geoLocation.getLat(),geoLocation.getLng());
        }
        else {//invalid position
            textLat2.setText(getString(R.string.empty_text));
            textLng2.setText(getString(R.string.empty_text));
            textAcc2.setText(getString(R.string.empty_text));
        }
        if(place1 == null || place2 == null){
            textDistance.setText(getString(R.string.empty_text));
            textDistance1.setText(getString(R.string.empty_text));
            textMaxError.setText(getString(R.string.empty_text));
        }
        else {
            float distance[] = new float[3];
            Location.distanceBetween(place1.latitude,place1.longitude,
                    place2.latitude,place2.longitude,distance);
            textDistance.setText(Utils.formatSize(distance[0],getContext()));
            textDistance1.setText(Utils.formatMegaSize(distance[0],getContext()));
            if(distance[0]>0.0f) {
                textMaxError.setText(getString(R.string.max_error,Utils.formatSize(maxError, getContext())));
            }
            else {
                textMaxError.setText(getString(R.string.max_error,Utils.formatSize(0.0f, getContext())));
            }
        }
    }

    public void updateCurrentLocation(Location location) {
        mCurrentLocation = location;
        updateUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        realmListener = new RealmChangeListener() {
            @Override
            public void onChange(Object o) {
                init();
            }
        };
        realm.addChangeListener(realmListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        realm.removeChangeListener(realmListener);
    }
}
