package com.srini.timecare.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.srini.timecare.Datastore.SharedPreferenceStore;
import com.srini.timecare.R;

import java.lang.ref.Reference;

public class Login extends AppCompatActivity {

    //view Variables
    private EditText vMailID;
    private EditText vPassword;
    private RelativeLayout vForgotPassword;
    private RelativeLayout vRegisterUser;
    private RelativeLayout vLoginBtn;
    private ProgressBar vProgressBar;
    private TextView vBtnTextview;

    //Backend Variables
    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private String age;
    private String phone;
    private SharedPreferences sharedPreferences;

    //firebase variables
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase rootNode;
    private DatabaseReference referenceNode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        //if already loged in
        if(firebaseUser!= null){
            finish();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }
        //Hooks

        vMailID = findViewById(R.id.emailID);
        vPassword = findViewById(R.id.password);
        vForgotPassword = findViewById(R.id.btnforget);
        vRegisterUser = findViewById(R.id.btnnewUser);
        vLoginBtn = findViewById(R.id.btnlogin);
        vProgressBar = findViewById(R.id.ProgressbarLogin);
        vBtnTextview = findViewById(R.id.BtnLoginText);


        vProgressBar.setVisibility(View.INVISIBLE);


        vLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = vMailID.getText().toString().trim();
                password = vPassword.getText().toString().trim();
                if(userName.isEmpty()){
                    vMailID.setError("Mail id is required");
                }else if(password.isEmpty()){
                    vPassword.setError("Password is required");
                }else if(password.length()<7){
                    vPassword.setError("Password should be more then 7 character long");
                }else{
                vProgressBar.setVisibility(View.VISIBLE);
                vBtnTextview.setVisibility(View.INVISIBLE);


                firebaseAuth.signInWithEmailAndPassword(userName,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            firebaseUser = firebaseAuth.getCurrentUser();
                            if(!firebaseUser.isEmailVerified()){
                                vProgressBar.setVisibility(View.INVISIBLE);
                                vBtnTextview.setVisibility(View.VISIBLE);
                                Toast.makeText(Login.this, "Please verify your email", Toast.LENGTH_SHORT).show();
                            }else {
                                sharedPreferences = getSharedPreferences(SharedPreferenceStore.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
                                rootNode = FirebaseDatabase.getInstance();
                                referenceNode = rootNode.getReference("users");
                                referenceNode.child(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (!task.getResult().exists()) {
                                                startActivity(new Intent(getApplicationContext(), UserDetails.class));
                                            } else {
                                                DataSnapshot dataSnapshot = task.getResult();
                                                firstName = String.valueOf(dataSnapshot.child("fname").getValue());
                                                lastName = String.valueOf(dataSnapshot.child("lname").getValue());
                                                age = String.valueOf(dataSnapshot.child("age").getValue());
                                                phone = String.valueOf(dataSnapshot.child("phone").getValue());
                                                sharedPreferences = getSharedPreferences(SharedPreferenceStore.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putString(SharedPreferenceStore.KEY_FIRST_NAME, firstName).apply();
                                                editor.putString(SharedPreferenceStore.KEY_LAST_NAME, lastName).apply();
                                                editor.putString(SharedPreferenceStore.KEY_AGE, age).apply();
                                                editor.putString(SharedPreferenceStore.KEY_PHONE, phone).apply();
                                                finish();
                                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                            }
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Failed to read", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        }else{
                            vProgressBar.setVisibility(View.INVISIBLE);
                            vBtnTextview.setVisibility(View.VISIBLE);
                            Toast.makeText(Login.this, "Login failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            }
        });
        vForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ForgotPassword.class));
            }
        });
        vRegisterUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),RegisterUser.class));
            }
        });
    }
}