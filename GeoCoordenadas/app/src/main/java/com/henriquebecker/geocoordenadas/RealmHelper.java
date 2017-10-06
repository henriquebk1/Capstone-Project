package com.henriquebecker.geocoordenadas;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;


public class RealmHelper {
    private final Realm realm;
    private Context context;
    public RealmHelper(final Realm realm, Context context){
        this.realm = realm;
        this.context = context;
    }

    public void delete(final GeoLocation geoLocation){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                RealmResults<GeoLocation> realmResults = realm.where(GeoLocation.class).findAll();
                GeoLocation location = realmResults.where().equalTo("id",geoLocation.getId()).findFirst();
                location.deleteFromRealm();
            }
        });
    }

    public void create(final GeoLocation geoLocation){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.insert(geoLocation);
            }
        });
    }

    public void update(final GeoLocation geoLocation){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                realm.insertOrUpdate(geoLocation);
            }
        });
    }

    public ArrayList<String> retrieveNames(){
        RealmResults<GeoLocation> realmResults = realm.where(GeoLocation.class).findAll();
        ArrayList<String> names = new ArrayList<>();
        for (GeoLocation geoLocation : realmResults){
            names.add(geoLocation.getName());
        }
        return names;
    }


    public ArrayList<GeoLocation> retrieveAll(){
        RealmResults<GeoLocation> realmResults = realm.where(GeoLocation.class).findAll();
        ArrayList<GeoLocation> geoLocations = new ArrayList<>();
        geoLocations.addAll(realm.copyFromRealm(realmResults));
        return geoLocations;
    }
}
