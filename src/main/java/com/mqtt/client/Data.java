package com.mqtt.client;

import java.util.Date;

public class Data {
    private String IMEI;
    private int GPS;
    private double Lng;
    private double Lat;
    private boolean IsKeyOn;
    private int Heading;
    private int Unplugged;
    private double Fuel;
    private String Timestamp;
    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Data(){}
    public Data(String IMEI, int GPS, double lng, double lat, boolean isKeyOn, int heading, int unplugged, double fuel, String timestamp) {
        this.IMEI = IMEI;
        this.GPS = GPS;
        Lng = lng;
        Lat = lat;
        IsKeyOn = isKeyOn;
        Heading = heading;
        Unplugged = unplugged;
        Fuel = fuel;
        Timestamp = timestamp;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public int getGPS() {
        return GPS;
    }

    public void setGPS(int GPS) {
        this.GPS = GPS;
    }

    public double getLng() {
        return Lng;
    }

    public void setLng(double lng) {
        Lng = lng;
    }

    public double getLat() {
        return Lat;
    }

    public void setLat(double lat) {
        Lat = lat;
    }

    public boolean isKeyOn() {
        return IsKeyOn;
    }

    public void setKeyOn(boolean keyOn) {
        IsKeyOn = keyOn;
    }

    public int getHeading() {
        return Heading;
    }

    public void setHeading(int heading) {
        Heading = heading;
    }

    public int getUnplugged() {
        return Unplugged;
    }

    public void setUnplugged(int unplugged) {
        Unplugged = unplugged;
    }

    public double getFuel() {
        return Fuel;
    }

    public void setFuel(double fuel) {
        Fuel = fuel;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Data{" +
                "IMEI='" + IMEI + '\'' +
                ", GPS=" + GPS +
                ", Lng=" + Lng +
                ", Lat=" + Lat +
                ", IsKeyOn=" + IsKeyOn +
                ", Heading=" + Heading +
                ", Unplugged=" + Unplugged +
                ", Fuel=" + Fuel +
                ", Timestamp='" + Timestamp + '\'' +
                ", date=" + date +
                '}';
    }
}
