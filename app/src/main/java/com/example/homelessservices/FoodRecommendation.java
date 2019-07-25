package com.example.homelessservices;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Fancy on 2017/10/05.
 */

public class FoodRecommendation extends Fragment {

    private CalendarView calendarView;
    private ArrayList<FoodPlace>  foodPlaces, favouriteList;
    private ListView eventView;
    private Double lat,lng;
    private FoodAdapter foodAdapter;
    private String selectedDay,temp_day;
    private TextView select_day;
    private ArrayList<String> userCommentList;
    private HashMap<String, String> holidays;
    private int year, month, day;
    private ImageView info;
    private PopupWindow mPopWindow;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        favouriteList = ReadDataFromFireBase.readOneUserFromFireBase();
        userCommentList = ReadDataFromFireBase.readOneUserCommentsFromFireBase();
        foodPlaces = new ArrayList<>();

        holidays = new HashMap<>();
        holidays.put("1 1","New Year's Day");
        holidays.put("1 26","Australia Day");
        holidays.put("4 25","ANZAC Day");
        holidays.put("12 25","Christmas Day");
        holidays.put("12 26","Boxing Day");

        View view = inflater.inflate(R.layout.fragment_schedule, null);
        getActivity().setTitle("Recommend Food");
        calendarView = (CalendarView) view.findViewById(R.id.calendarView);
        eventView = (ListView) view.findViewById(R.id.event_display);
        info = (ImageView) view.findViewById(R.id.info);

        select_day = (TextView)view.findViewById(R.id.select_day);
        if (selectedDay == null) {
            Calendar calendar = Calendar.getInstance();
            selectedDay = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(calendar.getTime());
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        long milliTime = calendar.getTimeInMillis();
        calendarView.setDate(milliTime,true,true);
        select_day.setText("Recommendations on " + selectedDay + ", " + day + "/" + (month+1) +"/" + year);
        temp_day = (month+1)+" "+day;
        if (holidays.containsKey(temp_day)) {
            selectedDay = "Public_holidays";
            Toast.makeText(getActivity(), "Today is " + holidays.get(temp_day), Toast.LENGTH_SHORT).show();
        }
        lat = 0.0;
        lng = 0.0;
        getCurrentLatLng();

        RecommendationLogic recommendationLogic =
                new RecommendationLogic(lat, lng, selectedDay, favouriteList);
        foodPlaces = recommendationLogic.getRecommendations();
        foodAdapter = new FoodAdapter();
        eventView.setAdapter(foodAdapter);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();
        if (view != null){
            calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(@NonNull CalendarView view, int nyear, int nmonth, int ndayOfMonth) {
                    year = nyear;
                    month = nmonth;
                    day = ndayOfMonth;
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, day);
                    selectedDay = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(calendar.getTime());
                    select_day.setText("Recommendations on " + selectedDay + ", " + day + "/" + (month+1) +"/" + year);
                    temp_day = (month+1)+" "+day;
                    if (holidays.containsKey(temp_day)) {
                        selectedDay = "Public_holidays";
                        Toast.makeText(getActivity(), "Today is " + holidays.get(temp_day), Toast.LENGTH_SHORT).show();
                    }

                    RecommendationLogic recommendationLogic =
                            new RecommendationLogic(lat, lng, selectedDay, favouriteList);
                    foodPlaces = recommendationLogic.getRecommendations();
                    foodAdapter = new FoodAdapter();
                    eventView.setAdapter(foodAdapter);
                }
            });

            eventView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    FoodPlace foodPlace = foodAdapter.getItem(position);

                    PlaceDetails placeDetails = new PlaceDetails();
                    placeDetails.transmitPlaceObjectToPlaceDetailFragment(foodPlace,favouriteList,userCommentList);

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.replace(R.id.fragment_content,placeDetails);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });

            info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopUpWindow();
                }
            });
        }
    }

    private void showPopUpWindow() {
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.recommend_explanation, null);
        mPopWindow = new PopupWindow(contentView);
        mPopWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopWindow.setOutsideTouchable(true);
        mPopWindow.setFocusable(true);
        mPopWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopWindow.showAtLocation(contentView, Gravity.CENTER,0,40);
    }


    private void getCurrentLatLng() {
        String serviceString = Context.LOCATION_SERVICE;
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(serviceString);
        String provider = LocationManager.NETWORK_PROVIDER;
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},12);
        }
        else
        {
            Location location = locationManager.getLastKnownLocation(provider);
            lat = location.getLatitude();
            lng = location.getLongitude();
        }
    }

    /**
     * Request the permission and grant it.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 12)
        {
            if (permissions[0].equals(android.Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[0]== PackageManager.PERMISSION_GRANTED)
            {
                try
                {
                    LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                    Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                    foodAdapter = new FoodAdapter();
                    eventView.setAdapter(foodAdapter);
                }
                catch (SecurityException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                Toast.makeText(getActivity(), "Please grant the permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * This class is used to create the adapter object, so that use for display the object in the recommendation view.
     */
    private class FoodAdapter extends BaseAdapter{

        @Override
        public int getCount()
        {
            return foodPlaces.size();
        }

        @Override
        public FoodPlace getItem(int position)
        {
            return foodPlaces.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        private class ViewHolder
        {
            TextView place_name;
            TextView place_trading;
            TextView getPlace_price;
            TextView place_distance;
            ImageView place_marker_best;
            ImageView place_like_heart;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder = new ViewHolder();
            if (convertView == null)
            {
                convertView = View.inflate(getContext(),R.layout.list_recommendation,null);
                holder.place_name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.place_trading = (TextView) convertView.findViewById(R.id.tv_trading);
                holder.getPlace_price = (TextView) convertView.findViewById(R.id.tv_fee);
                holder.place_distance = (TextView) convertView.findViewById(R.id.tv_distance);
                holder.place_marker_best = (ImageView) convertView.findViewById(R.id.marker_best);
                holder.place_like_heart = (ImageView) convertView.findViewById(R.id.marker_heart);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            if (foodPlaces.get(position).equals(foodPlaces.get(0)))
                holder.place_marker_best.setVisibility(View.VISIBLE);
            else
                holder.place_marker_best.setVisibility(View.INVISIBLE);
            holder.place_name.setText(foodPlaces.get(position).getName());
            if (getThatDayStatus(selectedDay,foodPlaces.get(position)).length() > 4)
                holder.place_trading.setText(getThatDayStatus(selectedDay,foodPlaces.get(position)));

            if (foodPlaces.get(position).getCost().length() >= 40)
            {
                holder.getPlace_price.setText(foodPlaces.get(position).getCost().substring(0,35)+"....");
            }
            else
            {
                holder.getPlace_price.setText(foodPlaces.get(position).getCost());
            }

            for (FoodPlace foodPlace : favouriteList)
            {
                if (foodPlaces.get(position).getName().equals(foodPlace.getName())
                        && foodPlaces.get(position).getSuburb().equals(foodPlace.getSuburb())) {
                    holder.place_like_heart.setVisibility(View.VISIBLE);
                    break;
                }
            }

            if (lat==0.0)
            {
                holder.place_distance.setText("");
            }
            else
            {
                double distance = new Distance().getDistance(lat, lng, Double.parseDouble(foodPlaces.get(position).getLatitude()),
                        Double.parseDouble(foodPlaces.get(position).getLongitude()));
                DecimalFormat df = new DecimalFormat("##0.00");
                String result = df.format(distance/1000);
                holder.place_distance.setText(result +" km");
            }
            return convertView;
        }

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

            return thatDayStatus;
        }
    }
}
