package com.ap.fietskorier;

import android.Manifest;
import android.util.Log;

public class Constants {
    public static final int ERROR_DIALOG_REQUEST =9001;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS =9002;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION= 9003;
    public static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    public static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static  final  int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    public static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final String USERS_COLLECTION = "users";
    public static final String PACKAGES_COLLECTIONS = "packages";
    public static  final String OWNER_ID ="Owner ID" ;
    public static final String PACKAGE_ID=       "Package ID";
    public static final String PRICE =    "Price";
    public static final String DISTANCE =  "Distance";
    public static final String DESTINATION_EMAIL =  "Destination Email";
    public static final String DESTINATION_ADDRESS = "Destination Address";
    public static final String SOURCE_ADDRESS = "Source Address";

    //public static final String  =   "saveFirestore: "  ;
}
