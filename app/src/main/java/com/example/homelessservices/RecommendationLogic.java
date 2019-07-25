package com.example.homelessservices;

import android.provider.Settings;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Fancy on 2017/10/05.
 */

public class RecommendationLogic {

    private ArrayList<FoodPlace> foodPlaces, favouriteList, resultList, recommendations;
    private String selectedDay;
    private CustomComparator  comparator;
    private int min, max;
    private double farthest, nearest;

    /**
     * constructor
     * @param lat
     * @param lng
     */
    public RecommendationLogic(double lat, double lng, String selectedDay, ArrayList<FoodPlace> favouriteList) {
        this.foodPlaces = ReadDataFromFireBase.readFoodData();
        this.favouriteList =  favouriteList;
        this.selectedDay = selectedDay;
        comparator = new CustomComparator(lat, lng);
        this.resultList = new ArrayList<>();
        this.recommendations = new ArrayList<>();
    }

    public ArrayList<FoodPlace> getRecommendations()
    {
        //nearest and best
        addNearestAndBest();

        //nearest
        rankFoodPlaceByDistance(foodPlaces, false);
        if (resultList.size() > 0 &&
                (!isIncluded(recommendations, resultList.get(0))))
            recommendations.add(resultList.get(0));

        //nearest and most favorite
        if (favouriteList.size() > 0) {
            rankFoodPlaceByDistance(favouriteList, false);
            if (resultList.size() > 0 &&
                    (!isIncluded(recommendations, resultList.get(0))))
                recommendations.add(resultList.get(0));
        }

        return recommendations;
    }

    /**
     * Nearest
     */
    private void rankFoodPlaceByDistance(ArrayList<FoodPlace> places, boolean flag) {
        resultList.clear();
        getFoodPlacesBasedOnDays(places);
        Collections.sort(resultList, comparator);

        if (flag && resultList.size() > 0) {
            this.nearest = comparator.getDistance(Double.parseDouble(resultList.get(0).getLatitude()),
                    Double.parseDouble(resultList.get(0).getLongitude()));

            int index = resultList.size() - 1;
            this.farthest = comparator.getDistance(Double.parseDouble(resultList.get(index).getLatitude()),
                    Double.parseDouble(resultList.get(index).getLongitude()));
        }
    }

    /**
     * Nearest and Best
     */
    private void addNearestAndBest(){
        double min = Double.MAX_VALUE;
        FoodPlace fp = new FoodPlace();

        rankFoodPlaceByDistance(foodPlaces, true);
        for (int i = 0; i< resultList.size(); i++){
            if (getRank(resultList.get(i)) <  min) {
                min = getRank(resultList.get(i));
                fp = resultList.get(i);
            }
        }
        if (!isIncluded(recommendations, fp))
            recommendations.add(fp);
    }

    /**
     * Used to get rank score of each food place
     * @param service
     * @return
     */
    public double getRank(FoodPlace service) {
        double preferenceScore;
        double distanceScore;

        double distScore = comparator.getDistance(Double.parseDouble(service.getLatitude()),
                Double.parseDouble(service.getLongitude()));

        distanceScore = normalized(this.nearest, this.farthest, distScore);
        preferenceScore = 1 - normalized(this.min, this.max, service.getAddTimes());

        double rank = (0.7 * distanceScore) + (0.3 * preferenceScore);
        return rank;
    }

    /**
     * Get all open food places on specific days
     */
    private ArrayList<FoodPlace> getFoodPlacesBasedOnDays(ArrayList<FoodPlace> foodPlaces) {
        for(FoodPlace foodPlace: foodPlaces)
        {
            if((!getThatDayStatus(selectedDay,foodPlace).equals("Closed")) && (!getThatDayStatus(selectedDay,foodPlace).equals("N/A"))
                    && (!isIncluded(resultList,foodPlace)))
                resultList.add(foodPlace);
        }
        return resultList;
    }

    /**
     *
     */
    private boolean isIncluded(ArrayList<FoodPlace> foods, FoodPlace fp){
        boolean flag = false;
        for (FoodPlace foodPlace : foods)
        {
            if (foodPlace.getName().equals(fp.getName())
                    && foodPlace.getSuburb().equals(fp.getSuburb())) {
                flag = true;
                break;
            }
        }
        return flag;
    }
    /**
     * @param text
     * @param foodPlace
     * @return
     */
    private String getThatDayStatus(String text, FoodPlace foodPlace) {
        String methodName = "get" + text;
        String thatDayStatus = "";
        try
        {
            Method method = foodPlace.getClass().getMethod(methodName);
            try
            {
                thatDayStatus = (String)method.invoke(foodPlace);
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
            catch (InvocationTargetException e)
            {
                e.printStackTrace();
            }
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        if (thatDayStatus.length() >= 6)
            thatDayStatus = thatDayStatus.substring(0,6);
        return thatDayStatus;
    }

    /**
     * normalized data in order to make the score within 0 - 1
     * @param min
     * @param max
     * @param x
     * @return
     */
    private double normalized(double min, double max, double x) {
        double normalized;

        normalized = (x - min) / (max - min);

        return normalized;
    }

    /**
     * find the maximum and minimum times that a food place has been added to favorite
     */
    private void findMinAndMax() {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        for (int i = 0; i < this.resultList.size(); i++) {
            if (this.resultList.get(i).getAddTimes() < min) {
                min = this.resultList.get(i).getAddTimes();
            }
        }

        for (int i = 0; i < this.foodPlaces.size(); i++) {
            if (this.resultList.get(i).getAddTimes() > max) {
                max = this.resultList.get(i).getAddTimes();
            }
        }

        this.max = max;
        this.min = min;
    }
}

/**
 * customize a comparator used to compare the distance
 * @author Chen Pan
 */
class CustomComparator implements Comparator<FoodPlace> {
    double startLat;
    double startLon;

    public CustomComparator(double lat, double lon) {
        this.startLat = lat;
        this.startLon = lon;
    }

    @Override
    public int compare(FoodPlace o1, FoodPlace o2) {
        double lat1 = Double.parseDouble(o1.getLatitude());
        double lon1 = Double.parseDouble(o1.getLongitude());

        double lat2 = Double.parseDouble(o2.getLatitude());
        double lon2 = Double.parseDouble(o2.getLongitude());

        return getDistance(lat1, lon1).compareTo(getDistance(lat2, lon2));
    }

    public Double getDistance(double endLat, double endLon) {
        final int earthRadius = 6371;

        double latDistance = Math.toRadians(endLat - this.startLat);
        double lonDistance = Math.toRadians(endLon - this.startLon);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(this.startLat))
                * Math.cos(Math.toRadians(endLat)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c * 1000;

        return distance;
    }
}