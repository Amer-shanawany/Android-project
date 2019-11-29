package com.ap.fietskorier;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.LogDescriptor;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class add_package extends AppCompatActivity {
    EditText editText;
     FirebaseFirestore database;
     Button button;
    private String TAG = "Add Package";
     private DocumentReference mDocRef = FirebaseFirestore.getInstance().collection("users").document("Amer");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_package);


    }


    public void saveFirestore(View view) {
        editText =   findViewById(R.id.edit_text);
        String text = editText.getText().toString();
        Map<String,Object> dataToSave = new HashMap<>();
        dataToSave.put("Quote",text);
        mDocRef.set(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Document has been saved! ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Document was not saved!",e);
            }
        });
    }
}
