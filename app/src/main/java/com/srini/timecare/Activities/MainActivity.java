package com.srini.timecare.Activities;

import androidx.annotation.NonNull;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.srini.timecare.Datastore.SharedPreferenceStore;
import com.srini.timecare.Fragment.AddScheduleFragment;
import com.srini.timecare.Fragment.AddTaskFragment;
import com.srini.timecare.Fragment.HomeFragment;
import com.srini.timecare.Fragment.ProfileFragment;
import com.srini.timecare.Helper.UserHelperClass;
import com.srini.timecare.R;

public class MainActivity extends AppCompatActivity {

    private NavigationBarView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_nav);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selected=null;
                switch (item.getItemId()){
                    case R.id.nav_home: {
                        selected = new HomeFragment();
                        break;
                    }
                    case R.id.nav_addTask:{
                        selected = new AddTaskFragment();
                        break;
                    }
                    case R.id.nav_addSchedule:{
                        selected = new AddScheduleFragment();
                        break;
                    }
                    case R.id.nav_profile:{
                        selected = new ProfileFragment();
                        break;
                    }
                    default:{
                        selected = null;
                        break;
                    }
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selected).commit();
                return true;
            }
        });

    }


}