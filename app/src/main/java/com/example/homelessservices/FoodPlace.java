package com.example.homelessservices;

import com.google.android.gms.maps.model.LatLng;
//import com.google.maps.android.clustering.ClusterItem;

import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by Eric on 17/8/17.
 * Food place object class.
 */

public class FoodPlace implements Serializable
{
    private String category,sub_category,name,what,who,suburb,phone,phone2,website,
            alternate_website,monday,tuesday,wednesday,thursday,friday,saturday,sunday,public_holidays,
            cost,tram_routes,nearest_train_station,bus_routes,address_1,address_2,latitude,longitude;

    private int addTimes;
    private double distance;
    private ArrayList<String> attributeList;
    private ArrayList<String> reviewList;

    public FoodPlace() {
        category = "category";
        sub_category = "sub_category";
        name = "name";
        what = "what";
        who = "who";
        suburb = "suburb";
        phone = "phone";
        phone2 = "phone2";
        website = "website";
        alternate_website = "alternate_website";
        monday = "monday";
        tuesday = "tuesday";
        wednesday = "wednesday";
        thursday = "thursday";
        friday = "friday";
        saturday = "saturday";
        sunday = "sunday";
        public_holidays = "public_holidays";
        cost = "cost";
        tram_routes = "tram_routes";
        nearest_train_station = "nearest_train_station";
        bus_routes = "bus_routes";
        address_1 = "address_1";
        address_2 = "address_2";
        latitude = "latitude";
        longitude = "longitude";
        distance = 0.00;
        addTimes = 0;
        attributeList = new ArrayList<>();
        reviewList = new ArrayList<>();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSub_category() {
        return sub_category;
    }

    public void setSub_category(String sub_category) {
        this.sub_category = sub_category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWhat() {
        return what;
    }

    public void setWhat(String what) {
        this.what = what;
    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getAlternate_website() {
        return alternate_website;
    }

    public void setAlternate_website(String alternate_website) {
        this.alternate_website = alternate_website;
    }

    public String getMonday() {
        return monday;
    }

    public void setMonday(String monday) {
        this.monday = monday;
    }

    public String getTuesday() {
        return tuesday;
    }

    public void setTuesday(String tuesday) {
        this.tuesday = tuesday;
    }

    public String getWednesday() {
        return wednesday;
    }

    public void setWednesday(String wednesday) {
        this.wednesday = wednesday;
    }

    public String getThursday() {
        return thursday;
    }

    public void setThursday(String thursday) {
        this.thursday = thursday;
    }

    public String getFriday() {
        return friday;
    }

    public void setFriday(String friday) {
        this.friday = friday;
    }

    public String getSaturday() {
        return saturday;
    }

    public void setSaturday(String saturday) {
        this.saturday = saturday;
    }

    public String getSunday() {
        return sunday;
    }

    public void setSunday(String sunday) {
        this.sunday = sunday;
    }

    public String getPublic_holidays() {
        return public_holidays;
    }

    public void setPublic_holidays(String public_holidays) {
        this.public_holidays = public_holidays;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getTram_routes() {
        return tram_routes;
    }

    public void setTram_routes(String tram_routes) {
        this.tram_routes = tram_routes;
    }

    public String getNearest_train_station() {
        return nearest_train_station;
    }

    public void setNearest_train_station(String nearest_train_station) {
        this.nearest_train_station = nearest_train_station;
    }

    public String getBus_routes() {
        return bus_routes;
    }

    public void setBus_routes(String bus_routes) {
        this.bus_routes = bus_routes;
    }

    public String getAddress_1() {
        return address_1;
    }

    public void setAddress_1(String address_1) {
        this.address_1 = address_1;
    }

    public String getAddress_2() {
        return address_2;
    }

    public void setAddress_2(String address_2) {
        this.address_2 = address_2;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void addAllAttributesToList() {
        attributeList.add(category);
        attributeList.add(sub_category);
        attributeList.add(name);
        attributeList.add(what);
        attributeList.add(who);
        attributeList.add(suburb);
        attributeList.add(phone);
        attributeList.add(phone2);
        attributeList.add(website);
        attributeList.add(alternate_website);
        attributeList.add(monday);
        attributeList.add(tuesday);
        attributeList.add(wednesday);
        attributeList.add(thursday);
        attributeList.add(friday);
        attributeList.add(saturday);
        attributeList.add(sunday);
        attributeList.add(public_holidays);
        attributeList.add(cost);
        attributeList.add(tram_routes);
        attributeList.add(nearest_train_station);
        attributeList.add(bus_routes);
        attributeList.add(address_1);
        attributeList.add(address_2);
        attributeList.add(latitude);
        attributeList.add(longitude);
    }

    public ArrayList<String> getAllAttributesToList()
    {
        return attributeList;
    }

    public Integer getAddTimes() {
        return addTimes;
    }

    public void setAddTimes(int addTimes) {
        this.addTimes = addTimes;
    }

    public void addOneTimes()
    {
        addTimes += 1;
    }

    public ArrayList<String> getReviewList() {
        return reviewList;
    }

    public void setReviewList(ArrayList<String> reviewList) {
        this.reviewList = reviewList;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
