package com.ap.fietskorier;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.ap.fietskorier.Constants.ERROR_DIALOG_REQUEST;
import static com.ap.fietskorier.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class HomeActivity extends AppCompatActivity {
    Button btnLogout;
    Button btnPickUpPackage;
    Button btnAddPackage;
    //FirebaseAuth mFirebaseAuth;
    //FirebaseUser user  ;
    String uid ;//unique UserID after verification
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private boolean mLocationPermissionGranted = false;
    private String TAG ="HomeActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btnLogout = findViewById(R.id.btn_logOut);
        btnPickUpPackage = findViewById(R.id.btn_pickUp);
        btnAddPackage = findViewById(R.id.btn_add_package);


        //user this line to get the current user information
        User user = ((UserClient)(getApplicationContext())).getUser();

        // no need to instantiate FirebaseAuthentication method
        //in order to check if the user is Signed in
        //user = FirebaseAuth.getInstance().getCurrentUser();

        Log.i(TAG, "user ID: "+user.getUser_id()
        +" User email address: "  + user.getEmail()
        +" User name: " +user.getUsername());
        checkMapServices();
        if(user.getUser_id()!=null){
        uid = user.getUser_id();
        }else{
            Toast.makeText(this,"User is null",Toast.LENGTH_LONG).show();
        }

        // Write a message to the database
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(uid).setValue(user);



        btnAddPackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intToAddPackages = new Intent(HomeActivity.this,add_package.class);

                startActivity(intToAddPackages);



            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intToMain = new Intent(HomeActivity.this,LoginActivity.class);
                startActivity(intToMain);
            }
        });
        btnPickUpPackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intToMaps = new Intent(HomeActivity.this, activity_maps.class);
                startActivity(intToMaps);
            }
        });

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

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(HomeActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occurred but we can resolve it
            Log.d(TAG, "isServicesOK: an error occurred but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(HomeActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

}
