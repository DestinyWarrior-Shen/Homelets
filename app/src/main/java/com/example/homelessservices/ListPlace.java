package com.example.homelessservices;
import android.graphics.Color;

import androidx.fragment.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * This class implement the list view to display all available information. Including search function, select category
 * function.
 */
public class ListPlace extends Fragment
{
    private ListView listView;
    private ArrayList<FoodPlace> foodPlaces,beforeFilterFoodPlaces,favouriteList;
    private ArrayList<String> userCommentList;
    private MyAdapter myAdapter;
    private SearchView mSearchView;
    private Spinner spinner;
    private String itemSelected,categorySelected,subCategorySelected;
    private Double lat,lng;
    private TextView filters;
    private boolean flag = true;
    private ArrayList<String> selectCheckBoxNameList,searchConditionList;
    //private ArrayList<Map<String, Object>> mData;
    //private HashMap<String,FoodPlace> foodPlaceMap;

    /**
     * This class responsible to create view
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_place, container,false);
        getActivity().setTitle("Find Food Service");
        setHasOptionsMenu(true);

        listView = (ListView) view.findViewById(R.id.listView);
        listView.setTextFilterEnabled(true);

        spinner = (Spinner) view.findViewById(R.id.spinner);
        mSearchView = (SearchView) view.findViewById(R.id.search_view);

        filters = (TextView)view.findViewById(R.id.filters);
        filters.setCompoundDrawablesWithIntrinsicBounds(R.drawable.filters,0,0,0);
        filters.setGravity(Gravity.CENTER);

        //beforeFilterFoodPlaces = new ArrayList<>();
        setSearchConditionList();
        beforeFilterFoodPlaces = foodPlaces;
        itemSelected = "";

        initialSpinner(spinner,searchConditionList);

        lat = 0.0;
        lng = 0.0;

        //mData = new ArrayList<Map<String, Object>>();
        //foodPlaceMap = new HashMap<>();

//        for(int i=0; i<foodPlaces.size(); i++)
//        {
//            final Map<String,Object> eachRowItem = new HashMap<>();
//            FoodPlace place = foodPlaces.get(i);
//            eachRowItem.put("latitude",place.getLatitude());
//            eachRowItem.put("name",place.getName());
//            String suburb = place.getSuburb();
//            String who = place.getWho();
//            String fee = place.getCost();
//            if (suburb.equals(""))
//            {
//                eachRowItem.put("suburb","None");
//            }
//            else
//            {
//                eachRowItem.put("suburb",suburb);
//            }
//
//            if (who.equals(""))
//            {
//                eachRowItem.put("who","None");
//            }
//            else
//            {
//                eachRowItem.put("who",who);
//            }
//
//            if (fee.equals(""))
//            {
//                eachRowItem.put("fee","None");
//            }
//            else
//            {
//                eachRowItem.put("fee",fee);
//            }
//
//            mData.add(eachRowItem);
//            foodPlaceMap.put(place.getLatitude(),place);
//        }
//
//        SimpleAdapter adapter = new SimpleAdapter(getActivity(),mData,R.layout.list_item,
//                new String[] {"name","suburb","who","fee"},
//                new int[]{R.id.tv_name,R.id.tv_suburb,R.id.tv_who,R.id.tv_fee});
        //myAdapter = new MyAdapter(getContext(),foodPlaces);
        getCurrentLatLng();
        myAdapter = new MyAdapter();
        listView.setAdapter(myAdapter);
        return view;
    }

    private void setSearchConditionList() {
        searchConditionList = new ArrayList<>();
        searchConditionList.add("All");
        searchConditionList.add("Name");
        searchConditionList.add("Suburb");
        searchConditionList.add("Who");
        searchConditionList.add("Cost");
    }

    /**
     * This method is invoked when this fragment's created, it is responsible to register the click method.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Toast.makeText(getActivity(), "You got " + foodPlaces.size() + " places available" +
                (flag?" today":""), Toast.LENGTH_SHORT).show();

        View view = getView();
        if (view != null)
        {
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                {
                    itemSelected = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent)
                {

                }
            });

            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query)
                {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText)
                {
                    if (TextUtils.isEmpty(newText))
                    {
                        listView.clearTextFilter();
                    }
                    else
                    {
                        listView.setFilterText(newText);
                    }
                    return false;
                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
//                    HashMap<String,Object> oneRowItem = new HashMap<>();
//                    oneRowItem = (HashMap<String,Object>) listView.getAdapter().getItem(position);
//                    FoodPlace fp = foodPlaceMap.get(oneRowItem.get("latitude"));
                    FoodPlace foodPlace = myAdapter.getItem(position);

                    PlaceDetails placeDetails = new PlaceDetails();
                    placeDetails.transmitPlaceObjectToPlaceDetailFragment(foodPlace,favouriteList,userCommentList);

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.replace(R.id.fragment_content,placeDetails);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });

            filters.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FilterBottomSheet filterBottomSheet = new FilterBottomSheet();
                    filterBottomSheet.sendMessageToDialog(selectCheckBoxNameList,categorySelected,subCategorySelected);
                    filterBottomSheet.show(getFragmentManager(),"a");
                }
            });
        }
    }

    public void setListToListPlaceFragment (boolean flag) {
        getFoodPlacesBasedOnDays();
        this.favouriteList = ReadDataFromFireBase.readOneUserFromFireBase();
        this.userCommentList = ReadDataFromFireBase.readOneUserCommentsFromFireBase();
        this.flag = flag;
    }

    /**
     * This method is used to get the object list from previous fragment.
     */
    public void setListToListPlaceFragment (ArrayList<FoodPlace> foodPlaces, boolean flag) {
        this.foodPlaces = foodPlaces;
        this.favouriteList = ReadDataFromFireBase.readOneUserFromFireBase();
        this.userCommentList = ReadDataFromFireBase.readOneUserCommentsFromFireBase();
        this.flag = flag;
    }

