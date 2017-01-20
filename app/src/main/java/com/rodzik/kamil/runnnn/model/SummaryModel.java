package com.rodzik.kamil.runnnn.model;


public class SummaryModel {
    private String date;
    private String time;
    private double distance;

    public SummaryModel(String date, String time, double distance) {
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

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
