package com.ap.fietskorier;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.AlertDialog;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static com.ap.fietskorier.Constants.PERMISSIONS_REQUEST_ENABLE_CAMERA;

/**
 * Created by akhil on 28-12-16.
 */

public class QrReader extends Activity implements ZXingScannerView.ResultHandler {

    public  static final String deliveryQrReply = "com.ap.fietskorier.deliveryQrReply";

    private ZXingScannerView mScannerView;
    String TAG="QRREADER";
    String packageID;
    String pickupCode;
    String deliveryCode;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        // Here, thisActivity is the current activity

        if (ContextCompat.checkSelfPermission(QrReader.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(QrReader.this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST_ENABLE_CAMERA);

            // MY_PERMISSIONS_REQUEST_CAMERA is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        } else {
            // Permission has already been granted
        }

        Bundle extras = getIntent().getExtras();
        packageID = extras.getString("PACKAGEID");
        pickupCode = extras.getString("PICKUPCODE");
        deliveryCode = extras.getString("DELIVERYCODE");

        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view

    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.v(TAG, rawResult.getText()); // Prints scan results
        Log.v(TAG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

        String scannedCode = rawResult.getText();
        if (scannedCode.contains(pickupCode)) {  //SO WEIRD BUT OTHERWISE IT DOESN'T WORK!!!
            Alert("You succesfully picked up package " + packageID + ".");

        }
        else if (scannedCode.contains(deliveryCode)) {
            Alert("You succesfully delivered package "+ packageID + ".");

        }
        else Alert("This is not valid code for package "+ packageID + ".");


        // call the alert dialog
        //Alert(X);

    }

    public void Alert(String messsage){
        AlertDialog.Builder builder = new AlertDialog.Builder(QrReader.this);
        builder.setTitle("Packaged");
        builder.setMessage(messsage)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // back to previous activity
                        finish();
                    }
                })
                //.setNegativeButton("Scan Again", new DialogInterface.OnClickListener() {
                //    public void onClick(DialogInterface dialog, int id) {
                //        // User cancelled the dialog
                //       // If you would like to resume scanning, call this method below:
                //       mScannerView.resumeCameraPreview(QrReader.this);
                //   }
                //   })
                ;
        // Create the AlertDialog object and return it
        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // camera related task you need to do.
                } else {
                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}
