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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.srini.timecare.R;

public class ForgotPassword extends AppCompatActivity {

    //view variables
    private EditText vMailID;
    private ProgressBar vProgressBar;
    private RelativeLayout vRecoverId;
    private TextView BacktoSignin;

    //backend variables
    private String mailID;

    //firebase variables
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        firebaseAuth = FirebaseAuth.getInstance();

        //hooks
        vMailID = findViewById(R.id.emailID_torecover);
        vProgressBar = findViewById(R.id.ProgressbarForgetPassword);
        vRecoverId = findViewById(R.id.btnRecover_fpa);
        BacktoSignin = findViewById(R.id.fptologin);

        vProgressBar.setVisibility(View.INVISIBLE);

        BacktoSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });

        vRecoverId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mailID = vMailID.getText().toString().trim();
                vProgressBar.setVisibility(View.VISIBLE);
                firebaseAuth.sendPasswordResetEmail(mailID).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ForgotPassword.this, "Check your mail", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(new Intent(getApplicationContext(),Login.class)));
                        }else{
                            Toast.makeText(ForgotPassword.this, "Try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}