package com.ap.fietskorier;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;

public class ViewPackage extends AppCompatActivity {

    Button btn_delete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_package);
        //Intent i = getIntent();
        Bundle extras = getIntent().getExtras();

        String packageId = extras.getString("ID");
        String packageSourceAddress = extras.getString("SOURCE");
        String packageDestinationAddress = extras.getString("DESTINATION");
        String packageDestinationEmail = extras.getString("EMAIL");
        String packagePickupQRCode = extras.getString("QR");

        TextView packageIden = findViewById(R.id.packageID);
        TextView packageSource = findViewById(R.id.sourceAddress);
        TextView packageDestAdd = findViewById(R.id.destinationAddress);
        TextView packageDestMail = findViewById(R.id.destinationEmail);

        packageIden.setText(packageId);
        packageSource.setText(packageSourceAddress);
        packageDestAdd.setText(packageDestinationAddress);
        packageDestMail.setText(packageDestinationEmail);

        ImageView PickupQRCode = findViewById(R.id.QR_pickup);

        new DownLoadImageTask(PickupQRCode).execute(packagePickupQRCode);


        btn_delete = findViewById(R.id.button_deletePackage);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    private class DownLoadImageTask extends AsyncTask<String,Void,Bitmap>{
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView){
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String...urls){
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try{
                InputStream is = new URL(urlOfImage).openStream();
                logo = BitmapFactory.decodeStream(is);
            }catch(Exception e){
                e.printStackTrace();
            }
            return logo;
        }

        protected void onPostExecute(Bitmap result){
            imageView.setImageBitmap(result);
        }
    }

}
