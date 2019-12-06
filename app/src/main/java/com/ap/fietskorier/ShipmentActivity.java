package com.ap.fietskorier;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.LinkedList;

import static com.ap.fietskorier.Constants.DESTINATION_EMAIL;
import static com.ap.fietskorier.Constants.PACKAGES_COLLECTIONS;
import static com.ap.fietskorier.Constants.PACKAGE_ID;
import static com.ap.fietskorier.Constants.PRICE;
import static com.ap.fietskorier.add_package.DESTINATION_ADDRESS;
import static com.ap.fietskorier.add_package.SOURCE_ADDRESS;
import static com.ap.fietskorier.add_package.SOURCE_ID;

public class ShipmentActivity extends AppCompatActivity {

    private static final String TAG = "ShipmentActivity" ;
    private final LinkedList<Package> myDataset = new LinkedList<Package>();

    //RECYCLERVIEW
    private RecyclerView recyclerView;
    private RecyclerView.Adapter myAdapter;
    private RecyclerView.LayoutManager myLayoutManager;
    //!!!
    private LinkedList<Package> packageList;
    private  FirebaseAuth mFirebaseAuth;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //make an instance of this user
        user = ((UserClient)(getApplicationContext())).getUser();
        //mFirebaseAuth = FirebaseAuth.getInstance();
        //FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        //TODO : Use //// Source can be CACHE, SERVER, or DEFAULT.
        //TODO Source source = Source.CACHE;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Create a reference to the cities collection
        CollectionReference packages = db.collection(PACKAGES_COLLECTIONS);
        packages.whereEqualTo("Owner ID",user.getUser_id()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for(QueryDocumentSnapshot document:task.getResult()){

                        //String temp = document.getDouble("Price").toString();
                        //Log.d(TAG, user.toString());
                        Log.d(TAG, document.getString(SOURCE_ADDRESS).toString());
                        Log.d(TAG, document.getString(DESTINATION_ADDRESS).toString());
                        Log.d(TAG, document.getString(DESTINATION_EMAIL));
                        Log.d(TAG, document.getDouble(PRICE).toString());

                        //User user, String addressSource, String addressDestination, String emailDestination, double price
                        Package tempPackage = new Package(user,
                                document.getString(SOURCE_ADDRESS),
                                document.getString(DESTINATION_ADDRESS),
                                document.getString(DESTINATION_EMAIL),
                                document.getDouble(PRICE)
                                );
                        tempPackage.setPackageID(document.getString(PACKAGE_ID));

                                 myDataset.add(tempPackage);
                    }
                }else{
                    Log.w(TAG, "Error getting documents.",task.getException() );
                }
            }
        });
        // Create a query against the collection.
        //Query query = packages.whereEqualTo(SOURCE_ID, user.getUser_id());
//
//        // retrieve  query results asynchronously using query.get()
//        Api
        //    ApiFuture<QuerySnapshot> querySnapshot = query.get();
//
//        foreah (){
//            //            Package temp  =new Package(
////                    price,
////                    user,
////                    mDocRef.getId(),
////                    source_Place.getAddress(),
////                    destination_Place.getAddress(),
////                    destination_Email.getText().toString());
//        }
//        for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
//            System.out.println(document.getId());
//        }


        //Package pakje1 = new Package("fdsfe5678rreer","Camelialei 13","2170 Merksem", false);
        Package package1 = new Package(null,"sourceAddress 1","Destination Address 1", "email1@receiver.com",9.3);
//
        //Package package2 = new Package(null,"sourceAddress 2","Destination Address 2", "email2@receiver.com",15.73);
//
        //Package package3 = new Package(null,"sourceAddress 3","Destination Address 3", "email3@receiver.com",32.3);
        ////Package pakje2 = new Package("ff89rze34Ftyuy","Bredabaan 256","2170 Merksem", false);
        ////Package pakje3 = new Package("34F5679DIOP324","Meir 234","2000 Antwerpen", false);
        //Package package4 = new Package(null,"hardcoded address","yes","fake@ever.us",6.66);
        myDataset.add(package1);
        //myDataset.add(package2);
        //myDataset.add(package3);
        //myDataset.add(package4);
//        myDataset.add(pakje1);
//        myDataset.add(pakje3);
//        myDataset.add(pakje2);

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
