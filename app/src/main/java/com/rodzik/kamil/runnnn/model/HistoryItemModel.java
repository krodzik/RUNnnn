package com.rodzik.kamil.runnnn.model;


public class HistoryItemModel {
    private String date;
    private String time;
    private String distance;

    public HistoryItemModel(String date, String time, String distance) {
        this.date = date;
        this.time = time;
        this.distance = distance;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
