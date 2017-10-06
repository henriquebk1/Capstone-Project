package com.henriquebecker.geocoordenadas;


import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import io.realm.Realm;
import io.realm.RealmChangeListener;

@EFragment(R.layout.fragment_location_tools)
public class LocationToolsFragment extends Fragment {
    @ViewById(R.id.listView_locations)
    ListView locationsList;
    private RealmChangeListener realmListener;
    Realm realm;

    @AfterViews
    void init(){
        realm = ((MainActivity_)getActivity()).realm;
        }

    @Click(R.id.button_create_new_location)
    void createNew(){
        Dialogs.ShowCreateNewDialog(getContext(),getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        RealmHelper realmHelper = new RealmHelper(realm,getContext());
        locationsList.setAdapter(new LocationsAdapter(getContext(),getActivity(),realmHelper.retrieveAll()));
        realmListener = new RealmChangeListener() {
            @Override
            public void onChange(Object o) {
                RealmHelper realmHelper = new RealmHelper(realm,getContext());
                locationsList.setAdapter(new LocationsAdapter(getContext(),getActivity(),realmHelper.retrieveAll()));
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
