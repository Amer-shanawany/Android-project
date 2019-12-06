package com.ap.fietskorier;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SearchPackage extends AppCompatActivity {

    Button btn_packageDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_package);
        btn_packageDetails = findViewById(R.id.button_packageDetails);
        btn_packageDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToPickupPackage = new Intent(SearchPackage.this, PickupPackage.class);
                startActivity(intentToPickupPackage);
            }
        });
    }
}
