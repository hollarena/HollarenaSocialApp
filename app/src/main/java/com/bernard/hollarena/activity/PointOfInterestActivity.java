package com.bernard.hollarena.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.bernard.hollarena.R;
import com.bernard.hollarena.adapter.POIRecyclerViewAdapter;
import com.bernard.hollarena.model.Interests;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class PointOfInterestActivity extends Activity {
    RecyclerView recyclerView;
    POIRecyclerViewAdapter recyclerViewAdapter;

    List<Interests> interests;
    public static Button submitBt;

    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    private DatabaseReference databaseReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest_rv);

        initViews();
        String name =" ";

        final FirebaseUser user = mAuth.getCurrentUser();
        /*if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                name = mAuth.getCurrentUser().getDisplayName();
                // UID specific to the provider
                String uid = profile.getUid();

                // Name, email address, and profile photo Url
                if (name == null)
                 name= profile.getDisplayName();
                String email = profile.getEmail();

            }*/
            new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Hi "
                            + "You completed SignUp.")
                    .setContentText("Let Us know more about your Interest.")
                    .setConfirmText("Ok!")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                        }
                    })
                    .show();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        initializeData();
        initializeAdapter();

        submitBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //storage firebase : email,phone,gender,interest,location
                //// TODO: 9/11/2017  firebase storage
                databaseReference.child(getString(R.string.db_key_users)).child(user.getUid()).child(getString(R.string.db_key_name)).setValue(user.getDisplayName());
                databaseReference.child(getString(R.string.db_key_users)).child(user.getUid()).child(getString(R.string.db_key_email)).setValue(user.getEmail());
                databaseReference.child(getString(R.string.db_key_users)).child(user.getUid()).child(getString(R.string.db_key_interest)).setValue(recyclerViewAdapter.getSelectedInterest());
                databaseReference.child(getString(R.string.db_key_users)).child(user.getUid()).child(getString(R.string.db_key_Premium)).setValue(false);
                databaseReference.child(getString(R.string.db_key_users)).child(user.getUid()).child(getString(R.string.db_key_thumb_image)).setValue("https://upload.wikimedia.org/wikipedia/commons/1/1e/Default-avatar.jpg");

                startActivity(new Intent(PointOfInterestActivity.this,LocationActivity.class));

            }

        });


    }
    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initializeAdapter() {
        recyclerViewAdapter = new POIRecyclerViewAdapter(PointOfInterestActivity.this,interests);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void initializeData() {
        interests = new ArrayList<>();
        interests.add(new Interests("Car ", R.drawable.motorcyclecar));
        interests.add(new Interests("Fashion", R.drawable.fashion));
        interests.add(new Interests("Home Decor", R.drawable.homedecor));
        interests.add(new Interests("Music", R.drawable.music));
        interests.add(new Interests("Food", R.drawable.spices));
        interests.add(new Interests("Tattos", R.drawable.tattos));
        interests.add(new Interests("Wedding", R.drawable.wedding));


    }

    private void initViews() {

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        databaseReference = mDatabase.getReference();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        submitBt = (Button) findViewById(R.id.next_bt);
    }


}

