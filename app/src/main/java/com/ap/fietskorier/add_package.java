package com.ap.fietskorier;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.GeoPoint;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;
//import com.google.maps.DirectionsApiRequest;
//import com.google.maps.GeoApiContext;
//import com.google.maps.PendingResult;
//import com.google.maps.model.DirectionsResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static com.ap.fietskorier.Constants.FINE_LOCATION;
import static com.ap.fietskorier.Constants.LOCATION_PERMISSION_REQUEST_CODE;

public class add_package extends AppCompatActivity  implements OnMapReadyCallback  {

    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private static final float DEFAULT_ZOOM = 13f;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private Place source_Place =null;
    private Place destination_Place=null;

    private double distance;
    //widgets
    Button save_info_btn;
    private ImageView mGps;
    EditText editText;
    FirebaseFirestore database;
    TextView priceTextView;

    public static final String SOURCE_ID ="Source ID";
    public static final String SOURCE_NAME="Source Name";
    public static final String SOURCE_ADDRESS="Source Address";
    public static final String SOURCE_LATLNG="Source LatLng";
    public static final String DESTINATION_ID="Destination ID";
    public static final String DESTINATION_NAME="Destination Name";
    public static final String DESTINATION_ADDRESS="Destination Address";
    public static final String DESTINATION_LATLNG="Destination LatLng";
    public static final String SOURCE_GEO = "Source GeoPoint";
    public static final String DESTINATION_GEO = "Destination GeoPoint";
    //GeoApiContext context = new GeoApiContext.builder();
    private GeoPoint geoSource;
     private GeoPoint geoDestination;
     private String TAG = "ADD_PACKAGE";
     private Marker  myLocationMarker;
     private Marker  sourceMarker;
     private Marker  destinationMarker;
    private Polyline polyline;
     private GeoApiContext geoApiContext = null;
//
    private DocumentReference mDocRef = FirebaseFirestore.getInstance()
             .collection("users")
             .document("Amer");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_package);

        save_info_btn = findViewById(R.id.save_Package_info);
        //Device Location
        mGps =   findViewById(R.id.ic_gps);
        priceTextView = findViewById(R.id.price);
        getLocationPermission();
        save_info_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFirestore(v);
            }
        });


        /**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */

        //TODO:Replace the API Key with a string value !!!
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyA82ZnHt02qAGgujAnXNisNFpbA_TIS6CQ");
        }

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragmentsSource = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.source_autocomplete_fragment);
        AutocompleteSupportFragment autocompleteFragmentsDestination = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.destination_autocomplete_fragment);
        //Restrict and ask for specific fields
        if (autocompleteFragmentsSource != null) {
            autocompleteFragmentsSource.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG
            ,Place.Field.ADDRESS));

        }
        if (autocompleteFragmentsDestination != null) {
            autocompleteFragmentsDestination.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG
                    ,Place.Field.ADDRESS));
        }

        autocompleteFragmentsSource.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                //  Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId()+", " + place.getAddress() + ", " + place.getLatLng());
                if (sourceMarker!=null){sourceMarker.remove();}

                source_Place = place;
                geoSource = new GeoPoint(place.getLatLng().latitude,place.getLatLng().longitude);
                //LatLng latLng, float zoom, String title
                moveCamera(source_Place.getLatLng(),DEFAULT_ZOOM);
                sourceMarker = mMap.addMarker(new MarkerOptions().title("From").position(source_Place.getLatLng()));
                sourceMarker.showInfoWindow();
                calculateDistance();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
        autocompleteFragmentsDestination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                 Log.i(TAG, "Place: " + place.getName() + ", " + place.getId()+", " + place.getAddress() + ", " + place.getLatLng());
                if (destinationMarker!=null){destinationMarker.remove();}

                destination_Place = place;
                geoDestination = new GeoPoint(place.getLatLng().latitude,place.getLatLng().longitude);
               // moveCamera(destination_Place.getLatLng(),DEFAULT_ZOOM,"Destination ",dest);
                moveCamera(destination_Place.getLatLng(), DEFAULT_ZOOM);
                destinationMarker = mMap.addMarker(new MarkerOptions()
                        .title("To")
                        .position(destination_Place.getLatLng()));
                destinationMarker.showInfoWindow();
                calculateDistance();
//                if(source_Place!=null){
//                    if(polyline!=null){
//                    polyline.remove();}
//                    polyline = mMap.addPolyline(new PolylineOptions()
//                            .clickable(false)
//                            .add(source_Place.getLatLng(),destination_Place.getLatLng())
//                    );
//
//                    //Get directiont
//                    calculateDirections(source_Place,destination_Place);
//                    //TODO: Add distance calculation (distance in Meters)
//                    priceTextView.setText("Price : "+ (distance  / 10)+"$");
//                }


            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
