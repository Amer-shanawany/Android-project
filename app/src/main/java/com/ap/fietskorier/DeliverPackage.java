package com.ap.fietskorier;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static com.ap.fietskorier.Constants.DELIVERER_ID;
import static com.ap.fietskorier.Constants.PERMISSIONS_REQUEST_ENABLE_CAMERA;

public class DeliverPackage extends AppCompatActivity {

    public static final String EXTRA_DELIVERYCODE = "com.ap.fietskoerier.extra.DELIVERYCODE";
    static final int DELIVERY_REQUEST = 1;

    Button btn_scanDeliveryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliver_package);

        Bundle extras = getIntent().getExtras();

        String packageId = extras.getString("ID");
        String packageSourceAddress = extras.getString("SOURCE");
        String packageDestinationAddress = extras.getString("DESTINATION");
        String packageDestinationEmail = extras.getString("EMAIL");
        String delivererID = extras.getString(DELIVERER_ID);
        //String packagePickupQRCode = extras.getString("QR");

        TextView packageIden = findViewById(R.id.packageID);
        TextView packageSource = findViewById(R.id.sourceAddress);
        TextView packageDestAdd = findViewById(R.id.destinationAddress);
        TextView packageDestMail = findViewById(R.id.destinationEmail);


        packageIden.setText(packageId);
        packageSource.setText(packageSourceAddress);
        packageDestAdd.setText(packageDestinationAddress);
        packageDestMail.setText(packageDestinationEmail);

        btn_scanDeliveryCode = findViewById(R.id.button_scanDeliveryCode);
        btn_scanDeliveryCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle extras = new Bundle();
                Intent deliveryIntent = new Intent(DeliverPackage.this, QrReader.class);
                //TODO hier de goede variabelen zetten
                extras.putString("PACKAGEID", packageId);
                extras.putString("PICKUPCODE", "#@&"); //zo laten staan!!!
                extras.putString("DELIVERYCODE", packageId+delivererID);

                deliveryIntent.putExtras(extras);
                startActivity(deliveryIntent);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DELIVERY_REQUEST) {
            if (resultCode == RESULT_OK) {
                String reply = data.getStringExtra(QrReader.deliveryQrReply);

            }
        }
    }
}
