package com.example.homelessservices;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;


public class ToiletCluster extends Fragment implements OnMapReadyCallback,View.OnClickListener
{
    private GoogleMap mGoogleMap;
    private MapView mMapView;
    private CardView nearest_btn,apply_btn;
    private CheckBox checkBox_male,checkBox_female,checkBox_wheelChair,checkBox_baby;
    private ArrayList<ToiletPlace> toiletPlaceArrayList;
    private ArrayList<CheckBox> allCheckBoxList;
    private HashMap<String,ToiletPlace> map;
    private ClusterManager<Toilet> clusterManager;
    private Double lat,lng;


    //cameraPosition
    private final CameraPosition[] mPreviousCameraPosition = {null};

    private static final LatLng MELBOURNE_CITY_CENTER = new LatLng(-37.81303878836988,144.96597290039062);
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_toilet_cluster, container, false);
        getActivity().setTitle("Find Nearby Toilets");
        setHasOptionsMenu(true);

        lat = 0.0;
        lng = 0.0;

        map = new HashMap<>();
        initView(view);

        toiletPlaceArrayList = ReadDataFromFireBase.readToiletData();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        if (view != null)
        {
            nearest_btn.setOnClickListener(this);
            apply_btn.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.nearest_cardView:
                try {
                    findNearest();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.apply_cardView:
                try {
                    ArrayList<ToiletPlace> resultList = new ArrayList<>(getToiletPlacesBasedOnCondition());
                    setMapItem(resultList,true);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void initView(View view) {
        nearest_btn = (CardView) view.findViewById(R.id.nearest_cardView);
        apply_btn = (CardView) view.findViewById(R.id.apply_cardView);
        allCheckBoxList = new ArrayList<>();
        checkBox_male = (CheckBox) view.findViewById(R.id.male_box);
        checkBox_female = (CheckBox) view.findViewById(R.id.female_box);
        checkBox_wheelChair = (CheckBox) view.findViewById(R.id.wheelChair_box);
        checkBox_baby = (CheckBox) view.findViewById(R.id.baby_box);
        allCheckBoxList.add(checkBox_male);
        allCheckBoxList.add(checkBox_female);
        allCheckBoxList.add(checkBox_wheelChair);
        allCheckBoxList.add(checkBox_baby);
    }

    private void findNearest() throws InvocationTargetException, IllegalAccessException {
        getCurrentLatLng();
        double shortestDistance = 500000.00;
        ToiletPlace thatToilet = new ToiletPlace();
            ArrayList<ToiletPlace> tmpResultList = new ArrayList<>(getToiletPlacesBasedOnCondition());
            ArrayList<ToiletPlace> resultList = new ArrayList<>();
            for (ToiletPlace toiletPlace : tmpResultList)
            {
                double distance = new Distance().getDistance(lat, lng, Double.parseDouble(toiletPlace.getLat()),
                        Double.parseDouble(toiletPlace.getLon()));
                if (distance < shortestDistance)
                {
                    shortestDistance = distance;
                    thatToilet = toiletPlace;
                }
            }
            resultList.add(thatToilet);
            setMapItem(resultList,false);
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

    private ArrayList<ToiletPlace> getToiletPlacesBasedOnCondition() throws InvocationTargetException, IllegalAccessException {

        ArrayList<ToiletPlace> toiletResultList = new ArrayList<>(toiletPlaceArrayList);
        for(CheckBox checkBox : allCheckBoxList)
        {
            if (checkBox.isChecked())
            {
                for(int index = 0; index < toiletResultList.size();)
                {
                    if((reflectGetMethod(toiletResultList.get(index),checkBox.getText().toString()).equals("no")))
                        toiletResultList.remove(index);
                    else
                        index++;
                }
            }
        }
        return toiletResultList;
    }
    /**
     * Register the mapView, create the mapView and load the map asynchronously
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = (MapView) view.findViewById(R.id.map_view);
        if (mMapView != null)
        {
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
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setCompassEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        setMapItem(toiletPlaceArrayList,true);
    }

    private void setMapItem(ArrayList<ToiletPlace> list,boolean flag) {
        mGoogleMap.clear();
        setUpCluster();
        controlCameraMoving();
        getCurrentLocation();

        for (ToiletPlace tp : list)
        {
            map.put(tp.getName(),tp);
            try
            {
                addItemToCluster(tp);
            } catch (InvocationTargetException e)
            {
                e.printStackTrace();
            } catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }

        clusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<Toilet>() {
            @Override
            public boolean onClusterClick(final Cluster<Toilet> cluster)
            {
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cluster.getPosition(),
                        (float)Math.floor(mGoogleMap.getCameraPosition().zoom + 1)), 300, null);
                return true;
            }
        });

        clusterManager.cluster();

        if (flag)
        {
            CameraPosition melbourne = CameraPosition.builder().target(MELBOURNE_CITY_CENTER).zoom(12).bearing(0).build();
            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(melbourne));
        }
        else
        {
            LatLng target = new LatLng(Double.parseDouble(list.get(0).getLat()),Double.parseDouble(list.get(0).getLon()));
            CameraPosition melbourne = CameraPosition.builder().target(target).zoom(12).bearing(0).build();
            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(melbourne));
        }

        mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker)
            {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = inflater.inflate(R.layout.info_window,null);
                TextView name = (TextView) v.findViewById(R.id.tv_name);
                ImageView male = (ImageView) v.findViewById(R.id.img_male);
                ImageView female = (ImageView) v.findViewById(R.id.img_female);
                ImageView wheelchair = (ImageView) v.findViewById(R.id.img_wheel);
                ImageView baby = (ImageView) v.findViewById(R.id.img_baby);

                String[] array = marker.getSnippet().split(",");
                name.setText(marker.getTitle());

                if (array[0].equals("no"))
                    male.setImageResource(R.drawable.wrong);
                if (array[1].equals("no"))
                    female.setImageResource(R.drawable.wrong);
                if (array[2].equals("no"))
                    wheelchair.setImageResource(R.drawable.wrong);
                if (array[3].equals("no"))
                    baby.setImageResource(R.drawable.wrong);
                return v;
            }
        });

        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker)
            {
                String[] array = marker.getSnippet().split(",");
                navigationFunction(array[4],array[5]);
            }
        });

    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        {
            mGoogleMap.setMyLocationEnabled(true);
        }
        else
        {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},12345);
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
        else if (requestCode == 12345)
        {
            if (permissions[0].equals(android.Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[0]== PackageManager.PERMISSION_GRANTED)
            {
                try
                {
                    mGoogleMap.setMyLocationEnabled(true);
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

    private void setUpCluster() {
        clusterManager = new ClusterManager<Toilet>(getContext(), mGoogleMap);
        mGoogleMap.setOnCameraIdleListener(clusterManager);
        mGoogleMap.setOnInfoWindowClickListener(clusterManager);
        mGoogleMap.setOnMarkerClickListener(clusterManager);
        clusterManager.setRenderer(new OwnIconRendered(getContext(),mGoogleMap,clusterManager));
    }

    private void controlCameraMoving() {
        mGoogleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle()
            {
                CameraPosition position = mGoogleMap.getCameraPosition();
                if(mPreviousCameraPosition[0] == null || mPreviousCameraPosition[0].zoom != position.zoom)
                {
                    mPreviousCameraPosition[0] = mGoogleMap.getCameraPosition();
                    clusterManager.cluster();
                }
            }
        });
    }

    public void transmitToToiletClusterFragment(ArrayList<ToiletPlace> toiletPlaceArrayList) {
        this.toiletPlaceArrayList = toiletPlaceArrayList;
    }

    public class OwnIconRendered extends DefaultClusterRenderer<Toilet> {

        public OwnIconRendered(Context context, GoogleMap map, ClusterManager<Toilet> clusterManager)
        {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(Toilet fpi, MarkerOptions markerOptions)
        {
            int height = 70;
            int width = 70;
            BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.toilet_marker);
            Bitmap b=bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

            String[] array = fpi.getName().split("-");
            markerOptions.title(array[array.length-1].trim());
            //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
            //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.toilet_marker));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

            markerOptions.snippet(fpi.getMale()+","+fpi.getFemale()+","+fpi.getWheelchair()+","+fpi.getBaby_facil()+","+fpi.getLat()+","+fpi.getLon());

            super.onBeforeClusterItemRendered(fpi, markerOptions);
        }
        @Override
        protected void onClusterItemRendered(Toilet clusterItem, Marker marker)
        {
            super.onClusterItemRendered(clusterItem, marker);
        }

    }

    public void addItemToCluster(ToiletPlace toiletPlace) throws InvocationTargetException, IllegalAccessException {
        Toilet item = new Toilet();
        item.addAllAttributesToList();
        item.setPosition(Double.parseDouble(toiletPlace.getLat()), Double.parseDouble(toiletPlace.getLon()));
        updateObjectAttributes(toiletPlace,item,clusterManager,item.getAllAttributesToList());
    }

    /**
     * Create the food place item object, and update the attribute information.
     */
    private void updateObjectAttributes(ToiletPlace toiletPlace, Toilet toilet,ClusterManager<Toilet> ClusterList,
                                        ArrayList<String> attributeList) throws InvocationTargetException, IllegalAccessException {
        for (int j=0;j<attributeList.size(); j++)
        {
            String name = attributeList.get(j);

            String attributeValue  = reflectGetMethod(toiletPlace,name);

            String methodName = "set" + name.toUpperCase().charAt(0) + name.substring(1);

            try
            {
                Method method = toilet.getClass().getMethod(methodName,String.class);
                method.invoke(toilet,attributeValue);
            }
            catch (NoSuchMethodException e)
            {
                e.printStackTrace();
            }
        }
        ClusterList.addItem(toilet);
    }

    private String reflectGetMethod(ToiletPlace tp,String attribute) throws InvocationTargetException, IllegalAccessException {
        if (attribute.equals("WheelChair"))
            attribute = "wheelchair";
        if (attribute.equals("Baby Facility"))
            attribute = "baby_facil";
        String methodName = "get" + attribute.toUpperCase().charAt(0) + attribute.substring(1);
        String tmpResult = "";
        try
        {
            Method method = tp.getClass().getMethod(methodName);
            tmpResult = (String)method.invoke(tp);
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        return tmpResult;
    }

    private void navigationFunction(final String lat,final String lon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());
        builder.setMessage("You want to navigate to this toilet ?");
        builder.setPositiveButton("Navigate", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                Double latitude = Double.parseDouble(lat);
                Double longitude = Double.parseDouble(lon);
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }
}
