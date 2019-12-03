package com.ap.fietskorier;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;

import com.google.zxing.WriterException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;
import static com.ap.fietskorier.Constants.FINE_LOCATION;
import static com.ap.fietskorier.Constants.LOCATION_PERMISSION_REQUEST_CODE;
import static com.ap.fietskorier.Constants.PACKAGES_COLLECTIONS;

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
    EditText destination_Email =null;
    private double price=1;
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


    //QR Code
    Bitmap bitmap;
    //String temp = Environment.getDataDirectory().getPath() + "/QRCode/";
    String savePath = Environment.getDataDirectory().getPath() + "/QRCode/";
    //String path = Environment.getDownloadCacheDirectory().getPath() + "/QRCode/";
    //FireStore reference
    private DocumentReference mDocRef = FirebaseFirestore.getInstance()
             .collection(PACKAGES_COLLECTIONS)
        .document();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //use this line to get the current user information
        User user = ((UserClient)(getApplicationContext())).getUser();


        setContentView(R.layout.activity_add_package);

        save_info_btn = findViewById(R.id.save_Package_info);
        //Device Location
        mGps =   findViewById(R.id.ic_gps);
        priceTextView = findViewById(R.id.price);
        destination_Email = findViewById(R.id.destination_email_editText);
        getLocationPermission();
        save_info_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: clicking save package information button updates the same document
                //TODO: so it's better to have two buttons, update info and save
                // Todo: or to add a function for the current button that will takes the
                //todo  the user back
                //todo : or use a dialog inflater to ask for conformation when placing a package
                // i prefer the lase option
                AlertSavePackage(v);
                //Done !
                //saveFirestore(v);
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

        if(source_Place!=null && destination_Place!=null&&destination_Email!=null) {
            Map<String, Object> dataToSave = new HashMap<>();
            //Todo: Hash/Encode the Package ID/Information
           GenerateQR();
            //dataToSave.put("QrBitmap",bitmap);
            //QRGen("Hi   ");
            dataToSave.put("PackageID",mDocRef.getId());
            //dataToSave.put(SOURCE_NAME,       source_Place.getName());
            //dataToSave.put(SOURCE_GEO, geoSource);
            dataToSave.put("Price",price);
            dataToSave.put("Distance",distance);
            dataToSave.put("Destination Email",destination_Email);
            //dataToSave.put(DESTINATION_NAME,      destination_Place.getName());
            dataToSave.put(DESTINATION_LATLNG,    destination_Place.getLatLng());
            dataToSave.put(DESTINATION_ID ,       destination_Place.getId());
            dataToSave.put(DESTINATION_ADDRESS,   destination_Place.getAddress());

            dataToSave.put(SOURCE_LATLNG,     source_Place.getLatLng());
            dataToSave.put(SOURCE_ID ,        source_Place.getId());
            dataToSave.put(SOURCE_ADDRESS,    source_Place.getAddress());

            //dataToSave.put(DESTINATION_GEO,geoDestination);

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
                price = distance*0.002;
                priceTextView.setText("Price : "+ price+"$");
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );

            }
        });
    }

    public void  AlertSavePackage(View v) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you wanna place this Package?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialg , int which) {
                        //add the save to database method here

                        saveFirestore(v);
                    }
                })
        .setNegativeButton("Cancel",null);
        final AlertDialog alert = builder.create();
        alert.show();
    }



    //Qr Generator


