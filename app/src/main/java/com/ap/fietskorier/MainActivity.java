package com.ap.fietskorier;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    EditText EmailID,Password;
    Button Btn_SignUp;
    TextView tvSignIn;
    FirebaseAuth mFirebaseAuth;

    String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseAuth = FirebaseAuth.getInstance();
        EmailID = findViewById(R.id.Email_signUp);
        Password =findViewById(R.id.Password_signUp);
        Btn_SignUp = findViewById(R.id.button_signup);
        tvSignIn = findViewById(R.id.yes_account_signIn);







        Btn_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = EmailID.getText().toString();
                String pwd = Password.getText().toString();
                if (email.isEmpty())
                {
                    EmailID.setError("Please enter email id");
                    EmailID.requestFocus();
                }else  if (pwd.isEmpty())
                {
                    Password.setError("Please enter your password");
                    Password.requestFocus();
                }else  if (email.isEmpty()&&pwd.isEmpty())
                {
                    Toast.makeText(MainActivity.this,"Fields are empty!",Toast.LENGTH_LONG).show();
                }else  if (!(email.isEmpty()&&pwd.isEmpty()))
                {
                    //create new user
                    mFirebaseAuth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if ( !task.isSuccessful()){
                                //
                                Toast.makeText(MainActivity.this, "SignUp Unsuccssful, Please try again", Toast.LENGTH_SHORT).show();
                            }else{
                                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                                //updateUI(user);
                                sendEmailVerification();
                                Intent i = new Intent(MainActivity.this,HomeActivity.class);
                                        startActivity(i);
                            }
                        }
                    });

                }else{
                    Toast.makeText( MainActivity.this   ,"Error Occurd!",Toast.LENGTH_LONG).show();
                }
            }
        });

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(i);
            }
        });
    }
    private void sendEmailVerification() {

        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mFirebaseAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]

                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(MainActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }

}
