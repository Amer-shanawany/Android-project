package com.ap.fietskorier;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Package {

    private GeoPoint geoSource;

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

    private GeoPoint geoDestination;
    private @ServerTimestamp Date timeStamp;//should be set to null when instantiating
    private double price;
    private String pickupQRUrl;
    private String ownerAddress;
    private String deliveryAddress;
    private String user_id;
    private User ownerUser;//owner / sender
    private User delivererUser = null;

    public String getDelivererID() {
        return delivererID;
    }

    public void setDelivererID(String delivererID) {
        this.delivererID = delivererID;
    }

    private String delivererID;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    private String emailDestination;

    public LatLng getSource_latlng() {
        return source_latlng;
    }

    public void setSource_latlng(LatLng source_latlng) {
        this.source_latlng = source_latlng;
    }

    public LatLng getDestination_latlng() {
        return destination_latlng;
    }

    public void setDestination_latlng(LatLng destination_latlng) {
        this.destination_latlng = destination_latlng;
    }

    private LatLng source_latlng;
    private LatLng destination_latlng;

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
        return true;
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
        if (isDelivered()) return R.drawable.ic_check_green;
        else if (isPicked()) return R.drawable.ic_check_orange;
        else return R.drawable.ic_check_red;

        //TODO RETURN THE APPROPRIATE STATUS!!!
    }

}
