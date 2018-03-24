package com.adhithya.track.model;

/**
 * Created by ASUS on 3/18/2018.
 */

public class LocationModel {
    double lattitude;
    double longtitude;
    String uid;

    public LocationModel(double lattitude, double longtitude, String uid) {
        this.lattitude = lattitude;
        this.longtitude = longtitude;
        this.uid = uid;
    }

    public double getLattitude() {
        return lattitude;
    }

    public void setLattitude(double lattitude) {
        this.lattitude = lattitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
