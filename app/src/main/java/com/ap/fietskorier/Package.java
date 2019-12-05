package com.ap.fietskorier;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Package {

    private String ownerAddress;
    private String deliveryAddress;
    //private GeoPoint geoSource;
    //private GeoPoint geoDestination;
    private @ServerTimestamp Date timeStamp;//should be set to null when instantiating//
    private double  price;
    private User ownerUser;//owner / sender
    private User delivererUser = null;

    private String emailDestination;


    private boolean isPicked = false;
    private boolean isDelivered = false;
    private String packageID;

    public String getOwnerAddress() {
        return ownerAddress;
    }

    public void setOwnerAddress(String ownerAddress) {
        this.ownerAddress = ownerAddress;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getEmailDestination() {
        return emailDestination;
    }

    public void setEmailDestination(String emailDestination) {
        this.emailDestination = emailDestination;
    }

    public void setPackageID(String packageID) {
        this.packageID = packageID;
    }

    public Package(User user, String addressSource, String addressDestination, String emailDestination, double price) {
        this.ownerUser= user;
        this.ownerAddress = addressSource;
        this.deliveryAddress = addressDestination;
        this.emailDestination = emailDestination;
        this.price = price;

    }

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
    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getPackageID() {
        return packageID;
    }


    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
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

}
