package com.henriquebecker.geocoordenadas;


import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class GeoLocation extends RealmObject {
    @PrimaryKey
    private String id;
    private String name;
    private double lat;
    private double lng;
    private float accuracy;

    public GeoLocation() {
        this.id = UUID.randomUUID().toString();
    }

    public GeoLocation(String name, double lat, double lng, float accuracy) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.accuracy = accuracy;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
