package com.srini.timecare.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.srini.timecare.R;

public class RegisterUser extends AppCompatActivity {
    //view variables
    private EditText vMailID;
    private EditText vPassword;
    private ProgressBar vProgressBar;
    private RelativeLayout vSignUp;
    private TextView vBacktoSignin;

    //backend variables
    private String emailId;
    private String password;

    //firebase variables
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        //hooks
        vMailID = findViewById(R.id.signUPEmailID);
        vPassword = findViewById(R.id.SignUPPassword);
        vProgressBar = findViewById(R.id.ProgressbarSignin);
        vSignUp = findViewById(R.id.btnSignIN);
        vBacktoSignin = findViewById(R.id.backtologin);

        vProgressBar.setVisibility(View.INVISIBLE);

        vBacktoSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });

        vSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vMailID.setBackground(getDrawable(R.drawable.curve_box));
                vPassword.setBackground(getDrawable(R.drawable.curve_box));
                emailId = vMailID.getText().toString().trim();
                password = vPassword.getText().toString().trim();

                if(emailId.isEmpty()){
                    Toast.makeText(RegisterUser.this, "Enter MailID", Toast.LENGTH_SHORT).show();
                    vMailID.setBackground(getDrawable(R.drawable.red_border));
                }else if(password.isEmpty()){
                    Toast.makeText(RegisterUser.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    vPassword.setBackground(getDrawable(R.drawable.red_border));
                }else{

                    vProgressBar.setVisibility(View.VISIBLE);
                    firebaseAuth = FirebaseAuth.getInstance();
                    firebaseAuth.createUserWithEmailAndPassword(emailId,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                vProgressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(RegisterUser.this, "Check your mail to verify your account", Toast.LENGTH_SHORT).show();
                                firebaseAuth.getCurrentUser().sendEmailVerification();
                                finish();
                                startActivity(new Intent(getApplicationContext(), UserDetails.class));

                            }else if(task.isCanceled()){
                                task.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(RegisterUser.this, e.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else{
                                vProgressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(RegisterUser.this, "Failed to register", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }
}