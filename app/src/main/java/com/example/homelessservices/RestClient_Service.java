package com.example.homelessservices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Eric on 3/10/17.
 */

public class RestClient_Service {

    private static final String BASE_URI = "https://data.melbourne.vic.gov.au/resource/nbdz-yp2p.json";

    /**
     * implement the http service to get the data.
     */
    public static ArrayList<Service> collectService()
    {
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        ArrayList<Service> serviceList = new ArrayList<>();
        try
        {
            url = new URL(BASE_URI);
            //open the connection
            conn = (HttpURLConnection) url.openConnection();
            //set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000); //set the connection method to GET
            conn.setRequestMethod("GET");
            //add http headers to set your response type to json
            //conn.setRequestProperty("X-App-Token","BTyRNiKWarzshNM6Mwph0aETY");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json"); //Read the response
            Scanner inStream = new Scanner(conn.getInputStream()); //read the input steream and store it as string
            while (inStream.hasNextLine())
            {
                textResult += inStream.nextLine();
            }

            JSONArray jsonArray = new JSONArray(textResult);
            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                Service service = new Service();
                service.addAllAttributeToList();
                updateObjectAttributes(service,serviceList,service.getAllAttributeFromList(),jsonObject);
//                    String category = jsonObject.getString("category");
//                    String sub_category = jsonObject.getString("sub_category");
//                    String name = jsonObject.getString("name");
//                    String what = jsonObject.getString("what");
//                    String special_dietary_requirements_1 = jsonObject.getString("special_dietary_requirements_1");
//                    String special_dietary_requirements_2 = jsonObject.getString("special_dietary_requirements_2");
//                    String special_dietary_requirements_3 = jsonObject.getString("special_dietary_requirements_3");
//                    String who = jsonObject.getString("who");
//                    String suburb = jsonObject.getString("suburb");
//                    String enter_via = jsonObject.getString("enter_via");
//                    String contact_person = jsonObject.getString("contact_person");
//                    String phone = jsonObject.getString("phone");
//                    String phone2 = jsonObject.getString("phone2");
//                    String freecall = jsonObject.getString("freecall");
//                    String email = jsonObject.getString("email");
//                    String website = jsonObject.getString("website");
//                    String alternate_website = jsonObject.getString("alternate_website");
//                    String social_media = jsonObject.getString("social_media");
//                    String monday = jsonObject.getString("monday");
//                    String tuesday = jsonObject.getString("tuesday");
//                    String wednesday = jsonObject.getString("wednesday");
//                    String thursday = jsonObject.getString("thursday");
//                    String friday = jsonObject.getString("friday");
//                    String saturday = jsonObject.getString("saturday");
//                    String sunday = jsonObject.getString("sunday");
//                    String public_holidays = jsonObject.getString("public_holidays");
//                    String access = jsonObject.getString("access");
//                    String cost = jsonObject.getString("cost");
//                    String tram_routes = jsonObject.getString("tram_routes");
//                    String nearest_train_station = jsonObject.getString("nearest_train_station");
//                    String bus_routes = jsonObject.getString("bus_routes");
//                    String car_parking = jsonObject.getString("car_parking");
//                    String bicycle_parking = jsonObject.getString("bicycle_parking");
//                    String walking = jsonObject.getString("walking");
//                    String address_1 = jsonObject.getString("address_1");
//                    String address_2 = jsonObject.getString("address_2");
//                    String latitude = jsonObject.getString("latitude");
//                    String longitude = jsonObject.getString("longitude");
//
//                    FoodPlace foodPlace = new FoodPlace(category,sub_category,name,what,special_dietary_requirements_1,
//                            special_dietary_requirements_2,special_dietary_requirements_3,who,suburb,enter_via,contact_person,
//                            phone,phone2,freecall,email,website,alternate_website,social_media,monday,tuesday,wednesday,
//                            thursday,friday,saturday,sunday,public_holidays,access,cost,tram_routes,nearest_train_station,
//                            bus_routes,car_parking,bicycle_parking,walking,address_1,address_2,latitude,longitude);

                //foodPlace.addAllAtributesToList();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            conn.disconnect();
        }

        return serviceList;
    }

    /**
     * Create the food place object, and update the attribute information.
     */
    public static void updateObjectAttributes(Service service,ArrayList<Service> serviceObjectsList,
                                              ArrayList<String> attributeList, JSONObject jsonObject)
            throws InvocationTargetException, IllegalAccessException
    {
        for (int j=0;j<attributeList.size(); j++)
        {
            String attributeValue = "";

            String name = attributeList.get(j);
            if (jsonObject.has(name))
            {
                attributeValue  = jsonObject.optString(name);
            }

            String methodName = "set" + name.toUpperCase().charAt(0) + name.substring(1);

            try
            {
                Method method = service.getClass().getMethod(methodName,String.class);
                method.invoke(service,attributeValue);
            }
            catch (NoSuchMethodException e)
            {
                e.printStackTrace();
            }
        }
            serviceObjectsList.add(service);
    }
}
