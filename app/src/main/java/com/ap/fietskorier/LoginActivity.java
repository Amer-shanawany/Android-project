package com.ap.fietskorier;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static com.ap.fietskorier.Constants.USERS_COLLECTION;

public class LoginActivity extends AppCompatActivity {

    EditText EmailID,Password;
    Button Btn_singIn;
    TextView tvSignUp;
    FirebaseAuth mFirebaseAuth;
    public User user;


Button Btn_fastSingIn;

    // Choose an arbitrary request code value
    private static final int RC_SIGN_IN = 123;

    private FirebaseAuth.AuthStateListener mAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mFirebaseAuth = FirebaseAuth.getInstance();
        EmailID = findViewById(R.id.Email_signIn);
        Password =findViewById(R.id.Password_singIn);
        Btn_singIn = findViewById(R.id.button_signIn);
        tvSignUp = findViewById(R.id.no_account_signUp);
        Btn_fastSingIn = findViewById(R.id.button_fastlogin);

        mAuthStateListener = new FirebaseAuth.AuthStateListener(){

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();

                if(mFirebaseUser!=null){

                    Toast.makeText(LoginActivity.this ,"You are logged in", Toast.LENGTH_SHORT).show();


                    Intent i = new Intent(LoginActivity.this,HomeActivity.class );




                    startActivity(i);
                 }else{Toast.makeText(LoginActivity.this,"Please Login",Toast.LENGTH_SHORT).show();
                }
            }
        };
        Btn_singIn.setOnClickListener(new View.OnClickListener() {
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
                        Toast.makeText(LoginActivity.this,"Fields are empty!",Toast.LENGTH_LONG).show();
                    }else  if (!(email.isEmpty()&&!pwd.isEmpty()))
                    {
                        //Sign in old user
                        mFirebaseAuth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(!task.isSuccessful()){
                                    Toast.makeText(LoginActivity.this,"Login Error, Please login again", Toast.LENGTH_SHORT).show();
                                }else{
                                    if(mFirebaseAuth.getCurrentUser().isEmailVerified()){
                                    Intent intToHome = new Intent(LoginActivity.this,HomeActivity.class);


                                        //Create a User in the DataBase

                                        String UID = mFirebaseAuth.getCurrentUser().getUid() ;

                                        DocumentReference mDocRef = FirebaseFirestore.getInstance().collection(USERS_COLLECTION).document(UID);
                                        String Email = mFirebaseAuth.getCurrentUser().getEmail();
                                        user = new User(UID,"Unknown",Email);
                                        Map<String,Object> dataToSave = new HashMap<>();
                                        dataToSave.put("UID",UID);
                                        dataToSave.put("Email",Email);

                                        mDocRef.set(dataToSave);

                                        startActivity(intToHome);}else{
                                        Toast.makeText(LoginActivity.this,"please verify your email first and try again",Toast.LENGTH_LONG).show();
                                    }

                                }
                            }
                        });

                    }else{
                        Toast.makeText( LoginActivity.this   ,"Error Occurd!",Toast.LENGTH_LONG).show();
                    }
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  intSignUp = new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(intSignUp);

            }
        });

        Btn_fastSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //EmailID.setText("jonas.baert@icloud.com");
                //Password.setText("123456");
                String email = "s109802@ap.be";
                String pwd = "123456";
                mFirebaseAuth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(LoginActivity.this,"Login Error, Please login again", Toast.LENGTH_SHORT).show();
                            }else{
                                if(mFirebaseAuth.getCurrentUser().isEmailVerified()){
                                    Intent intToHome = new Intent(LoginActivity.this,ShipmentActivity.class);


                                    //Create a User in the DataBase
                                    String UID = mFirebaseAuth.getCurrentUser().getUid() ;
                                    DocumentReference mDocRef = FirebaseFirestore.getInstance().collection(USERS_COLLECTION).document(UID);
                                    String Email = mFirebaseAuth.getCurrentUser().getEmail();
                                    Map<String,Object> dataToSave = new HashMap<>();
                                    //dataToSave.put("UID",UID);
                                    dataToSave.put("Email",Email);

                                    mDocRef.set(dataToSave);

                                    startActivity(intToHome);}else{
                                    Toast.makeText(LoginActivity.this,"please verify your email first and try again",Toast.LENGTH_LONG).show();
                                }

                            }
                        }
                    });

            }
        });

    }

}