//        if (source_Place!=null){
//            moveCamera(source_Place.getLatLng(),DEFAULT_ZOOM,"Source ",sourceMarker);}
//        if(destination_Place!=null) {
//            moveCamera(destination_Place.getLatLng(), DEFAULT_ZOOM, "Destination ",destinationMarker);
//        }

     }


//
//    private void init(){
//        Log.d(TAG, "init: initializing");
//
//        source.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if(actionId == EditorInfo.IME_ACTION_SEARCH
//                || actionId == EditorInfo.IME_ACTION_DONE
//                || event.getAction() == KeyEvent.ACTION_DOWN
//                || event.getAction() == KeyEvent.KEYCODE_ENTER){
//                    //excute sarching methods
//                    geoLocate();
//
//                }
//
//                return false;
//            }
//        });
//
//    }

    private void calculateDistance(){
        if(destination_Place!=null&&source_Place!=null){
            if(polyline!=null){
                polyline.remove();}
            polyline = mMap.addPolyline(new PolylineOptions()
                    .clickable(false)
                    .add(source_Place.getLatLng(),destination_Place.getLatLng())
            );
            //Get directiont
            calculateDirections(source_Place,destination_Place);
            //TODO: Add distance calculation (distance in Meters)
            //priceTextView.setText("Price : "+ (distance  / 10)+"$");
        }

    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            if(currentLocation !=null){
                            moveCamera(new LatLng(currentLocation.getLatitude(),
                                            currentLocation.getLongitude()),
                                    DEFAULT_ZOOM);}// ,"My Location",myLocationMarker

                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(add_package.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }
    //TODO:Delete geoLocate use AutocompleteSearchFragment
    private void geoLocate(){
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = source_Place.getAddress();

        Geocoder geocoder = new Geocoder(add_package.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage() );
        }

        if(list.size() > 0){
            Address address = list.get(0);

            Log.d(TAG, "geoLocate: found a location: " + address.toString());
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM
                    );//address.getAddressLine(0),myLocationMarker

        }
    }

    //Move maps Camera

    private void moveCamera(LatLng latLng, float zoom ){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
//        //add a marker
//        if(!title.equals("My Location")){
//                    options.position(latLng)
//                    .title(title);
//            mMap.addMarker(options);
//        }

        hideSoftKeyboard();
    }

    //save to the Database
    public void saveFirestore(View view) {

        if(source_Place!=null && destination_Place!=null) {
            Map<String, Object> dataToSave = new HashMap<>();

            dataToSave.put(SOURCE_ID ,        source_Place.getId());
            dataToSave.put(SOURCE_NAME,       source_Place.getName());
            dataToSave.put(SOURCE_ADDRESS,    source_Place.getAddress());
            dataToSave.put(SOURCE_GEO, geoSource);
            dataToSave.put(SOURCE_LATLNG,     source_Place.getLatLng());

            dataToSave.put(DESTINATION_ID ,       destination_Place.getId());
            dataToSave.put(DESTINATION_NAME,      destination_Place.getName());
            dataToSave.put(DESTINATION_ADDRESS,   destination_Place.getAddress());
            dataToSave.put(DESTINATION_GEO,geoDestination);
            dataToSave.put(DESTINATION_LATLNG,    destination_Place.getLatLng());

            mDocRef.set(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Document has been saved! ");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Document was not saved!", e);
                }
            });
        }else{
            Toast.makeText(this,"please provide all the required info",Toast.LENGTH_LONG).show();

        }
    }


    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        // Get the SupportMapFragment and request notification when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(add_package.this);

        //initializing GeoApiContext
        if (geoApiContext==null){
            geoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_api_key))
                    .build();

        }

    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);


            mGps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: clicked gps icon");
                    getDeviceLocation();
                }
            });

            hideSoftKeyboard();
        }



        // Add polylines and polygons to the map. This section shows just
        // a single polyline. Read the rest of the tutorial to learn more.


        // Position the map's camera near Alice Springs in the center of Australia,
        // and set the zoom factor so most of Australia shows on the screen.
       // googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-23.684, 133.903), 4));


    }



    //Calculating the distance
    private void calculateDirections(Place s,Place d){
        Log.d(TAG, "calculateDirections: calculating directions.");

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                d.getLatLng().latitude,
                d.getLatLng().longitude
        );
        //generate directions
        DirectionsApiRequest directions = new DirectionsApiRequest(geoApiContext);

        directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(
                        s.getLatLng().latitude,
                        s.getLatLng().longitude
                )
        );
        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG, "calculateDirections: routes: " + result.routes[0].toString());
                Log.d(TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                Log.d(TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                Log.d(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
                        distance =  result.routes[0].legs[0].distance.inMeters;
                        //TODO: calculate better pricing !
                //
                priceTextView.setText("Price : "+ (distance  / 10)+"$");
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );

            }
        });
    }


}
