package com.bernard.hollarena.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bernard.hollarena.ProfileActivity;
import com.bernard.hollarena.R;
import com.bernard.hollarena.adapter.SectionsPagerAdapter;
import com.bernard.hollarena.adapter.UserAdapter;
import com.bernard.hollarena.model.UserModel;
import com.bernard.hollarena.model.Users;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<String> result;
    UserAdapter adapter;
    String TAG = "TAG";

    FirebaseUser firebaseUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    String uId;

    private Toolbar mToolbar;

    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private TabLayout mTabLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference().child(getString(R.string.db_key_users));
        mDatabase.getReference().keepSynced(true);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = (RecyclerView) findViewById(R.id.user_list);

        mToolbar = (Toolbar) findViewById(R.id.users_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Tabs
//        mViewPager = (ViewPager)findViewById(R.id.main_tabPager);
//        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
//
//        mViewPager.setAdapter(mSectionsPagerAdapter);
//
//        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
//        mTabLayout.setupWithViewPager(mViewPager);

        result = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.user_list);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(linearLayoutManager);
//        createResult();

//        adapter = new UserAdapter(result);
//        recyclerView.setAdapter(adapter);

//        updateList();

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Users,UserViewHolder> mFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UserViewHolder>(
                Users.class,R.layout.layout_all_users,UserViewHolder.class,mReference) {
            @Override
            protected void populateViewHolder(UserViewHolder viewHolder, Users model, final int position) {
                viewHolder.setName(model.getName());
                viewHolder.setIsPremium(model.isPremium());
                viewHolder.setUserImage(model.getThumb_image(),getApplicationContext());

                final String key = getRef(position).getKey();
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent profileIntent =new Intent(UserListActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("user_id",key);
                        startActivity(profileIntent);

                    }
                });
            }
        };

        recyclerView.setAdapter(mFirebaseRecyclerAdapter);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 0:
                break;
            case 1:
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void updateList() {
        mReference.child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                UserModel userModel = new UserModel();

                try {
                    String  json = (String) dataSnapshot.getValue();
                    JSONObject jsonResponse = new JSONObject(json);
                    JSONObject jsonPremiumResponse = jsonResponse.getJSONObject(getString(R.string.db_key_Premium));
                    boolean isPaid = jsonPremiumResponse.getBoolean(getString(R.string.paid));
                    if (isPaid && !dataSnapshot.getKey().equals(firebaseUser.getDisplayName())){
                        result.add(dataSnapshot.getKey());
                    }
                    Log.e(TAG, "onChildAdded: isPaid==> "+isPaid);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.e(TAG, "onChildAdded: key " + dataSnapshot.getKey());
                Log.e(TAG, "onChildAdded: value " + dataSnapshot.getValue());

                 adapter.notifyDataSetChanged();
                    Log.e(TAG, "onChildAdded: result " + result.toString());



            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.e(TAG, "onDataChange: key " + dataSnapshot.getKey());

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static class UserViewHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView userNameView;
        String name;

        public UserViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }
        public void setName(String name){
            this.name=name;
            userNameView = (TextView) mView.findViewById(R.id.all_user_name);
        }
        public void setIsPremium(boolean isPremium){
            if (!isPremium ){
                userNameView.setText(name);
            }
        }

        public void setUserImage(String thumb_image, Context applicationContext) {
            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.all_user_profile_pic);
            Picasso.with(applicationContext).load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);
        }
    }

}
