package com.example.homelessservices;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by Eric on 7/10/17.
 */

public class ServiceClusterItem extends Service implements ClusterItem,Serializable {

    private transient LatLng position;

    public ServiceClusterItem() {
        super();
        position = new LatLng(-37.81303878836988,144.96597290039062);
    }

    public void setPosition(double lat,double lng)
    {
        this.position = new LatLng(lat,lng);
    }

    @Override
    public LatLng getPosition()
    {
        return position;
    }


    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeDouble(position.latitude);
        out.writeDouble(position.longitude);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        position = new LatLng(in.readDouble(), in.readDouble());
    }
}
