package com.example.homelessservices;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;


public class PlaceDetailsOther extends Fragment implements OnMapReadyCallback, View.OnClickListener,
                                                            RatingBar.OnRatingBarChangeListener{

    private GoogleMap mGoogleMap;
    private MapView mMapView;
    private View mView;
    private Service service;
    private TextView placeName,suburb,category,address, navigation, description, phoneNo, website,
            monday, tuesday, wednesday, thursday, friday, saturday, sunday, publicDay,
            tramInfo, trainInfo, busInfo,fiveStar,
            fourStar,threeStar,twoStar,oneStar,tv_averageMark,tv_num_of_review,
            currentUserName,
            no_comment,personName1,tv_comment1,personName2,tv_comment2, time1,time2;
    private ImageView heart,headImage1,headImage2;
    private RatingBar reviewSummaryRB,rate_and_review_RB,ratingBar1,ratingBar2;
    private boolean repeat;
    private View tradeView, busView, tramView, trainView,moreReviewView;
    private LinearLayout layout_call, layout_website, layout_address, layout_description, layout_navigation,
            navigation_topLine,description_topLine,call_topLine,website_topLine,tradingHour_topLine,
            user_comment1,user_comment2,more_reviews;
    private DatabaseReference mDatabaseRootRef, favouriteBookRef, foodPlaceRef,foodPlaceCommentRef,userCommentRef;
    private ArrayList<FoodPlace> favouriteList;
    private ArrayList<String> commentList,userCommentList;
    private FirebaseAuth mAuth;
    private String uid;
    private Double lat, lng;
    private float totalMark;

    private AlertDialog alertDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_place_details_other, container, false);
        getActivity().setTitle("Place Details");

        repeat = false;

        lat = 0.0;
        lng = 0.0;
        totalMark = 0.0f;
        mDatabaseRootRef = FirebaseDatabase.getInstance().getReference();
        foodPlaceRef = mDatabaseRootRef.child("FoodPlaces");

        commentList = service.getReviewList();

        registerUI();

        setting_top_four();
        setting_address();
        setting_distance();
        setting_description();
        setting_phone();
        setting_website();
        setting_bus();
        setting_tram();
        setting_train();
        setting_trading_hour();

//        setReviewSummary();
//
//        try
//        {
//            setAllReviews();
//        }
//        catch (ParseException e)
//        {
//            e.printStackTrace();
//        }
//
//        judgeIfRepeat();
//
//        setRateAndReview();
        return mView;
    }

    /**
     * Register the mapView, create the mapView and load the map asynchronously
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = (MapView) mView.findViewById(R.id.mapview);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    /**
     * When after loading the map, this method setting the map type, zoomable, permission, marker,
     * camera position etc. According to the location, display the position on map.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        LatLng latLng = new LatLng(Double.parseDouble(service.getLatitude()), Double.parseDouble(service.getLongitude()));

        googleMap.addMarker(new MarkerOptions().position(latLng));
        CameraPosition melbourne = CameraPosition.builder().target(latLng).zoom(14).bearing(0).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(melbourne));

        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                changeFragmentToOnePlaceFragment();
            }
        });

        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                changeFragmentToOnePlaceFragment();
                return false;
            }
        });
    }

    /**
     * invoked when view is created.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();
        if (view != null) {
            layout_address.setOnClickListener(this);
            layout_navigation.setOnClickListener(this);
            layout_description.setOnClickListener(this);
            layout_call.setOnClickListener(this);
            layout_website.setOnClickListener(this);
            heart.setOnClickListener(this);
            rate_and_review_RB.setOnRatingBarChangeListener(this);
            moreReviewView.setOnClickListener(this);

            user_comment1.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mAuth.getCurrentUser() != null)
                    {
                        if (mAuth.getCurrentUser().getEmail().equals(personName1.getText()))
                        {
                            deleteComment(ratingBar1,tv_comment1.getText().toString());
                        }
                    }
                    return false;
                }
            });

            user_comment2.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mAuth.getCurrentUser() != null)
                    {
                        if (mAuth.getCurrentUser().getEmail().equals(personName2.getText()))
                        {
                            deleteComment(ratingBar2,tv_comment2.getText().toString());
                        }
                    }
                    return false;
                }
            });
        }
    }

    /**
     * This method is used for get the food place object from previous fragment.
     */
    public void transmitPlaceObjectToPlaceDetailFragment(Service service) {
        this.service = service;
//        this.favouriteList = favouriteList;
//        this.userCommentList = userCommentList;
    }

//    private void judgeIfRepeat() {
//        mAuth = FirebaseAuth.getInstance();
//        if (mAuth.getCurrentUser() != null) {
//            uid = mAuth.getCurrentUser().getUid();
//
//            for (int n = 0; n < favouriteList.size(); n++) {
//                if (favouriteList.get(n).getLatitude().equals(foodPlace.getLatitude())) {
//                    repeat = true;
//                    break;
//                }
//            }
//
//            if (repeat) {
//                heart.setImageResource(R.drawable.heart);
//            }
//        }
//    }

    private void changeFragmentToOnePlaceFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        OnePlaceMap onePlaceMap = new OnePlaceMap();
        onePlaceMap.transmitPlaceObjectToOnePlaceMapFragment(service);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.fragment_content, onePlaceMap);
        ft.addToBackStack(null);
        ft.commit();
    }

