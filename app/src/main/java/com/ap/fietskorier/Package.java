package com.ap.fietskorier;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Package {


    private GeoPoint geoSource;
    private GeoPoint geoDestination;
    private @ServerTimestamp Date timeStamp;//should be set to null when instantiating
    private int price;
    private User ownerUser;//owner / sender
    private User delivererUser = null;

    public User getOwnerUser() {
        return ownerUser;
    }

    public void setOwnerUser(User ownerUser) {
        this.ownerUser = ownerUser;
    }

    public User getDelivererUser() {
        return delivererUser;
    }

    public void setDelivererUser(User delivererUser) {
        this.delivererUser = delivererUser;
    }

    private boolean isPicked = false;
    private boolean isDelivered = false;

    public Package(GeoPoint geoSource, GeoPoint geoDestination, Date timeStamp, int price, User user) {
        this.geoSource = geoSource;
        this.geoDestination = geoDestination;
        this.timeStamp = timeStamp;
        this.price = price;
        this.ownerUser = user;
    }

    public GeoPoint getGeoSource() {
        return geoSource;
    }

    public void setGeoSource(GeoPoint geoSource) {
        this.geoSource = geoSource;
    }

    public GeoPoint getGeoDestination() {
        return geoDestination;
    }

    public void setGeoDestination(GeoPoint geoDestination) {
        this.geoDestination = geoDestination;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }



    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }



    public boolean isPicked() {
        return isPicked;
    }

    public void setPicked(boolean picked) {
        isPicked = picked;
    }

    public boolean isDelivered() {
        return isDelivered;
    }

    public void setDelivered(boolean delivered) {
        isDelivered = delivered;
    }

    @Override
    public String toString() {
        return "Package{" +
                "geoSource=" + geoSource +
                ", geoDestination=" + geoDestination +
                ", timeStamp=" + timeStamp +
                ", price=" + price +
                ", user=" + ownerUser +
                ", isPicked=" + isPicked +
                ", isDelivered=" + isDelivered +
                '}';
    }
}