package com.ap.fietskorier;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PickupPackage extends AppCompatActivity {

    Button btn_scanPickupCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_package);
        btn_scanPickupCode = findViewById(R.id.button_scanPickupCode);
        btn_scanPickupCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
