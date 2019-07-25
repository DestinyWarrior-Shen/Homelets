package com.example.homelessservices;

import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class OnePlaceMap extends Fragment implements OnMapReadyCallback
{
    private GoogleMap googleMap;
    private MapView mapView;
    private View view;
    private FoodPlace foodPlace;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_one_place_map, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) view.findViewById(R.id.mp);
        if (mapView != null)
        {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        MapsInitializer.initialize(getContext());
        googleMap = map;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        {
            googleMap.setMyLocationEnabled(true);
        }
        else
        {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},12345);
        }



        LatLng latLng = new LatLng(Double.parseDouble(foodPlace.getLatitude()),Double.parseDouble(foodPlace.getLongitude()));

        googleMap.addMarker(new MarkerOptions().position(latLng).title(foodPlace.getName()).
                snippet(foodPlace.getAddress_2()));
        CameraPosition melbourne = CameraPosition.builder().target(latLng).zoom(14).bearing(0).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(melbourne));

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
                        googleMap.setMyLocationEnabled(true);
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

    /**
     * This method is used for get the food place object from previous fragment.
     */
    public void transmitPlaceObjectToOnePlaceMapFragment(FoodPlace foodPlace)
    {
        this.foodPlace = foodPlace;
    }
}
