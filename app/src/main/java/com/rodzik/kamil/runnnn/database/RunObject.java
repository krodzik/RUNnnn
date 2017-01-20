package com.rodzik.kamil.runnnn.database;


import io.realm.RealmList;
import io.realm.RealmObject;

public class RunObject extends RealmObject {
    private String dateTime;
    private double distance;
    private long timeInMilliseconds;
    private RealmList<RealmString> latLngList;
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

    public RealmList<RealmString> getLatLngList() {
        return latLngList;
    }

    public void setLatLngList(RealmList<RealmString> latLngList) {
        this.latLngList = latLngList;
    }

    public RealmList<RealmInt> getHeartRateList() {
        return heartRateList;
    }

    public void setHeartRateList(RealmList<RealmInt> heartRateList) {
        this.heartRateList = heartRateList;
    }
}
