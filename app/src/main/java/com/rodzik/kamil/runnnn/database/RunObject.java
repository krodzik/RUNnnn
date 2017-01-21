package com.rodzik.kamil.runnnn.database;


import io.realm.RealmList;
import io.realm.RealmObject;

public class RunObject extends RealmObject {
    private String dateTime;
    private double distance;
    private long timeInMilliseconds;
    private String encodedLatLngList;
    private RealmList<RealmInt> heartRateList;

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public long getTimeInMilliseconds() {
        return timeInMilliseconds;
    }

    public void setTimeInMilliseconds(long timeInMilliseconds) {
        this.timeInMilliseconds = timeInMilliseconds;
    }

    public String getEncodedLatLngList() {
        return encodedLatLngList;
    }

    public void setEncodedLatLngList(String encodedLatLngList) {
        this.encodedLatLngList = encodedLatLngList;
    }

    public RealmList<RealmInt> getHeartRateList() {
        return heartRateList;
    }

    public void setHeartRateList(RealmList<RealmInt> heartRateList) {
        this.heartRateList = heartRateList;
    }
}
