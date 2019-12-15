package com.ap.fietskorier;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static com.ap.fietskorier.Constants.OWNER_ID;

public class PickupPackage extends AppCompatActivity {

    public static final String EXTRA_PICKUPCODE = "com.ap.fietskoerier.extra.PICKUPCODE";

    Button btn_scanPickupCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_package);

        Bundle extras = getIntent().getExtras();

        String packageId = extras.getString("ID");
        String packageSourceAddress = extras.getString("SOURCE");
        String packageDestinationAddress = extras.getString("DESTINATION");
        String packageDestinationEmail = extras.getString("EMAIL");
        String owner_ID = extras.getString(OWNER_ID);
        //String packagePickupQRCode = extras.getString("QR");

        TextView packageIden = findViewById(R.id.packageID);
        TextView packageSource = findViewById(R.id.sourceAddress);
        TextView packageDestAdd = findViewById(R.id.destinationAddress);
        TextView packageDestMail = findViewById(R.id.destinationEmail);

        packageIden.setText(packageId);
        packageSource.setText(packageSourceAddress);
        packageDestAdd.setText(packageDestinationAddress);
        packageDestMail.setText(packageDestinationEmail);

        btn_scanPickupCode = findViewById(R.id.button_scanPickupCode);
        btn_scanPickupCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle extras = new Bundle();
                Intent pickupIntent = new Intent(PickupPackage.this, QrReader.class);
                //TODO hier de goede variabelen zetten
                extras.putString("PACKAGEID", packageId);
                extras.putString("PICKUPCODE", packageId+owner_ID);
                 extras.putString("DELIVERYCODE", "#@&"); //zo laten staan!!!
                pickupIntent.putExtras(extras);
                startActivity(pickupIntent);

            }
        });
    }
}
