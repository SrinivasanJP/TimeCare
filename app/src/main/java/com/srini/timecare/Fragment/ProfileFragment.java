package com.srini.timecare.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.srini.timecare.Activities.Login;
import com.srini.timecare.Datastore.SharedPreferenceStore;
import com.srini.timecare.R;

public class ProfileFragment extends Fragment {


//view variables
    private RelativeLayout vLogoutBtb;
    private TextView vPersonName;
    private TextView vPersonAge;
    private TextView vPersonPhone;
    private TextView vPersonEmail;


    //firebase variables
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    //backend variables
    SharedPreferences sharedPreferences;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        sharedPreferences = this.getActivity().getSharedPreferences(SharedPreferenceStore.SHARED_PREFERENCE_NAME,this.getActivity().MODE_PRIVATE);

        //hooks
        vPersonName = view.findViewById(R.id.personName);
        vPersonAge = view.findViewById(R.id.personAge);
        vPersonEmail = view.findViewById(R.id.personmailID);
        vPersonPhone = view.findViewById(R.id.personPhone);
        vLogoutBtb  = view.findViewById(R.id.BtnLogout);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        vPersonName.setText(sharedPreferences.getString(SharedPreferenceStore.KEY_FIRST_NAME,null)+" "+sharedPreferences.getString(SharedPreferenceStore.KEY_LAST_NAME,null));
        vPersonAge.setText(sharedPreferences.getString(SharedPreferenceStore.KEY_AGE,null));
        vPersonPhone.setText(sharedPreferences.getString(SharedPreferenceStore.KEY_PHONE,null));
        vPersonEmail.setText(firebaseUser.getEmail());



        vLogoutBtb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                getActivity().finish();
                startActivity(new Intent(getActivity().getApplicationContext(), Login.class));
            }
        });
        return view;
    }

}