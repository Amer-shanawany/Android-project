package com.ap.fietskorier;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeActivity extends AppCompatActivity {
    Button btnLogout;
    Button btnPickUpPackage;
    Button btnAddPackage;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser user  ;
    String uid ;//unique UserID after verification
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btnLogout = findViewById(R.id.btn_logOut);
        btnPickUpPackage = findViewById(R.id.btn_pickUp);
        btnAddPackage = findViewById(R.id.btn_add_package);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user.getUid()!=null){
        uid = user.getUid();
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