    public void sendFilterCondition(ArrayList<String> selectCheckBoxNameList,String categorySelected,String subCategorySelected) {
        this.selectCheckBoxNameList = selectCheckBoxNameList;
        this.categorySelected = categorySelected;
        this.subCategorySelected = subCategorySelected;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Invoke when menu item is selected.
     */
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
            AllPlaces allPlaces = new AllPlaces();
            allPlaces.transmitToAllPlacesFragment(foodPlaces);
            allPlaces.sendFilterConditionToMap(selectCheckBoxNameList,categorySelected,subCategorySelected);
            ft.replace(R.id.fragment_content,allPlaces);
            ft.addToBackStack(null);
            ft.commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void settingSpinnerView(int position, View view, ArrayList<String> list, Spinner spinner) {
        TextView label = (TextView) view.findViewById(R.id.spinner_item_label);
        label.setText(list.get(position));
        if (spinner.getSelectedItemPosition() == position)
        {
            view.setBackgroundColor(getResources().getColor(R.color.colorSelect));
            label.setTextColor(Color.WHITE);
        }
        else
        {
            view.setBackgroundColor(getResources().getColor(R.color.colorSpinner));
        }
    }

    private void initialSpinner(final Spinner spinner, final ArrayList<String> strings) {
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, strings);
//        spinner.setAdapter(adapter);

        ArrayAdapter<String> arrayAdapterCate = new ArrayAdapter<String>(getActivity(),R.layout.search_spinner_checked_text,strings)
        {
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
            {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.search_spinner_item_layout,null);
                settingSpinnerView(position,view,strings,spinner);
                return view;
                //super.getDropDownView(position, convertView, parent);
            }
        };
        arrayAdapterCate.setDropDownViewResource(R.layout.search_spinner_item_layout);
        spinner.setAdapter(arrayAdapterCate);
    }

    /**
     * This class is used to implement the filter function.
     */
    private class MyFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint)
        {
            FilterResults results = new FilterResults();

            ArrayList<FoodPlace> newValues = new ArrayList<>();

            String filterString = constraint.toString().trim().toLowerCase();

            if (TextUtils.isEmpty(filterString))
            {
                newValues = beforeFilterFoodPlaces;
            }
            else
            {
                for (FoodPlace foodPlace : beforeFilterFoodPlaces)
                {
                    if (itemSelected.equals("All"))
                    {
                        if (foodPlace.getName().toLowerCase().contains(filterString) ||
                                foodPlace.getSuburb().toLowerCase().contains(filterString) ||
                                foodPlace.getWho().toLowerCase().contains(filterString) ||
                                foodPlace.getCost().toLowerCase().contains(filterString))
                        {
                            newValues.add(foodPlace);
                        }
                    }
                    else
                    {
                        String attribute = "";
                        String methodName = "get" + itemSelected.toUpperCase().charAt(0) + itemSelected.substring(1);
                        try
                        {
                            Method method = foodPlace.getClass().getMethod(methodName);
                            try
                            {
                                attribute = (String)method.invoke(foodPlace);
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

                        if (attribute.toLowerCase().contains(filterString))
                        {
                            newValues.add(foodPlace);
                        }
                    }
                }
            }

            results.values = newValues;
            results.count = newValues.size();

            return results;
        }

        /**
         * This method is send the result after filtering.
         */
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results)
        {
//            if (((ArrayList<FoodPlace>)results.values).size() !=0)
//                foodPlaces = new ArrayList<FoodPlace>((ArrayList<FoodPlace>)results.values);
//            else
//                foodPlaces = new ArrayList<FoodPlace>(beforeFilterFoodPlaces);
//
//
//            if (results.count > 0)
//            {
//                myAdapter.notifyDataSetChanged();
//            }
//            else
//            {
//                myAdapter.notifyDataSetInvalidated();
//            }
            try
            {
                if (((ArrayList<FoodPlace>) results.values).size() != 0)
                    foodPlaces = new ArrayList<FoodPlace>((ArrayList<FoodPlace>) results.values);
                else
                    foodPlaces = new ArrayList<FoodPlace>(beforeFilterFoodPlaces);


                if (results.count > 0) {
                    myAdapter.notifyDataSetChanged();
                } else {
                    myAdapter.notifyDataSetInvalidated();
                }
            }
            catch (NullPointerException ex)
            {

            }
        }

    }

    /**
     * This class is used to create the adapter object, so that use for display the object in the list view.
     */
    private class MyAdapter extends BaseAdapter implements Filterable {
        private MyFilter mFilter;

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
            TextView place_suburb;
            TextView place_person;
            TextView place_cost;
            TextView place_distance;
            ImageView place_like_heart;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder = new ViewHolder();
            if (convertView == null)
            {
                //LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = View.inflate(getContext(),R.layout.list_item,null);
                holder.place_name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.place_suburb = (TextView) convertView.findViewById(R.id.tv_suburb);
                holder.place_person = (TextView) convertView.findViewById(R.id.tv_who);
                holder.place_cost = (TextView) convertView.findViewById(R.id.tv_fee);
                holder.place_distance = (TextView) convertView.findViewById(R.id.tv_distance);
                holder.place_like_heart = (ImageView) convertView.findViewById(R.id.marker_heart);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.place_name.setText(foodPlaces.get(position).getName());
            holder.place_suburb.setText(foodPlaces.get(position).getSuburb());
            if (foodPlaces.get(position).getWho().length() >= 40)
            {
                holder.place_person.setText(foodPlaces.get(position).getWho().substring(0,35)+"....");
            }
            else
            {
                holder.place_person.setText(foodPlaces.get(position).getWho());
            }

            if (foodPlaces.get(position).getCost().length() >= 40)
            {
                holder.place_cost.setText(foodPlaces.get(position).getCost().substring(0,35)+"....");
            }
            else
            {
                holder.place_cost.setText(foodPlaces.get(position).getCost());
            }

            for (FoodPlace foodPlace : favouriteList)
            {
                if (foodPlaces.get(position).getName().equals(foodPlace.getName())
                        && foodPlaces.get(position).getSuburb().equals(foodPlace.getSuburb()))
                {
                    holder.place_like_heart.setVisibility(View.VISIBLE);
                    break;
                }
                else
                    holder.place_like_heart.setVisibility(View.INVISIBLE);
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

        @Override
        public MyFilter getFilter()
        {
            if (mFilter == null)
            {
                mFilter = new MyFilter();
            }
            return mFilter;
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
                    myAdapter = new MyAdapter();
                    listView.setAdapter(myAdapter);
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
     * Get all open food places opened on today
     * @return the list of food places opened on today
     */
    private void getFoodPlacesBasedOnDays(){
        foodPlaces = ReadDataFromFireBase.readFoodData();
        Calendar calendar = Calendar.getInstance();
        String today = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(calendar.getTime());

        for(int index = 0; index < foodPlaces.size();)
        {
            if(getThatDayStatus(today,foodPlaces.get(index)).equals("Closed") || getThatDayStatus(today,foodPlaces.get(index)).equals("N/A"))
                foodPlaces.remove(index);
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
}






