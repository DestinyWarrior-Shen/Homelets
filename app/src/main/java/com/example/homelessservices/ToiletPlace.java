package com.example.homelessservices;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Fancy on 2017/09/02.
 */

public class ToiletPlace implements Serializable
{
    private String lat,lon,name, female, male, wheelchair, operator, baby_facil;

    private ArrayList<String> attributeList;

    public ToiletPlace()
    {
        this.lat = "lat";
        this.lon = "lon";
        this.name = "name";
        this.female = "female";
        this.male = "male";
        this.wheelchair = "wheelchair";
        this.operator = "operator";
        this.baby_facil = "baby_facil";
        this.attributeList = new ArrayList<>();
    }


    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFemale() {
        return female;
    }

    public void setFemale(String female) {
        this.female = female;
    }

    public String getMale() {
        return male;
    }

    public void setMale(String male) {
        this.male = male;
    }

    public String getWheelchair() {
        return wheelchair;
    }

    public void setWheelchair(String wheelchair) {
        this.wheelchair = wheelchair;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getBaby_facil() {
        return baby_facil;
    }

    public void setBaby_facil(String baby_facil) {
        this.baby_facil = baby_facil;
    }

    public void addAllAttributesToList() {
        attributeList.add(lat);
        attributeList.add(lon);
        attributeList.add(name);
        attributeList.add(female);
        attributeList.add(male);
        attributeList.add(wheelchair);
        attributeList.add(operator);
        attributeList.add(baby_facil);
        ;
    }

    public ArrayList<String> getAllAttributesToList()
    {
        return attributeList;
    }
}
