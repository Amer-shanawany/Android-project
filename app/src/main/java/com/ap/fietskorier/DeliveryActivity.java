package com.ap.fietskorier;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.LinkedList;

import static com.ap.fietskorier.Constants.DELIVERER_ID;
import static com.ap.fietskorier.Constants.DESTINATION_ADDRESS;
import static com.ap.fietskorier.Constants.DESTINATION_EMAIL;
import static com.ap.fietskorier.Constants.PACKAGES_COLLECTIONS;
import static com.ap.fietskorier.Constants.PACKAGE_ID;
import static com.ap.fietskorier.Constants.PICKUP_QR_URL;
import static com.ap.fietskorier.Constants.PRICE;
import static com.ap.fietskorier.Constants.SOURCE_ADDRESS;

public class DeliveryActivity extends AppCompatActivity {

    private static final String TAG = "DeliveryActivity" ;
    private final LinkedList<Package> myDataset = new LinkedList<Package>();

    //RECYCLERVIEW
    private RecyclerView recyclerView;
    private RecyclerView.Adapter myAdapter;
    private RecyclerView.LayoutManager myLayoutManager;
    //!!!
    private LinkedList<Package> packageList;
    private FirebaseAuth mFirebaseAuth;
    private User user;
    private FirebaseFirestore db;

    //Button BtnToDeliverPackage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //BtnToDeliverPackage = findViewById(R.id.DeliverPackage);

        //make an instance of this user
        user = ((UserClient)(getApplicationContext())).getUser();
        //mFirebaseAuth = FirebaseAuth.getInstance();
        //FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToSearchPackage = new Intent(DeliveryActivity.this,SearchPackage.class);
                startActivity(intentToSearchPackage);
            }
        });

        setupRecyclerView();


        /*BtnToDeliverPackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToDeliverPackage = new Intent(DeliveryActivity.this,DeliverPackage.class);
                startActivity(intentToDeliverPackage);
            }
        });*/

    }

    private void setupRecyclerView() {

        CollectionReference packages = db.collection(PACKAGES_COLLECTIONS);

        packages.whereEqualTo(DELIVERER_ID,user.getUser_id()) //TODO ADD A DELIVERER ID IN THE FIREBASE!!!
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }

                        for (DocumentChange document : snapshots.getDocumentChanges()) {
                            switch (document.getType()) {
                                case ADDED:
                                    Log.d(TAG, "New Package: " + document.getDocument().getData());

                                    if(document.getDocument().getData().get(PRICE)!=null){
                                        Package addPackage = new Package(user,
                                                document.getDocument().getString(SOURCE_ADDRESS),
                                                document.getDocument().getString(DESTINATION_ADDRESS),
                                                document.getDocument().getString(DESTINATION_EMAIL),
                                                document.getDocument().getDouble(PRICE)
                                        );
                                        addPackage.setPackageID(document.getDocument().getString(PACKAGE_ID));
                                        addPackage.setEmailDestination(document.getDocument().getString(DESTINATION_EMAIL));
                                        addPackage.setDeliveryAddress(document.getDocument().getString(DESTINATION_ADDRESS));
                                        addPackage.setOwnerAddress(document.getDocument().getString(SOURCE_ADDRESS));
                                        addPackage.setPickupQR(document.getDocument().getString(PICKUP_QR_URL));
                                        myDataset.add(addPackage);
                                        myAdapter.notifyDataSetChanged();}

                                    break;
                                case MODIFIED:
                                    Log.d(TAG, "Modified Package: " + document.getDocument().getData());


                                    break;
                                case REMOVED:
                                    Log.d(TAG, "Removed Package: " + document.getDocument().getData());
                                    document.getDocument().getString(PACKAGE_ID);
                                    for (Package pack : myDataset) {
                                        if (pack.getPackageID().equals(document.getDocument().getString(PACKAGE_ID))) {
                                            int i = myDataset.indexOf(pack);
                                            myDataset.remove(i);
                                            myAdapter.notifyDataSetChanged();
                                        }
                                    }
                                    break;
                            }
                        }

                    }
                });


        //TODO : Use //// Source can be CACHE, SERVER, or DEFAULT.
        //TODO Source source = Source.CACHE;



        //RECYCLERVIEW
        recyclerView = (RecyclerView) findViewById(R.id.delivery_recyclerview);
        //fixed size
        recyclerView.setHasFixedSize(true);
        //linear layout manager
        myLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(myLayoutManager);
        //adapter
        myAdapter = new PackageAdapter(this, myDataset, new PackageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Package myPackage) {
                Intent i = new Intent(DeliveryActivity.this,DeliverPackage.class);
                Bundle extras = new Bundle();
                //extras.putParcelable(EXTRA_PACKAGE, myPackage);

                extras.putString("ID",myPackage.getPackageID());
                extras.putString("SOURCE", myPackage.getOwnerAddress());
                extras.putString("DESTINATION", myPackage.getDeliveryAddress());
                extras.putString("EMAIL", myPackage.getEmailDestination());
                //extras.putString("QR", myPackage.getPickupQR());
                i.putExtras(extras);
                startActivity(i);
                //Toast.makeText(getBaseContext(),myPackage.getPackageID(), Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(myAdapter);
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
                Intent intToMain = new Intent(this, LoginActivity.class);
                startActivity(intToMain);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
