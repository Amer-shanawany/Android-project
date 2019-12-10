package com.ap.fietskorier;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Package {

    private GeoPoint geoSource;
    private GeoPoint geoDestination;
    private @ServerTimestamp Date timeStamp;//should be set to null when instantiating
    private double price;
    private String pickupQRUrl;
    private String ownerAddress;
    private String deliveryAddress;
    //private GeoPoint geoSource;
    //private GeoPoint geoDestination;
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

    private String adres1;
    private String adres2;

    //even een gewone constructor
    public Package(String _packageID, String _adres1, String _adres2, Boolean _isDelivered) {
        packageID = _packageID;
        adres1 = _adres1;
        adres2 = _adres2;
        isDelivered = _isDelivered;
    }
    public Package(GeoPoint geoSource, GeoPoint geoDestination, Date timeStamp, int price, User user) {
        this.geoSource = geoSource;
        this.geoDestination = geoDestination;
        this.timeStamp = timeStamp;
        this.price = price;
        this.ownerUser = user;
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

    public String getAddress1() {
        return adres1;
    }

    public String getAddress2() {
        return adres2;
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

    public String getPickupQR() { return pickupQRUrl;
    }

    public void setPickupQR(String code) { this.pickupQRUrl = code;
    }

    public int getStatus() {
        return R.drawable.ic_check_red;

        //TODO RETURN THE APPROPRIATE STATUS!!!
    }

}
