package com.ap.fietskorier;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.LinkedList;

import static com.ap.fietskorier.Constants.PACKAGES_COLLECTIONS;

public class ShipmentActivity extends AppCompatActivity {

    private CollectionReference myColRef = FirebaseFirestore.getInstance().collection(PACKAGES_COLLECTIONS);
    private final LinkedList<Package> myDataset = new LinkedList<Package>();

    //RECYCLERVIEW
    private RecyclerView recyclerView;
    private RecyclerView.Adapter myAdapter;
    private RecyclerView.LayoutManager myLayoutManager;
    //!!!

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intToAddPackage = new Intent(ShipmentActivity.this, add_package.class);
                startActivity(intToAddPackage);

                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        Package pakje1 = new Package("fdsfe5678rreer","Camelialei 13","2170 Merksem", false);

        Package pakje2 = new Package("ff89rze34Ftyuy","Bredabaan 256","2170 Merksem", false);
        Package pakje3 = new Package("34F5679DIOP324","Meir 234","2000 Antwerpen", false);

        myDataset.add(pakje1);
        myDataset.add(pakje2);
        myDataset.add(pakje3);
        myDataset.add(pakje2);
        myDataset.add(pakje1);
        //myDataset.add(pakje2);
        //myDataset.add(pakje3);
        //myDataset.add(pakje2);


        //RECYCLERVIEW
        recyclerView = (RecyclerView) findViewById(R.id.shipments_recyclerview);
        //fixed size
        recyclerView.setHasFixedSize(true);
        //linear layout manager
        myLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(myLayoutManager);
        //adapter
        myAdapter = new PackageAdapter(this, myDataset, new PackageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Package myPackage) {
                Intent intToViewPackage = new Intent(ShipmentActivity.this, ViewPackage.class);
                startActivity(intToViewPackage);

                Toast.makeText(getBaseContext(),myPackage.getPackageID(), Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(myAdapter);
        //!!!

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_shipment:
                Intent intToShipment = new Intent(this, ShipmentActivity.class);
                startActivity(intToShipment);
                break;
            case R.id.action_delivery:
                Intent intToDelivery = new Intent(this, DeliveryActivity.class);
                startActivity(intToDelivery);
                break;
            case R.id.action_profile:
                Intent intToMyProfile = new Intent(this, MyProfileActivity.class);
                startActivity(intToMyProfile);
                break;
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intToMain = new Intent(ShipmentActivity.this, LoginActivity.class);
                startActivity(intToMain);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
