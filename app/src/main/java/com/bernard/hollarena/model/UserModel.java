package com.bernard.hollarena.model;


import java.util.ArrayList;

public class UserModel {
    String userName,key;
    ArrayList<String> interestList;
    double latitude,longitude;
    double timeInMillis;

    public double getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(double timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    public UserModel(){}

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public UserModel(String userName, ArrayList list) {
        this.userName = userName;
        this.interestList = list;

    }


    public ArrayList<String> getInterestList() {
        return interestList;
    }

    public void setInterestList(ArrayList<String> interestList) {
        this.interestList = interestList;
    }

    
}
