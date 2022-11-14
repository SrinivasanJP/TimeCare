package com.srini.timecare.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.srini.timecare.Datastore.SharedPreferenceStore;
import com.srini.timecare.Helper.UserHelperClass;
import com.srini.timecare.R;

public class UserDetails extends AppCompatActivity {
    //view variables
    private EditText vFirstName;
    private EditText vLastName;
    private EditText vAge;
    private EditText vPhone;
    private RelativeLayout vBtn;
    private ProgressBar vProgressBar;
    private TextView vContinue;
    //backend variables
    private String lname,fname,age,phone;
    private SharedPreferences sharedPreferences;

    //firebase variables
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase rootNode;
    private DatabaseReference referenceNode;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        //hooks
        vFirstName = findViewById(R.id.firstname);
        vLastName = findViewById(R.id.lastname);
        vAge = findViewById(R.id.age);
        vPhone = findViewById(R.id.phone);
        vBtn = findViewById(R.id.BtnaddData);
        vProgressBar = findViewById(R.id.Personal_details_progressBar);
        vContinue = findViewById(R.id.btnTextView);

        vBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vContinue.setVisibility(View.INVISIBLE);
                vProgressBar.setVisibility(View.VISIBLE);
                fname = vFirstName.getText().toString().trim();
                lname = vLastName.getText().toString().trim();
                age = vAge.getText().toString().trim();
                phone = vPhone.getText().toString().trim();



                UserHelperClass userDetails = new UserHelperClass(fname,lname,age,phone);

                //firebase connection
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseUser = firebaseAuth.getCurrentUser();
                rootNode = FirebaseDatabase.getInstance();
                referenceNode = rootNode.getReference("users");
                referenceNode.child(firebaseUser.getUid()).setValue(userDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            firebaseAuth.signOut();
                            finish();
                            startActivity(new Intent(getApplicationContext(), Login.class));

                        }else{
                            Toast.makeText(UserDetails.this, "failed to insert data", Toast.LENGTH_SHORT).show();
                            firebaseAuth.signOut();
                            vContinue.setVisibility(View.VISIBLE);
                            vProgressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });




    }
}