package com.ap.fietskorier;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class add_package extends AppCompatActivity {
    EditText editText;
     FirebaseFirestore database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_package);

        editText = findViewById(R.id.textView1);
    }

    public void wrtie2Database(View view) {




    }
}
