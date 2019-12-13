package com.ap.fietskorier;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentReference;

import java.util.LinkedList;

import static com.ap.fietskorier.Constants.COARSE_LOCATION;
import static com.ap.fietskorier.Constants.DESTINATION_ADDRESS;
import static com.ap.fietskorier.Constants.DESTINATION_EMAIL;
import static com.ap.fietskorier.Constants.ERROR_DIALOG_REQUEST;
import static com.ap.fietskorier.Constants.FINE_LOCATION;
import static com.ap.fietskorier.Constants.IS_PICKED;
import static com.ap.fietskorier.Constants.LOCATION_PERMISSION_REQUEST_CODE;
import static com.ap.fietskorier.Constants.OWNER_ID;
import static com.ap.fietskorier.Constants.PACKAGES_COLLECTIONS;
import static com.ap.fietskorier.Constants.PACKAGE_ID;
import static com.ap.fietskorier.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;
import static com.ap.fietskorier.Constants.PICKUP_QR_URL;
import static com.ap.fietskorier.Constants.SOURCE_ADDRESS;
import static com.ap.fietskorier.Constants.SOURCE_GEO;
import static com.ap.fietskorier.Constants.SOURCE_LATLNG;


public class SearchPackage extends AppCompatActivity
        implements OnMapReadyCallback {
    private static final String TAG = "SearchPackage";
    private boolean mLocationPermissionsGranted=false ;
    private GoogleMap mMap;
    private User user;
    private FirebaseFirestore db;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final float DEFAULT_ZOOM = 13f;

    private final LinkedList<Package> myDataset = new LinkedList<Package>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        user = ((UserClient)(getApplicationContext())).getUser();
        db = FirebaseFirestore.getInstance();



        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);
//         Get the SupportMapFragment and request notification
//         when the map is ready to be used.
         SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
               .findFragmentById(R.id.map);
         mapFragment.getMapAsync(this);
        getLocationPermission();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

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
                    Toast.makeText(SearchPackage.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        checkMapServices();
        Toast.makeText(this,"Map is ready ",Toast.LENGTH_SHORT).show();
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.

        //Todo: here !  Get the data from Firebase and then draw the markers


        getPackagesFirebase();


//        LatLng sydney = new LatLng(-33.852, 151.211);
//        googleMap.addMarker(new MarkerOptions().position(sydney)
//                .title("Marker   in Sydney"));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void getPackagesFirebase(){
        CollectionReference packages = db.collection(PACKAGES_COLLECTIONS);

        packages
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }
                        int index = 0;
                        for (DocumentChange document : snapshots.getDocumentChanges()) {
                                    index++;
                                    Log.d("FireBase", "Getting a package " + document.getDocument().getData());

                                    if(document.getDocument().getData().get(Constants.PRICE)!=null){
                                        Package addPackage = new Package(user,
                                                document.getDocument().getString(SOURCE_ADDRESS),
                                                document.getDocument().getString(DESTINATION_ADDRESS),
                                                document.getDocument().getString(DESTINATION_EMAIL),
                                                document.getDocument().getDouble(Constants.PRICE)
                                        );
                                        addPackage.setPackageID(document.getDocument().getString(PACKAGE_ID));
                                        addPackage.setEmailDestination(document.getDocument().getString(DESTINATION_EMAIL));
                                        addPackage.setDeliveryAddress(document.getDocument().getString(DESTINATION_ADDRESS));
                                        addPackage.setOwnerAddress(document.getDocument().getString(SOURCE_ADDRESS));
                                        addPackage.setPickupQR(document.getDocument().getString(PICKUP_QR_URL));
                                        //addPackage.setSource_latlng(document.getDocument().getGeoPoint(SOURCE_GEO));
                                        Log.d(TAG, "LATLNGXX"+document.getDocument().getGeoPoint(SOURCE_GEO).toString());
                                        LatLng temp = new LatLng(document.getDocument().getGeoPoint(SOURCE_GEO).getLatitude(),document.getDocument().getGeoPoint(SOURCE_GEO).getLongitude());
                                        addPackage.setSource_latlng(temp);
                                        mMap.addMarker(new MarkerOptions().position(temp).title("Package "+index));
                                        Log.d(TAG, temp.toString());
                                        //Todo: add isPicked & is delivered flags
                                        myDataset.add(addPackage);
                                        //myAdapter.notifyDataSetChanged();
                                        }

                            }
                        }


                });

        }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:
                //
                if(grantResults.length>0){

                    for (int i =0 ; i < grantResults.length;i++){
                        if (grantResults[i]!=PackageManager.PERMISSION_GRANTED ){

                            mLocationPermissionsGranted =false;
                            return;
                        }

                    }
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();

                }


        }
    }

    private void initMap(){
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(SearchPackage.this);
    }

    private void getLocationPermission(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
            }
            else{
                ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
            }

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    public boolean checkMapServices(){
        if(isServicesOK()){
            if(isMapsEnabled()){
                return true;
            }
        }
        return false;
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(SearchPackage.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occurred but we can resolve it
            Log.d(TAG, "isServicesOK: an error occurred but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(SearchPackage.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void moveCamera(LatLng latLng, float zoom ){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
//        //add a marker
//        if(!title.equals("My Location")){
//                    options.position(latLng)
//                    .title(title);
//            mMap.addMarker(options);
//        }

     }
//
//    private void buildAlertMessageNoGps() {
//        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
//                .setCancelable(false)
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
//                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
//                    }
//                });
//        final AlertDialog alert = builder.create();
//        alert.show();
//    }

//    public boolean isMapsEnabled(){
//        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
//
//        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
//            buildAlertMessageNoGps();
//            return false;
//        }
//        return true;
//    }


//    public boolean checkMapServices(){
//        if(isServicesOK()){
//            if(isMapsEnabled()){
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public boolean isServicesOK(){
//        Log.d(TAG, "isServicesOK: checking google services version");
//
//        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(SearchPackage.this);
//
//        if(available == ConnectionResult.SUCCESS){
//            //everything is fine and the user can make map requests
//            Log.d(TAG, "isServicesOK: Google Play Services is working");
//            return true;
//        }
//        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
//            //an error occurred but we can resolve it
//            Log.d(TAG, "isServicesOK: an error occurred but we can fix it");
//            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(SearchPackage.this, available, ERROR_DIALOG_REQUEST);
//            dialog.show();
//        }else{
//            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
//        }
//        return false;
//    }
}