//    private void removePlaceFromFavouriteList(String longitude) {
//        for (FoodPlace favouriteItem : favouriteList) {
//            if (favouriteItem.getLongitude().equals(longitude)) {
//                favouriteList.remove(favouriteItem);
//                break;
//            }
//        }
//    }

    private void registerUI() {
        tradeView = mView.findViewById(R.id.layout_trading_hours);
        busView = mView.findViewById(R.id.layout_bus);
        tramView = mView.findViewById(R.id.layout_tram);
        trainView = mView.findViewById(R.id.layout_train);
        moreReviewView = mView.findViewById(R.id.view_more_reviews);

        heart = (ImageView) mView.findViewById(R.id.like_heart);
        headImage1 = (ImageView) mView.findViewById(R.id.head_portrait_1);
        headImage2 = (ImageView) mView.findViewById(R.id.head_portrait_2);

        user_comment1 = (LinearLayout) mView.findViewById(R.id.user1_comment);
        user_comment2 = (LinearLayout) mView.findViewById(R.id.user2_comment);
        more_reviews = (LinearLayout) mView.findViewById(R.id.view_more_reviews);

        layout_address = (LinearLayout) mView.findViewById(R.id.layout_address_line);
        layout_navigation = (LinearLayout) mView.findViewById(R.id.layout_navigation_line);
        layout_description = (LinearLayout) mView.findViewById(R.id.layout_description_line);
        layout_call = (LinearLayout) mView.findViewById(R.id.layout_call_line);
        layout_website = (LinearLayout) mView.findViewById(R.id.layout_website_line);

        navigation_topLine = (LinearLayout) mView.findViewById(R.id.navigation_top_line);
        description_topLine = (LinearLayout) mView.findViewById(R.id.description_top_line);
        call_topLine = (LinearLayout) mView.findViewById(R.id.call_top_line);
        website_topLine = (LinearLayout) mView.findViewById(R.id.website_top_line);
        tradingHour_topLine = (LinearLayout) mView.findViewById(R.id.trading_hours_top_line);

        placeName = (TextView) mView.findViewById(R.id.tv_place_name);
        suburb = (TextView) mView.findViewById(R.id.tv_open_status);
        category = (TextView) mView.findViewById(R.id.tv_category);
        address = (TextView) mView.findViewById(R.id.tv_address);
        navigation = (TextView) mView.findViewById(R.id.tv_navigation);
        description = (TextView) mView.findViewById(R.id.tv_place_description);
        phoneNo = (TextView) mView.findViewById(R.id.tv_phone);
        website = (TextView) mView.findViewById(R.id.tv_website);
        tramInfo = (TextView) mView.findViewById(R.id.tv_tram);
        trainInfo = (TextView) mView.findViewById(R.id.tv_train);
        busInfo = (TextView) mView.findViewById(R.id.tv_bus);
        monday = (TextView) mView.findViewById(R.id.trading_hour_1);
        tuesday = (TextView) mView.findViewById(R.id.trading_hour_2);
        wednesday = (TextView) mView.findViewById(R.id.trading_hour_3);
        thursday = (TextView) mView.findViewById(R.id.trading_hour_4);
        friday = (TextView) mView.findViewById(R.id.trading_hour_5);
        saturday = (TextView) mView.findViewById(R.id.trading_hour_6);
        sunday = (TextView) mView.findViewById(R.id.trading_hour_7);
        publicDay = (TextView) mView.findViewById(R.id.trading_hour_public);

        fiveStar = (TextView) mView.findViewById(R.id.tv_5star);
        fourStar = (TextView) mView.findViewById(R.id.tv_4star);
        threeStar = (TextView) mView.findViewById(R.id.tv_3star);
        twoStar = (TextView) mView.findViewById(R.id.tv_2star);
        oneStar = (TextView) mView.findViewById(R.id.tv_1star);
        tv_averageMark = (TextView) mView.findViewById(R.id.review_summary_mark);
        tv_num_of_review = (TextView)mView.findViewById(R.id.review_summary_no_reviews);

        currentUserName = (TextView) mView.findViewById(R.id.current_user_name);

        no_comment = (TextView) mView.findViewById(R.id.No_one_comment);

        personName1 = (TextView) mView.findViewById(R.id.user_name_1);
        personName2 = (TextView) mView.findViewById(R.id.user_name_2);
        time1 = (TextView) mView.findViewById(R.id.review_time_1);
        time2 = (TextView) mView.findViewById(R.id.review_time_2);
        tv_comment1 = (TextView) mView.findViewById(R.id.comment1);
        tv_comment2 = (TextView) mView.findViewById(R.id.comment2);

        reviewSummaryRB = (RatingBar) mView.findViewById(R.id.review_summary_ratingBar);
        rate_and_review_RB = (RatingBar) mView.findViewById(R.id.rate_and_review_ratingBar);
        ratingBar1 = (RatingBar) mView.findViewById(R.id.ratingBar_1);
        ratingBar2 = (RatingBar) mView.findViewById(R.id.ratingBar_2);
    }

    private void setting_top_four() {
        placeName.setText(service.getName());
        suburb.setText(service.getSuburb());
        String cate1 = service.getCategory_1();
        String cate2 = service.getCategory_2();
        String cate3 = service.getCategory_3();
        String cate4 = service.getCategory_4();
        String cate5 = service.getCategory_5();
        ArrayList<String> list = new ArrayList<>();
        list.add(cate1);
        list.add(cate2);
        list.add(cate3);
        list.add(cate4);
        list.add(cate5);
        String result = "";
        for (String category : list)
        {
            if (!category.equals("N/A"))
            {
                if (result.length()==0)
                    result = result + category;
                else
                    result = result + " / " + category;
            }
        }
        category.setText(result);
    }

    private void setting_address() {
        if (service.getAddress_2().length() == 0 && service.getAddress_1().length() == 0) {
            layout_address.setVisibility(View.GONE);
            //address.setText(R.string.no_information);
        } else if (service.getAddress_2().length() != 0 && service.getAddress_1().length() == 0) {
            address.setText(service.getAddress_2());
        } else if (service.getAddress_1().length() != 0 && service.getAddress_2().length() == 0) {
            address.setText(service.getAddress_1());
        } else if (service.getAddress_1().length() != 0 && service.getAddress_2().length() != 0) {
            address.setText(service.getAddress_1() + ", " + service.getAddress_2());
        }
    }

    private void setting_distance() {
        getCurrentLatLng();
        //locationService();
        double distance = new Distance().getDistance(lat, lng, Double.parseDouble(service.getLatitude()),
                Double.parseDouble(service.getLongitude()));
        if (lat == 0.0)
        {
            navigation.setText("");
        }
        else
        {
            DecimalFormat df = new DecimalFormat("##0.00");
            String result = df.format(distance/1000);
            navigation.setText(result + " km");
        }
    }

    private void setting_description() {
        if (service.getWhat().length() >= 40) {
            description.setText(service.getWhat().substring(0, 40) + "....");
        } else if (service.getWhat().length() != 0) {
            description.setText(service.getWhat());
        } else {
            layout_description.setVisibility(View.GONE);
            //description.setText(R.string.no_information);
        }
    }

    private void setting_phone() {
        if (service.getPhone().length() == 0 && service.getPhone2().length() == 0 && service.getFree_call().length() == 0)
            layout_call.setVisibility(View.GONE);
        else if (service.getPhone().length() != 0)
            phoneNo.setText(service.getPhone());
        else if (service.getPhone2().length() !=0)
            phoneNo.setText(service.getPhone2());
        else
            phoneNo.setText(service.getFree_call());
    }

    private void setting_website() {
        if (service.getWebsite().length() == 0 && service.getAlternate_website().length() == 0) {
            layout_website.setVisibility(View.GONE);
            //website.setText(R.string.no_information);
        } else if (service.getWebsite().length() != 0) {
            website.setText(service.getWebsite());
        } else {
            website.setText(service.getAlternate_website());
        }
    }

    private void setting_bus() {
        if (service.getBus_routes().length() == 0) {
            busView.setVisibility(View.GONE);
        } else {
            busInfo.setText(service.getBus_routes());
        }
    }

    private void setting_tram() {
        if (service.getTram_routes().length() == 0) {
            tramView.setVisibility(View.GONE);
        } else {
            tramInfo.setText(service.getTram_routes());
        }
    }

    private void setting_train() {
        if (service.getNearest_train_station().length() == 0) {
            trainView.setVisibility(View.GONE);
        } else {
            trainInfo.setText(service.getNearest_train_station());
        }
    }

    private void setting_trading_hour() {
        if (service.getMonday().isEmpty())
        {
            monday.setText("Open");
            tuesday.setText("Open");
            wednesday.setText("Open");
            thursday.setText("Open");
            friday.setText("Open");
            saturday.setText("Open");
            sunday.setText("Open");
            publicDay.setText("Open");
        }
        else
        {
            monday.setText(service.getMonday());
            tuesday.setText(service.getTuesday());
            wednesday.setText(service.getWednesday());
            thursday.setText(service.getThursday());
            friday.setText(service.getFriday());
            saturday.setText(service.getSaturday());
            sunday.setText(service.getSunday());
            publicDay.setText(service.getPublic_holidays());
        }
    }

    private void getCurrentLatLng() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        String provider = LocationManager.NETWORK_PROVIDER;
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 123);
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
        if (requestCode == 123)
        {
            if (permissions[0].equals(android.Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[0]== PackageManager.PERMISSION_GRANTED)
            {
                try
                {
                    LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                    Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                    double distance = new Distance().getDistance(lat, lng, Double.parseDouble(service.getLatitude()),
                            Double.parseDouble(service.getLongitude()));
                    navigation.setText(String.valueOf(distance / 1000) + " km");

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
//    /**
//     * Request the permission and grant it.
//     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == 123) {
//            if (permissions[0].equals(android.Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                try
//                {
//                    LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                    if (location != null)
//                    {
//                        lat = location.getLatitude();
//                        lng = location.getLongitude();
//                        double distance = new Distance().getDistance(lat, lng, Double.parseDouble(foodPlace.getLatitude()),
//                                Double.parseDouble(foodPlace.getLongitude()));
//                        navigation.setText(String.valueOf(distance / 1000) + " km");
//                    }
//                    else
//                    {
//                        LocationListener locationListener = new LocationListener() {
//
//                            // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
//                            @Override
//                            public void onStatusChanged(String provider, int status, Bundle extras) {
//
//                            }
//
//                            // Provider被enable时触发此函数，比如GPS被打开
//                            @Override
//                            public void onProviderEnabled(String provider) {
//
//                            }
//
//                            // Provider被disable时触发此函数，比如GPS被关闭
//                            @Override
//                            public void onProviderDisabled(String provider) {
//                                Toast.makeText(getActivity(), "Please access to network", Toast.LENGTH_SHORT).show();
//                            }
//
//                            //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
//                            @Override
//                            public void onLocationChanged(Location location)
//                            {
//                                if (location != null)
//                                {
//                                    lat = location.getLatitude();
//                                    lng = location.getLongitude();
//                                    double distance = new Distance().getDistance(lat, lng, Double.parseDouble(foodPlace.getLatitude()),
//                                            Double.parseDouble(foodPlace.getLongitude()));
//                                    navigation.setText(String.valueOf(distance / 1000) + " km");
//                                }
//                            }
//                        };
//
//                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000, 0,locationListener);
//                        Location location1 = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                        if(location1 != null)
//                        {
//                            lat = location1.getLatitude();
//                            lng = location1.getLongitude();
//                            double distance = new Distance().getDistance(lat, lng, Double.parseDouble(foodPlace.getLatitude()),
//                                    Double.parseDouble(foodPlace.getLongitude()));
//                            navigation.setText(String.valueOf(distance / 1000) + " km");
//                        }
//                    }
//
//                }
//                catch (SecurityException e)
//                {
//                    e.printStackTrace();
//                }
//            }
//            else
//            {
//                Toast.makeText(getActivity(), "Please grant the permission", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.layout_address_line:
                layout_address.setBackgroundColor(getResources().getColor(R.color.colorSelectItem));
                navigation_topLine.setBackgroundColor(getResources().getColor(R.color.colorSelectItem));
                changeFragmentToOnePlaceFragment();
                break;
            case R.id.layout_navigation_line:
                layout_navigation.setBackgroundColor(getResources().getColor(R.color.colorSelectItem));
                description_topLine.setBackgroundColor(getResources().getColor(R.color.colorSelectItem));
                navigationFunction();
                break;
            case R.id.layout_description_line:
                layout_description.setBackgroundColor(getResources().getColor(R.color.colorSelectItem));
                call_topLine.setBackgroundColor(getResources().getColor(R.color.colorSelectItem));
                transferToDescriptionFragment();
                break;
            case R.id.layout_call_line:
                layout_call.setBackgroundColor(getResources().getColor(R.color.colorSelectItem));
                website_topLine.setBackgroundColor(getResources().getColor(R.color.colorSelectItem));
                callFunction();
                break;
            case R.id.layout_website_line:
                layout_website.setBackgroundColor(getResources().getColor(R.color.colorSelectItem));
                tradingHour_topLine.setBackgroundColor(getResources().getColor(R.color.colorSelectItem));
                websiteFunction();
                break;
            case R.id.like_heart:
                //changeHeartIcon();
                break;
//            case R.id.view_more_reviews:
//                transferToReviewBottomSheet();
//                break;

        }
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        if (mAuth.getCurrentUser() != null)
            showCommentDialog(rating);
        else
            showLoginDialog();
    }

    private void callFunction() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());
        builder.setMessage("You want to make a call to this place ?");
        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                if (service.getPhone().length() == 0 && service.getPhone2().length() == 0)
                {
                    layout_call.setClickable(false);
                }
                else if (service.getPhone().length() != 0)
                {
                    Uri data = Uri.parse("tel:" + service.getPhone());
                    intent.setData(data);
                    startActivity(intent);
                }
                else
                {
                    Uri data = Uri.parse("tel:" + service.getPhone2());
                    intent.setData(data);
                    startActivity(intent);
                }
                // layout_call.setBackgroundColor(getResources().getColor(R.color.colorOriginal));
                layout_call.setBackgroundColor(Color.TRANSPARENT);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //layout_call.setBackgroundColor(getResources().getColor(R.color.colorOriginal));
                layout_call.setBackgroundColor(Color.TRANSPARENT);
                dialogInterface.cancel();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void websiteFunction() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());
        builder.setMessage("You want to go to this website ?");
        builder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                if (service.getWebsite().length() == 0 && service.getAlternate_website().length() == 0) {
                    layout_website.setClickable(false);
                } else if (service.getWebsite().length() != 0) {
                    Uri uri = Uri.parse(service.getWebsite());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                } else {
                    Uri uri = Uri.parse(service.getAlternate_website());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
                layout_website.setBackgroundColor(Color.TRANSPARENT);
                tradingHour_topLine.setBackgroundColor(Color.TRANSPARENT);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                layout_website.setBackgroundColor(Color.TRANSPARENT);
                tradingHour_topLine.setBackgroundColor(Color.TRANSPARENT);
                dialogInterface.cancel();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void navigationFunction() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());
        builder.setMessage("You want to navigate to this place ?");
        builder.setPositiveButton("Navigate", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                Double latitude = Double.parseDouble(service.getLatitude());
                Double longitude = Double.parseDouble(service.getLongitude());
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
                layout_navigation.setBackgroundColor(Color.TRANSPARENT);
                description_topLine.setBackgroundColor(Color.TRANSPARENT);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                layout_navigation.setBackgroundColor(Color.TRANSPARENT);
                description_topLine.setBackgroundColor(Color.TRANSPARENT);
                dialogInterface.cancel();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void transferToDescriptionFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Description description = new Description();
        description.sendContentToDescriptionFragment(service.getWhat());
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.fragment_content, description);
        ft.addToBackStack(null);
        ft.commit();
    }

