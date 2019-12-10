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
    public static  final String OWNER_ID ="owner_id" ;
    public static final String PACKAGE_ID=       "pakcage_id";
    public static final String PRICE =    "price";
    public static final String DISTANCE =  "distance";
    public static final String DESTINATION_EMAIL =  "destination_email";
    public static final String DESTINATION_ADDRESS = "destination_address";
    public static final String SOURCE_ADDRESS = "source_address";
    public static final  String DESTINATION_LATLNG = "destination_lat_lon";
    public static final String DESTINATION_ID = "destination_id";
    public static final String SOURCE_LATLNG= "source_lat_lon";
    public static final String SOURCE_ID = "source_id";
    //public static final String  =   "saveFirestore: "  ;
}
