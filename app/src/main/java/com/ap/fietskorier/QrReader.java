package com.ap.fietskorier;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.app.AlertDialog;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.Result;
import com.google.zxing.WriterException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static com.ap.fietskorier.Constants.DELIVERER_ID;
import static com.ap.fietskorier.Constants.DELIVERY_QR_URL;
import static com.ap.fietskorier.Constants.IS_DELIVERED;
import static com.ap.fietskorier.Constants.IS_PICKED;
import static com.ap.fietskorier.Constants.OWNER_ID;
import static com.ap.fietskorier.Constants.PACKAGES_COLLECTIONS;
import static com.ap.fietskorier.Constants.PACKAGE_ID;
import static com.ap.fietskorier.Constants.PERMISSIONS_REQUEST_ENABLE_CAMERA;
import static com.ap.fietskorier.Constants.PICKUP_QR_URL;

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
    User user;
    Bitmap bitmap;

    private DocumentReference mDocRef;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        // Here, thisActivity is the current activity

        user = ((UserClient)(getApplicationContext())).getUser();

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
        mDocRef   = FirebaseFirestore.getInstance()
                .collection(PACKAGES_COLLECTIONS)
                .document(packageID);
        if (extras.getString("PICKUPCODE")!=null){
            pickupCode = extras.getString("PICKUPCODE");
        }
        if( extras.getString("DELIVERYCODE")!=null) {
            deliveryCode = extras.getString("DELIVERYCODE");
        }
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
        if (scannedCode.contentEquals(pickupCode)) {  //SO WEIRD BUT OTHERWISE IT DOESN'T WORK!!!
            Alert("You succesfully picked up package " + packageID + ".");
                //todo: 1 write ispicked to firestore Done
            //          2 add deliverer Id to firestore
            //          3 generate Qr code  packageID + delivererId
            //          4 send email through nodemailer

            saveFirestore(IS_PICKED,true,user.getUser_id());

        }
        else if (scannedCode.contentEquals(deliveryCode)) {
            Alert("You succesfully delivered package "+ packageID + ".");
                //todo add balcance  -- -- -- show balance !!
                 //  write is delivered to firestore
                    //delete delivery !
                          // write email to the owner your package is delivered !
                saveFirestore(IS_DELIVERED,true,null);
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
                });
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

    public void saveFirestore(String data,boolean flag, String otherdata) {

            Map<String, Object> dataToSave = new HashMap<>();
            if(otherdata!=null){
                dataToSave.put(DELIVERER_ID,otherdata);
            }

            switch (data){
                case IS_DELIVERED:
                dataToSave.put(IS_DELIVERED,flag);

                break;
                case IS_PICKED:
                dataToSave.put(IS_PICKED,flag);
                    GenerateQR();
                break;
            }

        mDocRef.update(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
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
        }

    //FireCloud reference
    // Create a storage reference from our app
    FirebaseStorage storage = FirebaseStorage.getInstance();


    // Create a storage reference from our app
    StorageReference storageRef = storage.getReference();

    // Create a reference to "mountains.jpg"
    // Here you give the image NAME
    StorageReference mountainsRef ;
    public void GenerateQR(){

        String DownloadURL = "";
        mountainsRef  = storageRef.child("QRdelivery/"+packageID+".jpg");
        //qrGenerator(view,mDocRef.getId());
        String inputValue = packageID+user.getUser_id();



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
                        //Log.d(TAG, )
                        mountainsRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Uri downloadUrl = uri;
                                Log.d(TAG, "OK");
                                Log.d(TAG, downloadUrl.toString());
                                String url = downloadUrl.toString();

                                //update firebase
                                Map<String, Object> dataToSave = new HashMap<>();
                                dataToSave.put(DELIVERY_QR_URL, url);
                                mDocRef.update(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
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

                               ////intent naar de geupdate lijst...
                               //Intent intent = new Intent(add_package.this, ShipmentActivity.class);
                               //startActivity(intent);
                            }
                        });
                        //Log.d(TAG, "OK");
                        //Log.d(TAG,taskSnapshot.getMetadata().getCustomMetadataKeys(""));
                        //Log.d(TAG, link);
                        //Log.d(TAG, mountainsRef.getDownloadUrl().toString());
                    }
                });
                Log.d(TAG, "GenerateQR: is successfull");
            } catch (WriterException e) {
                Log.v(TAG, e.toString());

            }
        }else {


            Log.d(TAG, "GenerateQR: Something went wrong");
        }

    }

}