//    private void changeHeartIcon() {
//        if (mAuth.getCurrentUser() != null)
//        {
//            if (!repeat)
//            {
//                favouriteList.add(foodPlace);
//                favouriteBookRef = mDatabaseRootRef.child("Users").child(uid).child("favouriteList");
//                favouriteBookRef.setValue(favouriteList);
//                heart.setImageResource(R.drawable.heart);
//                repeat = true;
//
//                foodPlace.addOneTimes();
//                int times = foodPlace.getAddTimes();
//                String[] array = foodPlace.getLongitude().split("\\.");
//                String level = array[0] + array[1];
//                foodPlaceRef.child(level).child("addTimes").setValue(times);
//            }
//            else
//            {
//                removePlaceFromFavouriteList(foodPlace.getLongitude());
//                favouriteBookRef = mDatabaseRootRef.child("Users").child(uid).child("favouriteList");
//                favouriteBookRef.setValue(favouriteList);
//                heart.setImageResource(R.drawable.heartempty);
//                repeat = false;
//            }
//        }
//        else
//        {
//            AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());
//            builder.setMessage("This function can only be used when you login.");
//            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    dialogInterface.cancel();
//                }
//            });
//            builder.setCancelable(false);
//            builder.show();
//        }
//    }

    private void transferToReviewBottomSheet() {
        String average_mark = tv_averageMark.getText().toString();
        String numOfReviews = tv_num_of_review.getText().toString();
        ReviewBottomSheet reviewBottomSheet = new ReviewBottomSheet();
        //reviewBottomSheet.sendDataToBottomSheetFragment(commentList,average_mark,numOfReviews);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.fragment_content, reviewBottomSheet);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void showCommentDialog(float rating) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View textEntryView = inflater.inflate(R.layout.rating_dialog, null);

        final RatingBar rating_page_RB = (RatingBar) textEntryView.findViewById(R.id.rating_page_ratingBar);
        final EditText rating_page_ET = (EditText) textEntryView.findViewById(R.id.rating_page_editText);

        rating_page_RB.setRating(rating);

        AlertDialog.Builder dl = new AlertDialog.Builder(mView.getContext());
        dl.setView(textEntryView);
        dl.setPositiveButton("Post", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i)
            {
                if (rating_page_RB.getRating() == 0.0f)
                {
                    Toast.makeText(getActivity(), "0 mark is not valid", Toast.LENGTH_SHORT).show();
                }
                else if (rating_page_ET.getText().toString().length()==0)
                {
                    Toast.makeText(getActivity(), "Please input something", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    writeCommentToFireBase(rating_page_ET.getText().toString(),rating_page_RB.getRating());
                    AlertDialog.Builder dl1 = new AlertDialog.Builder(mView.getContext());
                    dl1.setMessage("Comment post successfully, if you want to delete this comment, you can long click" +
                            " your comment at the top of comment area below.");
                    dl1.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            dialogInterface.cancel();
                            Toast.makeText(getActivity(), "Comment successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dl1.create().show();
                }
            }
        });

        dl.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });
        dl.show();
    }

    private void showLoginDialog() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View textEntryView = inflater.inflate(R.layout.require_login_dialog, null);
        Button btn = (Button) textEntryView.findViewById(R.id.btn_sign_in);
        TextView tv = (TextView) textEntryView.findViewById(R.id.btn_sign_up);

        AlertDialog.Builder dl = new AlertDialog.Builder(mView.getContext());
        dl.setView(textEntryView);
        dl.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });
        alertDialog = dl.show();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertDialog != null) {
                    alertDialog.dismiss();
                }
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Login login = new Login();
                ft.replace(R.id.fragment_content,login);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertDialog != null) {
                    alertDialog.dismiss();
                }
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Register register = new Register();
                ft.replace(R.id.fragment_content,register);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

    }

    private void writeCommentToFireBase(String comment,float mark) {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        String currentTime = df.format(date.getTime());
        String rating = mark + "";
        String currentID = mAuth.getCurrentUser().getEmail();

        String oneRecordForPlace = currentTime + "^" + currentID + "^" + rating + "^" + comment;
        commentList.add(oneRecordForPlace);
        String[] array = service.getLongitude().split("\\.");
        String placeID = array[0] + array[1];
        foodPlaceCommentRef = foodPlaceRef.child(placeID).child("reviewList");
        foodPlaceCommentRef.setValue(commentList);
        addNewReviewChangeSummary(mark);

        String oneRecordForUser = currentTime + "^" + service.getLongitude() + "-" + service.getName()
                + "^" + rating + "^" + comment;
        userCommentList.add(oneRecordForUser);
        userCommentRef = mDatabaseRootRef.child("Users").child(uid).child("commentList");
        userCommentRef.setValue(userCommentList);

        addNewReviewChangeReviewListView(currentID,comment,mark);
    }

    private void setReviewSummary() {
        String outcome = reviewSummaryCalculation();
        String[] array = outcome.split(",");
        tv_averageMark.setText(""+Float.parseFloat(array[0]));
        reviewSummaryRB.setRating(Float.parseFloat(array[0]));
        fiveStar.setText(array[1]);
        fourStar.setText(array[2]);
        threeStar.setText(array[3]);
        twoStar.setText(array[4]);
        oneStar.setText(array[5]);
        tv_num_of_review.setText(commentList.size()+" reviews");
    }

    private String reviewSummaryCalculation() {
        int five = 0;
        int four = 0;
        int three = 0;
        int two = 0;
        int one = 0;
        int numberOfReviews = commentList.size();
        for (String record : commentList)
        {
            String[] array = record.split("\\^");
            totalMark += Float.parseFloat(array[2]);
            if (Float.parseFloat(array[2]) == 5.0f)
                five +=1;
            else if (Float.parseFloat(array[2]) == 4.0f)
                four +=1;
            else if (Float.parseFloat(array[2]) == 3.0f)
                three +=1;
            else if (Float.parseFloat(array[2]) == 2.0f)
                two +=1;
            else
                one +=1;
        }
        float averageMark = totalMark/numberOfReviews;
        float round = (float)(Math.round(averageMark*10))/10;
        return round + "," + five + "," + four + "," + three + "," + two + "," + one ;
    }

    private void setRateAndReview() {

        if (mAuth.getCurrentUser() != null)
        {
            currentUserName.setText(mAuth.getCurrentUser().getEmail());
        }
        else
        {
            currentUserName.setText("Guest");
        }

    }

    private void setAllReviews() throws ParseException {
        if (commentList.size()==0)
        {
            no_comment.setVisibility(View.VISIBLE);
            user_comment1.setVisibility(View.GONE);
            user_comment2.setVisibility(View.GONE);
            more_reviews.setVisibility(View.GONE);
        }
        else if (commentList.size() == 1)
        {
            no_comment.setVisibility(View.GONE);
            user_comment1.setVisibility(View.VISIBLE);
            user_comment2.setVisibility(View.GONE);
            more_reviews.setVisibility(View.GONE);
            setReviewForEach(1,personName1,ratingBar1,tv_comment1,time1);
        }
        else if (commentList.size() == 2)
        {
            no_comment.setVisibility(View.GONE);
            user_comment1.setVisibility(View.VISIBLE);
            user_comment2.setVisibility(View.VISIBLE);
            more_reviews.setVisibility(View.GONE);
            setReviewForEach(1,personName1,ratingBar1,tv_comment1,time1);
            setReviewForEach(2,personName2,ratingBar2,tv_comment2,time2);
        }
        else
        {
            no_comment.setVisibility(View.GONE);
            user_comment1.setVisibility(View.VISIBLE);
            user_comment2.setVisibility(View.VISIBLE);
            more_reviews.setVisibility(View.VISIBLE);
            setReviewForEach(1,personName1,ratingBar1,tv_comment1,time1);
            setReviewForEach(2,personName2,ratingBar2,tv_comment2,time2);
        }
    }

    private void setReviewForEach(int number,TextView name,RatingBar rb,TextView tv_comment,TextView tv_time)
            throws ParseException {
        String[] recordArray = commentList.get(commentList.size()-number).split("\\^");
        name.setText(recordArray[1]);
        rb.setRating(Float.parseFloat(recordArray[2]));
        tv_comment.setText(recordArray[3]);
        changeTimeTextViewFromFireBase(recordArray,tv_time);
    }

    private int differentDaysByMillisecond(Date date1,Date date2) {
        int days = (int)((date2.getTime() - date1.getTime()) / (1000*3600*24));
        return days;
    }

    private void addNewReviewChangeSummary(float mark) {
        if (mark == 5.0f)
        {
            addNewReviewChangeSummaryEachCase(mark,fiveStar);
        }
        else if (mark == 4.0f)
        {
            addNewReviewChangeSummaryEachCase(mark,fourStar);
        }
        else if (mark == 3.0f)
        {
            addNewReviewChangeSummaryEachCase(mark,threeStar);
        }
        else if (mark == 2.0f)
        {
            addNewReviewChangeSummaryEachCase(mark,twoStar);
        }
        else
        {
            addNewReviewChangeSummaryEachCase(mark,oneStar);
        }
    }

    private void addNewReviewChangeSummaryEachCase(float rating, TextView textView) {
        String newValue = String.valueOf(Integer.parseInt(textView.getText().toString())+1);
        textView.setText(newValue);
        totalMark = totalMark + rating;
        float newAverageMark = totalMark/commentList.size();
        float round = (float)(Math.round(newAverageMark*10))/10;
        tv_averageMark.setText(round+"");
        reviewSummaryRB.setRating(newAverageMark);
        tv_num_of_review.setText(commentList.size()+" reviews");
    }

    private void addNewReviewChangeReviewListView(String currentID,String comment,float mark) {
        if (commentList.size()==1)
        {
            no_comment.setVisibility(View.GONE);
            personName1.setText(currentID);
            tv_comment1.setText(comment);
            ratingBar1.setRating(mark);
            time1.setText("moment ago");
            user_comment1.setVisibility(View.VISIBLE);
        }
        else if (commentList.size()==2)
        {
            personName2.setText(personName1.getText());
            tv_comment2.setText(tv_comment1.getText());
            ratingBar2.setRating(ratingBar1.getRating());
            time2.setText(time1.getText());
            user_comment2.setVisibility(View.VISIBLE);

            personName1.setText(currentID);
            tv_comment1.setText(comment);
            ratingBar1.setRating(mark);
            time1.setText("moment ago");
        }
        else if (commentList.size()==3)
        {
            personName2.setText(personName1.getText());
            tv_comment2.setText(tv_comment1.getText());
            ratingBar2.setRating(ratingBar1.getRating());
            time2.setText(time1.getText());

            personName1.setText(currentID);
            tv_comment1.setText(comment);
            ratingBar1.setRating(mark);
            time1.setText("moment ago");
            more_reviews.setVisibility(View.VISIBLE);
        }
        else
        {
            personName2.setText(personName1.getText());
            tv_comment2.setText(tv_comment1.getText());
            ratingBar2.setRating(ratingBar1.getRating());
            time2.setText(time1.getText());

            personName1.setText(currentID);
            tv_comment1.setText(comment);
            ratingBar1.setRating(mark);
            time1.setText("moment ago");
        }
    }

    private void deleteComment(final RatingBar ratingBar, final String comment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mView.getContext());
        builder.setTitle("Delete comment?");
        builder.setMessage("Are you sure you want to delete this comment?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
//                    for (String placeOneRecord : commentList)
//                    {
//                        String[] array = placeOneRecord.split("\\^");
//                        if (array[3].equals(comment))
//                        {
//                            commentList.remove(placeOneRecord);
//                        }
//                    }
                for (Iterator<String> iterator = commentList.iterator(); iterator.hasNext();)
                {
                    String[] array = iterator.next().split("\\^");
                    if (array[3].equals(comment))
                    {
                        iterator.remove();
                    }
                }

//                    for (String userOneRecord : userCommentList)
//                    {
//                        String[] array = userOneRecord.split("\\^");
//                        if (array[3].equals(comment))
//                        {
//                            userCommentList.remove(userOneRecord);
//                        }
//                    }

                for (Iterator<String> iterator = userCommentList.iterator();iterator.hasNext();)
                {
                    String[] array = iterator.next().split("\\^");
                    if (array[3].equals(comment))
                    {
                        iterator.remove();
                    }
                }

                try
                {
                    deleteCommentFromFireBase(ratingBar);
                }
                catch (ParseException e)
                {
                    e.printStackTrace();
                }
                Toast.makeText(getActivity(), "Comment has successfully deleted", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.create().show();
    }

    private void deleteCommentFromFireBase(RatingBar ratingBar) throws ParseException {
        String[] array = service.getLongitude().split("\\.");
        String placeID = array[0] + array[1];
        foodPlaceCommentRef = foodPlaceRef.child(placeID).child("reviewList");
        foodPlaceCommentRef.setValue(commentList);
        deleteOneReviewChangeSummary(ratingBar);

        userCommentRef = mDatabaseRootRef.child("Users").child(uid).child("commentList");
        userCommentRef.setValue(userCommentList);
        deleteOneReviewChangeReviewListView(ratingBar);
    }

    private void deleteOneReviewChangeSummary(RatingBar ratingBar) {
        if (ratingBar.getRating() == 5.0f)
        {
            deleteOneReviewChangeSummaryEachCase(5.0f,fiveStar);
        }
        else if (ratingBar.getRating() == 4.0f)
        {
            addNewReviewChangeSummaryEachCase(4.0f,fourStar);
        }
        else if (ratingBar.getRating() == 3.0f)
        {
            addNewReviewChangeSummaryEachCase(3.0f,threeStar);
        }
        else if (ratingBar.getRating() == 2.0f)
        {
            addNewReviewChangeSummaryEachCase(2.0f,twoStar);
        }
        else
        {
            addNewReviewChangeSummaryEachCase(1.0f,oneStar);
        }
    }

    private void deleteOneReviewChangeSummaryEachCase(float rating, TextView textView) {
        String newValue = String.valueOf(Integer.parseInt(textView.getText().toString())-1);
        textView.setText(newValue);
        totalMark = totalMark - rating;
        float newAverageMark = totalMark/commentList.size();
        float round = (float)(Math.round(newAverageMark*10))/10;
        tv_averageMark.setText(round+"");
        reviewSummaryRB.setRating(totalMark);
        tv_num_of_review.setText(commentList.size()+" reviews");
    }

    private void deleteOneReviewChangeReviewListView(RatingBar ratingBar) throws ParseException {
        if (commentList.size()==0)
        {
            no_comment.setVisibility(View.VISIBLE);
            user_comment1.setVisibility(View.GONE);
            user_comment2.setVisibility(View.GONE);
            more_reviews.setVisibility(View.GONE);
        }
        else if (commentList.size() == 1)
        {
            if (ratingBar == ratingBar1)
            {
                no_comment.setVisibility(View.GONE);
                user_comment1.setVisibility(View.VISIBLE);
                user_comment2.setVisibility(View.GONE);
                more_reviews.setVisibility(View.GONE);
                personName1.setText(personName2.getText());
                tv_comment1.setText(tv_comment2.getText());
                ratingBar1.setRating(ratingBar2.getRating());
                time1.setText(time2.getText());
            }
            else
            {
                no_comment.setVisibility(View.GONE);
                user_comment1.setVisibility(View.VISIBLE);
                user_comment2.setVisibility(View.GONE);
                more_reviews.setVisibility(View.GONE);
            }


        }
        else if (commentList.size() == 2)
        {
            if (ratingBar == ratingBar1)
            {
                no_comment.setVisibility(View.GONE);
                user_comment1.setVisibility(View.VISIBLE);
                user_comment2.setVisibility(View.VISIBLE);
                more_reviews.setVisibility(View.GONE);

                personName1.setText(personName2.getText());
                tv_comment1.setText(tv_comment2.getText());
                ratingBar1.setRating(ratingBar2.getRating());
                time1.setText(time2.getText());

                String[] array = commentList.get(0).split("\\^");
                personName2.setText(array[1]);
                tv_comment2.setText(array[3]);
                ratingBar2.setRating(Float.parseFloat(array[2]));
                changeTimeTextViewFromFireBase(array,time2);
            }
            else
            {
                no_comment.setVisibility(View.GONE);
                user_comment1.setVisibility(View.VISIBLE);
                user_comment2.setVisibility(View.VISIBLE);
                more_reviews.setVisibility(View.GONE);

                String[] array = commentList.get(0).split("\\^");
                personName2.setText(array[1]);
                tv_comment2.setText(array[3]);
                ratingBar2.setRating(Float.parseFloat(array[2]));
                changeTimeTextViewFromFireBase(array,time2);
            }


        }
        else
        {
            no_comment.setVisibility(View.GONE);
            user_comment1.setVisibility(View.VISIBLE);
            user_comment2.setVisibility(View.VISIBLE);
            more_reviews.setVisibility(View.VISIBLE);

            personName1.setText(personName2.getText());
            tv_comment1.setText(tv_comment2.getText());
            ratingBar1.setRating(ratingBar2.getRating());
            time1.setText(time2.getText());

            String[] array = commentList.get(commentList.size()-2).split("\\^");
            personName2.setText(array[1]);
            tv_comment2.setText(array[3]);
            ratingBar2.setRating(Float.parseFloat(array[2]));
            changeTimeTextViewFromFireBase(array,time2);
        }

    }

    private void changeTimeTextViewFromFireBase(String[] array, TextView textView) throws ParseException {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

        int numberOfDays = differentDaysByMillisecond(simpleDateFormat.parse(array[0]),date);
        if (numberOfDays < 1)
        {
            textView.setText("less than a day");
        }
        else if (numberOfDays <=7)
        {
            textView.setText(numberOfDays + " days ago");
        }
        else if (numberOfDays > 7 && numberOfDays <= 30)
        {
            int weekNumber = numberOfDays/7;
            String weekNo = String.valueOf(weekNumber);
            textView.setText(weekNo.charAt(0)+" weeks ago");
        }
        else if (numberOfDays > 30)
        {
            int monthNumber = numberOfDays/30;
            String monthNo = String.valueOf(monthNumber);
            if (monthNumber < 10)
            {
                textView.setText(monthNo.charAt(0)+ " months ago");
            }
            else
            {
                textView.setText(monthNo.charAt(0) + monthNo.charAt(1) + " months ago");
            }
        }
    }
}
