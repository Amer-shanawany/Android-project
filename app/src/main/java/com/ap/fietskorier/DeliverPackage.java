package com.ap.fietskorier;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DeliverPackage extends AppCompatActivity {
    Button btn_scanDeliveryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliver_package);

        btn_scanDeliveryCode = findViewById(R.id.button_scanDeliveryCode);
        btn_scanDeliveryCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
