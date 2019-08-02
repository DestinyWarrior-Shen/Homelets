package com.example.homelessservices;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;


public class ServiceCategoryResult extends Fragment implements View.OnClickListener{
    private String category;
    private ListView listView;
    private double lat,lng;
    private MyAdapter adapter;
    private boolean flag,status;
    private ArrayList<Service> locationServices,virtualServices,resultServices,todayList;
    private Menu mMenu;
    private TextView alphabet, distance;
    private ArrayList<Service> distanceServicePlaces;
    private ArrayList<Service> alphabetServicePlaces;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service_category_result, container, false);
        getActivity().setTitle(category);
        setHasOptionsMenu(true);

        lat = 0.0;
        lng = 0.0;

        alphabet = (TextView) view.findViewById(R.id.tv_alphabet);
        distance = (TextView) view.findViewById(R.id.tv_distance);
        listView = (ListView) view.findViewById(R.id.listView);

        getCurrentLatLng();

        if (status)
        {
            alphabet.setBackgroundColor(Color.LTGRAY);
            filterByAlphabet(resultServices);
        }
        else
        {
            resultServices = new ArrayList<>();
            if (flag)
            {
                locationServices = ReadDataFromFireBase.readLocationServiceData();
                getResultList(category,locationServices,resultServices);
            }
            else
            {
                virtualServices = ReadDataFromFireBase.readVirtualServiceData();
                getResultList(category,virtualServices,resultServices);
            }
            alphabet.setBackgroundColor(Color.LTGRAY);
            filterByAlphabet(resultServices);
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        if (view != null)
        {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    Service service = adapter.getItem(position);

                    PlaceDetailsOther placeDetailsOther = new PlaceDetailsOther();
                    placeDetailsOther.transmitPlaceObjectToPlaceDetailFragment(service);

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.replace(R.id.fragment_content,placeDetailsOther);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });

            alphabet.setOnClickListener(this);
            distance.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.tv_alphabet:
                alphabet.setBackgroundColor(Color.LTGRAY);
                distance.setBackgroundColor(Color.TRANSPARENT);
                distance.setBackgroundResource(R.drawable.shape);
                if (mMenu.findItem(R.id.today).getTitle().equals("Today"))
                {
                    filterByAlphabet(resultServices);
                }
                else
                    filterByAlphabet(todayList);
                break;
            case R.id.tv_distance:
                distance.setBackgroundColor(Color.LTGRAY);
                alphabet.setBackgroundColor(Color.TRANSPARENT);
                alphabet.setBackgroundResource(R.drawable.shape);

                if (mMenu.findItem(R.id.today).getTitle().equals("Today"))
                {
                    filterByDistance(resultServices);
                }
                else
                    filterByDistance(todayList);
                break;
        }
    }


    private void filterByDistance(ArrayList<Service> list) {
        distanceServicePlaces = new ArrayList<>(list);
        for (Service service : distanceServicePlaces)
        {
            double distance = new Distance().getDistance(lat, lng, Double.parseDouble(service.getLatitude()),
                    Double.parseDouble(service.getLongitude()));

            service.setDistance(distance);
        }
        Collections.sort(distanceServicePlaces, new Comparator<Service>() {
            @Override
            public int compare(Service o1, Service o2) {
                if (o2.getDistance() > o1.getDistance())
                    return -1;
                if (o2.getDistance() < o1.getDistance())
                    return 1;
                return 0;
            }
        });

        adapter = new MyAdapter(distanceServicePlaces);
        listView.setAdapter(adapter);
    }

    private void filterByAlphabet(ArrayList<Service> list) {
        alphabetServicePlaces = new ArrayList<>(list);
        Collections.sort(alphabetServicePlaces, new Comparator<Service>() {
            @Override
            public int compare(Service o1, Service o2) {
                if (o2.getName().charAt(0) > o1.getName().charAt(0))
                    return -1;
                if (o2.getName().charAt(0) < o1.getName().charAt(0))
                    return 1;
                return 0;
            }
        });

        adapter = new MyAdapter(alphabetServicePlaces);
        listView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.category_result, menu);
        mMenu = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.map)
        {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ServiceMap serviceMap = new ServiceMap();
            //serviceMap.transmitToServiceMapFragment(foodPlaces,favouriteList,userCommentList);
            //serviceMap.sendFilterConditionToMap(selectCheckBoxNameList,categorySelected,subCategorySelected);
            serviceMap.transmitToServiceMapFragment(resultServices,category);
            ft.replace(R.id.fragment_content,serviceMap);
            ft.addToBackStack(null);
            ft.commit();
            return true;
        }

        if (id == R.id.today)
        {
            if (mMenu.findItem(R.id.today).getTitle().equals("Today")) {
                //resetTextViewColor();
                filterTodayService();
                mMenu.findItem(R.id.today).setTitle("All");
                adapter = new MyAdapter(todayList);
                listView.setAdapter(adapter);
            }
            else {
                //resetTextViewColor();
                adapter = new MyAdapter(resultServices);
                listView.setAdapter(adapter);
                mMenu.findItem(R.id.today).setTitle("Today");
            }

        }
        return super.onOptionsItemSelected(item);
    }

    public void filterTodayService() {
        Calendar calendar = Calendar.getInstance();
        String today = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(calendar.getTime());

        todayList = new ArrayList<>(resultServices);
        for(int index = 0; index < todayList.size();)
        {
            if(getThatDayStatus(today,todayList.get(index)).equals("Closed") || getThatDayStatus(today,todayList.get(index)).equals("N/A"))
                todayList.remove(index);
            else
                index++;
        }
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

    public void setListToCategoryResultFragment (ArrayList<Service> serviceArrayList,String category ,boolean status) {
        this.resultServices = serviceArrayList;
//        this.favouriteList = ReadDataFromFireBase.readOneUserFromFireBase();
//        this.userCommentList = ReadDataFromFireBase.readOneUserCommentsFromFireBase();
        this.category = category;
        this.status = status;
    }

    public void sendNumberToResultFragment(String category, boolean flag) {
        this.category = category;
        this.flag = flag;
    }

    private void getResultList(String category,ArrayList<Service> list,ArrayList<Service> resultServices) {
        if (resultServices.size() != 0)
            resultServices.clear();
        for (Service service : list)
        {
            if (service.getCategory_1().equals(category) ||
                    service.getCategory_2().equals(category) ||
                    service.getCategory_3().equals(category) ||
                    service.getCategory_4().equals(category) ||
                    service.getCategory_5().equals(category))
                resultServices.add(service);
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
                    adapter = new MyAdapter(resultServices);
                    listView.setAdapter(adapter);
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

    private class MyAdapter extends BaseAdapter {
        private ArrayList<Service> list;

        public MyAdapter(ArrayList<Service> tmplist) {
            this.list = tmplist;
        }

        @Override
        public int getCount()
        {
            return list.size();
        }

        @Override
        public Service getItem(int position)
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
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();
            if (convertView == null)
            {
                convertView = View.inflate(getContext(),R.layout.list_item_other_service,null);
                holder.place_name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.place_suburb = (TextView) convertView.findViewById(R.id.tv_suburb);
                holder.place_person = (TextView) convertView.findViewById(R.id.tv_who);
                holder.place_cost = (TextView) convertView.findViewById(R.id.tv_fee);
                holder.place_distance = (TextView) convertView.findViewById(R.id.tv_distance);

                convertView.setTag(holder);
            }
            else
                holder = (ViewHolder) convertView.getTag();

            holder.place_name.setText(list.get(position).getName());
            holder.place_suburb.setText(list.get(position).getSuburb());

            if (list.get(position).getWho().length() >= 40)
                holder.place_person.setText(list.get(position).getWho().substring(0,40)+"....");
            else
                holder.place_person.setText(list.get(position).getWho());

            if (list.get(position).getCost().length() >= 40)
                holder.place_cost.setText(list.get(position).getCost().substring(0,40)+"....");
            else
                holder.place_cost.setText(list.get(position).getCost());

            if (lat==0.0)
                holder.place_distance.setText("");
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
}