//
//    public void qrGenerator(View v){
//        try {
//            //setting size of qr code
//            WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
//            Display display = manager.getDefaultDisplay();
//            Point point = new Point();
//            display.getSize(point);
//            int width = point.x;
//            int height = point.y;
//            int smallestDimension = width < height ? width : height;
//
//            //EditText qrInput = (EditText) findViewById(R.id.qrInput);
//            //String qrCodeData = qrInput.getText().toString();
//
//            //setting parameters for qr code
//            String charset = "UTF-8"; // or "ISO-8859-1"
//            Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<>();
//            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
//                //The first value is the encoded String
//            createQRCode(mDocRef.getId(), charset, hintMap, smallestDimension, smallestDimension);
//
//        } catch (Exception ex) {
//            Log.e("QrGenerate",ex.getMessage());
//        }
//    }
//
//    public Bitmap createQRCode(String qrCodeData,String charset, Map hintMap, int qrCodeheight, int qrCodewidth){
//
//        try {
//            //generating qr code in bitmatrix type
//            BitMatrix matrix = new MultiFormatWriter().encode(new String(qrCodeData.getBytes(charset), charset), BarcodeFormat.QR_CODE, qrCodewidth, qrCodeheight, hintMap);
//            //converting bitmatrix to bitmap
//            int width = matrix.getWidth();
//            int height = matrix.getHeight();
//            int[] pixels = new int[width * height];
//            // All are 0, or black, by default
//            for (int y = 0; y < height; y++) {
//                int offset = y * width;
//                for (int x = 0; x < width; x++) {
//                    pixels[offset + x] = matrix.get(x, y) ? BLACK : WHITE;
//                }
//            }
//
//              bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
//            return  bitmap;
//            //setting bitmap to image view
//            //ImageView myImage = (ImageView) findViewById(R.id.imageView1);
//            //myImage.setImageBitmap(bitmap);
//        }catch (Exception er){
//            Log.e("QrGenerate",er.getMessage());
//            return null;
//        }
//    }



    //FireCloud reference
    // Create a storage reference from our app
    FirebaseStorage storage = FirebaseStorage.getInstance();


    // Create a storage reference from our app
    StorageReference storageRef = storage.getReference();

    // Create a reference to "mountains.jpg"
    StorageReference mountainsRef = storageRef.child("qrpackages/"+mDocRef.getId()+".jpg");

    // Create a reference to 'images/mountains.jpg'
    //StorageReference mountainImagesRef = storageRef.child("qrpackages/mountains.jpg");


    public void GenerateQR(){
        //qrGenerator(view,mDocRef.getId());
         String inputValue = mDocRef.getId();

        if (inputValue.length() > 0) {
            WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            int width = point.x;
            int height = point.y;
            int smallerDimension = width < height ? width : height;
            smallerDimension = smallerDimension * 3 / 4;
            QRGEncoder qrgEncoder = new QRGEncoder(inputValue, null, QRGContents.Type.TEXT, smallerDimension);
            try {
                bitmap = qrgEncoder.encodeAsBitmap();
                //qrImage.setImageBitmap(bitmap);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();
                //this upoad method, save QR-images to Firebase Cloud Storage
                UploadTask uploadTask = mountainsRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        // ...
                    }
                });
                Log.d(TAG, "GenerateQR: is successfull");
            } catch (WriterException e) {
                Log.v(TAG, e.toString());

            }
        }else {
            //edtValue.setError("Required");
        }
        //TODO this save to local path method is not working.. DELETE If not solved
            //not needed
            File path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        Log.d(TAG, "GenerateQR() returned: Path: " +Environment.DIRECTORY_DOWNLOADS );
            File file = new File(path,"this.jpg");

        try {
            // Very simple code to copy a picture from the application's
            // resource into the external file.  Note that this code does
            // no error checking, and assumes the picture is small (does not
            // try to copy it in chunks).  Note that if external storage is
            // not currently mounted this will silently fail.
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();
            ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);
            //InputStream is = bs ;
            OutputStream os = new FileOutputStream(file);
            byte[] data = new byte[bs.available()];
            //it writes a file but unreadable !!!
            os.write(data);
            bs.close();
            os.close();
            Log.d(TAG, "GenerateQR: is Saved to local Storage ");

            // Tell the media scanner about the new file so that it is
            // immediately available to the user.
            MediaScannerConnection.scanFile(this,
                    new String[] { file.toString() }, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }});



        } catch (IOException e) {
            // Unable to create file, likely because external storage is
            // not currently mounted.
            Log.w("ExternalStorage", "Error writing " + file, e);
        }


//        try {
//            File file = new File(getExternalFilesDir(null),"Qr.jpg");
//            QRGSaver.save(getExternalFilesDir(bitmap).toString());
//
//            save = QRGSaver.save(savePath, "QR", bitmap, QRGContents.ImageType.IMAGE_JPEG);
//            result = save ? "Image Saved" : "Image Not Saved";//result alway false
//            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }
//
//    public Bitmap QRGen(String inputValue){
//        // Initializing the QR Encoder with your value to be encoded, type you required and Dimension
//        QRGEncoder qrgEncoder = new QRGEncoder(inputValue, null, QRGContents.Type.TEXT, 400);
//        try {
//            // Getting QR-Code as Bitmap
//            bitmap = qrgEncoder.encodeAsBitmap();
//            // Setting Bitmap to ImageView
//            //qrImage.setImageBitmap(bitmap);
//            // Save with location, value, bitmap returned and type of Image(JPG/PNG).
//            QRGSaver.save(savePath,mDocRef.getId() , bitmap, QRGContents.ImageType.IMAGE_JPEG);
//            Toast.makeText(this, "A QR image is saved to: "+savePath, Toast.LENGTH_SHORT).show();
//        } catch (WriterException e) {
//            Log.v(TAG, e.toString());
//        }
//
//        return bitmap;
//    }
}
