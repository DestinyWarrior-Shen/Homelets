package com.example.homelessservices;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class Favourite extends Fragment implements View.OnClickListener
{
    private TextView textView,time,alphabet,distance;
    private ListView listview;
    private CustomAdapter customAdapter;
    private ArrayList<FoodPlace> favouritePlaces,foodPlaces,distanceFoodPlaces,alphabetFoodPlaces;
    private ArrayList<String> userCommentList;
    private Double lat,lng;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);
        getActivity().setTitle("Favourite Food");
        textView = (TextView) view.findViewById(R.id.tv_favourite_number);
        time = (TextView) view.findViewById(R.id.tv_time);
        alphabet = (TextView) view.findViewById(R.id.tv_alphabet);
        distance = (TextView) view.findViewById(R.id.tv_distance);

        listview = (ListView) view.findViewById(R.id.listView_favourite);

        time.setBackgroundColor(Color.LTGRAY);

        lat = 0.0;
        lng = 0.0;

        foodPlaces = ReadDataFromFireBase.readFoodData();
        favouritePlaces = ReadDataFromFireBase.readOneUserFromFireBase();
        userCommentList = ReadDataFromFireBase.readOneUserCommentsFromFireBase();
        textView.setText(String.valueOf(favouritePlaces.size())+" place in your favourite list");

        getCurrentLatLng();
        //Collections.reverse(favouritePlaces);
        customAdapter = new CustomAdapter(favouritePlaces);
        listview.setAdapter(customAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        if (view != null)
        {
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> arg0, View view, int position, long id)
                {
                    FoodPlace foodPlace = new FoodPlace();

                    for (FoodPlace fp : foodPlaces)
                    {
                        if (fp.getLongitude().equals(customAdapter.getItem(position).getLongitude()))
                        {
                            foodPlace = fp;
                            break;
                        }
                    }

                    PlaceDetails placeDetails = new PlaceDetails();
                    placeDetails.transmitPlaceObjectToPlaceDetailFragment(foodPlace,favouritePlaces,userCommentList);

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.replace(R.id.fragment_content,placeDetails);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });

            time.setOnClickListener(this);
            distance.setOnClickListener(this);
            alphabet.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.tv_time:
                resetTextViewColor();
                time.setBackgroundColor(Color.LTGRAY);
                alphabet.setBackgroundResource(R.drawable.shape);
                distance.setBackgroundResource(R.drawable.shape);
                customAdapter = new CustomAdapter(favouritePlaces);
                listview.setAdapter(customAdapter);
                break;
            case R.id.tv_alphabet:
                resetTextViewColor();
                alphabet.setBackgroundColor(Color.LTGRAY);
                time.setBackgroundResource(R.drawable.shape);
                distance.setBackgroundResource(R.drawable.shape);
                filterByAlphabet();
                break;
            case R.id.tv_distance:
                resetTextViewColor();
                distance.setBackgroundColor(Color.LTGRAY);
                time.setBackgroundResource(R.drawable.shape);
                alphabet.setBackgroundResource(R.drawable.shape);
                filterByDistance();
                break;
        }
    }

    private void resetTextViewColor() {
        time.setBackgroundColor(Color.TRANSPARENT);
        alphabet.setBackgroundColor(Color.TRANSPARENT);
        distance.setBackgroundColor(Color.TRANSPARENT);
    }

    private void filterByDistance() {
        distanceFoodPlaces = new ArrayList<>(favouritePlaces);
        for (FoodPlace foodPlace : distanceFoodPlaces)
        {
            double distance = new Distance().getDistance(lat, lng, Double.parseDouble(foodPlace.getLatitude()),
                    Double.parseDouble(foodPlace.getLongitude()));
            foodPlace.setDistance(distance);
        }
        Collections.sort(distanceFoodPlaces, new Comparator<FoodPlace>() {
            @Override
            public int compare(FoodPlace o1, FoodPlace o2) {
                if (o2.getDistance() > o1.getDistance())
                    return -1;
                if (o2.getDistance() < o1.getDistance())
                    return 1;
                return 0;
            }
        });

        customAdapter = new CustomAdapter(distanceFoodPlaces);
        listview.setAdapter(customAdapter);
    }

    private void filterByAlphabet() {
        alphabetFoodPlaces = new ArrayList<>(favouritePlaces);
        Collections.sort(alphabetFoodPlaces, new Comparator<FoodPlace>() {
            @Override
            public int compare(FoodPlace o1, FoodPlace o2) {
                if (o2.getName().charAt(0) > o1.getName().charAt(0))
                    return -1;
                if (o2.getName().charAt(0) < o1.getName().charAt(0))
                    return 1;
                return 0;
            }
        });

        customAdapter = new CustomAdapter(alphabetFoodPlaces);
        listview.setAdapter(customAdapter);
    }

    private class CustomAdapter extends BaseAdapter {
        private ArrayList<FoodPlace> list;

        public CustomAdapter(ArrayList<FoodPlace> tmpList) {
            this.list = tmpList;
        }
        @Override
        public int getCount()
        {
            return list.size();
        }

        @Override
        public FoodPlace getItem(int position)
        {
            return list.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        private class ViewHolder
        {
            TextView place_name;
            TextView place_suburb;
            TextView place_person;
            TextView place_cost;
            TextView place_distance;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder = new ViewHolder();
            if (convertView == null)
            {
                convertView = View.inflate(getContext(),R.layout.list_item, null);
                holder.place_name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.place_suburb = (TextView) convertView.findViewById(R.id.tv_suburb);
                holder.place_person = (TextView) convertView.findViewById(R.id.tv_who);
                holder.place_cost = (TextView) convertView.findViewById(R.id.tv_fee);
                holder.place_distance = (TextView) convertView.findViewById(R.id.tv_distance);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.place_name.setText(list.get(position).getName());
            holder.place_suburb.setText(list.get(position).getSuburb());
            if (list.get(position).getWho().length() >= 40)
            {
                holder.place_person.setText(list.get(position).getWho().substring(0,35)+"....");
            }
            else
            {
                holder.place_person.setText(list.get(position).getWho());
            }

            if (list.get(position).getCost().length() >= 40)
            {
                holder.place_cost.setText(list.get(position).getCost().substring(0,35)+"....");
            }
            else
            {
                holder.place_cost.setText(list.get(position).getCost());
            }

            if (lat==0.0)
            {
                holder.place_distance.setText("");
            }
            else
            {
                double distance = new Distance().getDistance(lat, lng, Double.parseDouble(list.get(position).getLatitude()),
                        Double.parseDouble(list.get(position).getLongitude()));
                DecimalFormat df = new DecimalFormat("##0.00");
                String result = df.format(distance/1000);
                holder.place_distance.setText(result +" km");
            }

            return convertView;
        }
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
                    customAdapter = new CustomAdapter(favouritePlaces);
                    listview.setAdapter(customAdapter);
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
}
