package com.example.homelessservices;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Eric on 3/10/17.
 */

public class Service extends FoodPlace implements Serializable {
    private String what,name,who,address_1,address_2,suburb,phone,phone2,free_call,website,
            monday,tuesday,wednesday,thursday,friday,saturday,sunday,public_holidays,cost,tram_routes,
            nearest_train_station,bus_routes,category_1,category_2,category_3,category_4,category_5,latitude,longitude;

    private ArrayList<String> attributeList;
    private ArrayList<String> reviewList;
    private double distance;

    public Service() {
        what = "what";
        name = "name";
        who = "who";
        address_1 = "address_1";
        address_2 = "address_2";
        suburb = "suburb";
        phone = "phone";
        phone2 = "phone2";
        free_call = "freeCall";
        website = "website";
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
        category_1 = "category_1";
        category_2 = "category_2";
        category_3 = "category_3";
        category_4 = "category_4";
        category_5 = "category_5";
        latitude = "latitude";
        longitude = "longitude";
        distance = 0.00;
        attributeList = new ArrayList<>();
        reviewList = new ArrayList<>();
    }

    public void addAllAttributeToList() {
        attributeList.add(what);
        attributeList.add(name);
        attributeList.add(who);
        attributeList.add(address_1);
        attributeList.add(address_2);
        attributeList.add(suburb);
        attributeList.add(phone);
        attributeList.add(phone2);
        attributeList.add(free_call);
        attributeList.add(website);
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
        attributeList.add(category_1);
        attributeList.add(category_2);
        attributeList.add(category_3);
        attributeList.add(category_4);
        attributeList.add(category_5);
        attributeList.add(latitude);
        attributeList.add(longitude);


    }

    public ArrayList<String> getAllAttributeFromList() {
        return attributeList;
    }

    @Override
    public String getWhat() {
        return what;
    }

    @Override
    public void setWhat(String what) {
        this.what = what;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getWho() {
        return who;
    }

    @Override
    public void setWho(String who) {
        this.who = who;
    }

    @Override
    public String getAddress_1() {
        return address_1;
    }

    @Override
    public void setAddress_1(String address_1) {
        this.address_1 = address_1;
    }

    @Override
    public String getAddress_2() {
        return address_2;
    }

    @Override
    public void setAddress_2(String address_2) {
        this.address_2 = address_2;
    }

    @Override
    public String getSuburb() {
        return suburb;
    }

    @Override
    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    @Override
    public String getPhone() {
        return phone;
    }

    @Override
    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String getPhone2() {
        return phone2;
    }

    @Override
    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getFree_call() {
        return free_call;
    }

    public void setFree_call(String free_call) {
        this.free_call = free_call;
    }

    @Override
    public String getWebsite() {
        return website;
    }

    @Override
    public void setWebsite(String website) {
        this.website = website;
    }

    @Override
    public String getMonday() {
        return monday;
    }

    @Override
    public void setMonday(String monday) {
        this.monday = monday;
    }

    @Override
    public String getTuesday() {
        return tuesday;
    }

    @Override
    public void setTuesday(String tuesday) {
        this.tuesday = tuesday;
    }

    @Override
    public String getWednesday() {
        return wednesday;
    }

    @Override
    public void setWednesday(String wednesday) {
        this.wednesday = wednesday;
    }

    @Override
    public String getThursday() {
        return thursday;
    }

    @Override
    public void setThursday(String thursday) {
        this.thursday = thursday;
    }

    @Override
    public String getFriday() {
        return friday;
    }

    @Override
    public void setFriday(String friday) {
        this.friday = friday;
    }

    @Override
    public String getSaturday() {
        return saturday;
    }

    @Override
    public void setSaturday(String saturday) {
        this.saturday = saturday;
    }

    @Override
    public String getSunday() {
        return sunday;
    }

    @Override
    public void setSunday(String sunday) {
        this.sunday = sunday;
    }

    @Override
    public String getPublic_holidays() {
        return public_holidays;
    }

    @Override
    public void setPublic_holidays(String public_holidays) {
        this.public_holidays = public_holidays;
    }

    @Override
    public String getCost() {
        return cost;
    }

    @Override
    public void setCost(String cost) {
        this.cost = cost;
    }

    @Override
    public String getTram_routes() {
        return tram_routes;
    }

    @Override
    public void setTram_routes(String tram_routes) {
        this.tram_routes = tram_routes;
    }

    @Override
    public String getNearest_train_station() {
        return nearest_train_station;
    }

    @Override
    public void setNearest_train_station(String nearest_train_station) {
        this.nearest_train_station = nearest_train_station;
    }

    @Override
    public String getBus_routes() {
        return bus_routes;
    }

    @Override
    public void setBus_routes(String bus_routes) {
        this.bus_routes = bus_routes;
    }

    public String getCategory_1() {
        return category_1;
    }

    public void setCategory_1(String category_1) {
        this.category_1 = category_1;
    }

    public String getCategory_2() {
        return category_2;
    }

    public void setCategory_2(String category_2) {
        this.category_2 = category_2;
    }

    public String getCategory_3() {
        return category_3;
    }

    public void setCategory_3(String category_3) {
        this.category_3 = category_3;
    }

    public String getCategory_4() {
        return category_4;
    }

    public void setCategory_4(String category_4) {
        this.category_4 = category_4;
    }

    public String getCategory_5() {
        return category_5;
    }

    public void setCategory_5(String category_5) {
        this.category_5 = category_5;
    }

    @Override
    public String getLatitude() {
        return latitude;
    }

    @Override
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @Override
    public String getLongitude() {
        return longitude;
    }

    @Override
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @Override
    public double getDistance() {
        return distance;
    }

    @Override
    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public ArrayList<String> getReviewList() {
        return reviewList;
    }

    @Override
    public void setReviewList(ArrayList<String> reviewList) {
        this.reviewList = reviewList;
    }
}
