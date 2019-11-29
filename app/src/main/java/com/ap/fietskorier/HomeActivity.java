package com.ap.fietskorier;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {
    Button btnLogout;
    Button btnPickUpPackage;
    Button btnAddPackage;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btnLogout = findViewById(R.id.btn_logOut);
        btnPickUpPackage = findViewById(R.id.btn_pickUp);
        btnAddPackage = findViewById(R.id.btn_add_package);
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
                Intent intToMain = new Intent(HomeActivity.this,MainActivity.class);
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

}
