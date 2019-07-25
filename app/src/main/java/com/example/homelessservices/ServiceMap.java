package com.example.homelessservices;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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


public class ServiceMap extends Fragment implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;
    private MapView mMapView;
    private View view;
    private ArrayList<Service> servicePlaceArrayList,favouriteList;
    private ArrayList<String> userCommentList;
    private HashMap<String,Service> map;
    private ClusterManager<ServiceClusterItem> clusterManager;

    //cameraPosition
    private final CameraPosition[] mPreviousCameraPosition = {null};

    private static final LatLng MELBOURNE_CITY_CENTER = new LatLng(-37.81303878836988,144.96597290039062);
    private ArrayList<String> selectCheckBoxNameList;
    private String categorySelected,subCategorySelected,category;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_service_map, container, false);
        getActivity().setTitle(category);
        setHasOptionsMenu(true);

        map = new HashMap<>();
        return view;
    }


    /**
     * Register the mapView, create the mapView and load the map asynchronously
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
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
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setCompassEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);

        setUpCluster();
        controlCameraMoving();
        getCurrentLocation();


        for (Service service : servicePlaceArrayList)
        {
            map.put(service.getName(),service);
            try
            {
                addItemToCluster(service);
            } catch (InvocationTargetException e)
            {
                e.printStackTrace();
            } catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
            //clusterManager.addItem(fp);
        }

        clusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<ServiceClusterItem>() {
            @Override
            public boolean onClusterClick(final Cluster<ServiceClusterItem> cluster)
            {
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cluster.getPosition(),
                        (float)Math.floor(mGoogleMap.getCameraPosition().zoom + 1)), 300, null);
                return true;
            }
        });

        clusterManager.cluster();

        CameraPosition melbourne = CameraPosition.builder().target(MELBOURNE_CITY_CENTER).zoom(12).bearing(0).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(melbourne));

        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker)
            {
                Service service = map.get(marker.getTitle());

                PlaceDetailsOther placeDetailsOther = new PlaceDetailsOther();
                //placeDetailsOther.transmitPlaceObjectToPlaceDetailFragment(service,favouriteList,userCommentList);
                placeDetailsOther.transmitPlaceObjectToPlaceDetailFragment(service);

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.replace(R.id.fragment_content,placeDetailsOther);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

    }

    public void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        {
            mGoogleMap.setMyLocationEnabled(true);
        }
        else
        {
            getActivity().requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},12345);
        }
    }

    /**
     * Request the permission and grant it.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode)
        {
            case 12345:
            {
                if (grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
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
                    Toast.makeText(getContext(), "Please grant the permission", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void setUpCluster() {
        clusterManager = new ClusterManager<ServiceClusterItem>(getContext(), mGoogleMap);
        mGoogleMap.setOnCameraIdleListener(clusterManager);
        mGoogleMap.setOnInfoWindowClickListener(clusterManager);
        mGoogleMap.setOnMarkerClickListener(clusterManager);
        clusterManager.setRenderer(new OwnIconRendered(getContext(),mGoogleMap,clusterManager));
    }

    public void controlCameraMoving() {
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

    public void transmitToServiceMapFragment(ArrayList<Service> servicePlaceArrayList, ArrayList<Service> favouriteList, ArrayList<String> userCommentList) {
        this.servicePlaceArrayList = servicePlaceArrayList;
        this.favouriteList = favouriteList;
        this.userCommentList = userCommentList;
    }

    public void sendFilterConditionToMap(ArrayList<String> selectCheckBoxNameList,String categorySelected,String subCategorySelected) {
        this.selectCheckBoxNameList = selectCheckBoxNameList;
        this.categorySelected = categorySelected;
        this.subCategorySelected = subCategorySelected;
    }

    public void transmitToServiceMapFragment(ArrayList<Service> servicePlaceArrayList,String category) {
        this.servicePlaceArrayList = servicePlaceArrayList;
        this.category = category;
    }

    public class OwnIconRendered extends DefaultClusterRenderer<ServiceClusterItem> {

        public OwnIconRendered(Context context, GoogleMap map, ClusterManager<ServiceClusterItem> clusterManager)
        {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(ServiceClusterItem sci, MarkerOptions markerOptions)
        {
//            int height = 70;
//            int width = 70;
//            BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.food_marker);
//            Bitmap b=bitmapdraw.getBitmap();
//            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
//            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            markerOptions.title(sci.getName());
            if (sci.getAddress_2().length() != 0)
            {
                markerOptions.snippet(sci.getAddress_2());
            }
            else if (sci.getAddress_1().length() != 0 && sci.getAddress_2().length() == 0)
            {
                markerOptions.snippet(sci.getAddress_1());
            }
            super.onBeforeClusterItemRendered(sci, markerOptions);
        }
        @Override
        protected void onClusterItemRendered(ServiceClusterItem serviceClusterItem, Marker marker)
        {
            super.onClusterItemRendered(serviceClusterItem, marker);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.map_to_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Invoke when menu item is selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_mapToList)
        {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ServiceCategoryResult serviceCategoryResult = new ServiceCategoryResult();
            serviceCategoryResult.setListToCategoryResultFragment(servicePlaceArrayList,category,true);
            //serviceCategoryResult.sendFilterCondition(selectCheckBoxNameList,categorySelected,subCategorySelected);
            ft.replace(R.id.fragment_content,serviceCategoryResult);
            ft.addToBackStack(null);
            ft.commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addItemToCluster(Service service) throws InvocationTargetException, IllegalAccessException {
        ServiceClusterItem item = new ServiceClusterItem();
        item.addAllAttributesToList();
        item.setPosition(Double.parseDouble(service.getLatitude()), Double.parseDouble(service.getLongitude()));
        updateObjectAttributes(service,item,clusterManager,item.getAllAttributesToList());
    }

    /**
     * Create the food place item object, and update the attribute information.
     */
    public void updateObjectAttributes(Service service, ServiceClusterItem serviceClusterItem,ClusterManager<ServiceClusterItem> ClusterList,ArrayList<String> attributeList)
            throws InvocationTargetException, IllegalAccessException {
        for (int j=0;j<attributeList.size(); j++)
        {
            String name = attributeList.get(j);

            String attributeValue  = reflectGetMethod(service,name);

            String methodName = "set" + name.toUpperCase().charAt(0) + name.substring(1);

            try
            {
                Method method = serviceClusterItem.getClass().getMethod(methodName,String.class);
                method.invoke(serviceClusterItem,attributeValue);
            }
            catch (NoSuchMethodException e)
            {
                e.printStackTrace();
            }
        }
        ClusterList.addItem(serviceClusterItem);
    }

    public String reflectGetMethod(Service service,String attribute) throws InvocationTargetException, IllegalAccessException {
        String methodName = "get" + attribute.toUpperCase().charAt(0) + attribute.substring(1);
        String tmpResult = "";
        try
        {
            Method method = service.getClass().getMethod(methodName);
            tmpResult = (String)method.invoke(service);
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        return tmpResult;
    }
}
