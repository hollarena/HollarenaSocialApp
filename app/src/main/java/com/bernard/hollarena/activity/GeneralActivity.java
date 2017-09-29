package com.bernard.hollarena.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bernard.hollarena.MainActivity;
import com.bernard.hollarena.R;
import com.bernard.hollarena.fragment.ArticlesFragment;
import com.bernard.hollarena.fragment.NearByFragment;
import com.bernard.hollarena.fragment.SettingsFragment;
import com.bernard.hollarena.fragment.SpecialPageFragment;
import com.bernard.hollarena.fragment.UpgradeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

public class GeneralActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ArticlesFragment.OnFragmentInteractionListener,
        UpgradeFragment.OnFragmentInteractionListener,
        NearByFragment.OnFragmentInteractionListener,
        SpecialPageFragment.OnFragmentInteractionListener{

    NavigationView navigationView = null;
    Toolbar toolbar = null;

    FirebaseUser firebaseUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);

        initFirebase();
        checkMembership();

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);
        TextView nav_user = (TextView)hView.findViewById(R.id.username);
        nav_user.setText(firebaseUser.getDisplayName());

        TextView nav_user_email = (TextView)hView.findViewById(R.id.email);
        nav_user_email.setText(firebaseUser.getEmail());

        //set the fragment initially
        ArticlesFragment articlesFragment = new ArticlesFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,articlesFragment).commit();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //// TODO: 9/23/2017  reverse if

                if (!checkMembership()) {
                    //implement chat
                    startActivity(new Intent(GeneralActivity.this,MainActivity.class));
                } else {
                    Snackbar.make(view, "You are not premium member.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }
    boolean isPaid ;
    private boolean checkMembership() {
        mReference.child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//             datasnapshot.getValue:   {Premium={reactive_time={month=8, timezoneOffset=0, time=1506122754936, minutes=25, seconds=54, hours=23, day=5, date=22, year=117}, paid=true}, Interest=false}

                String json = dataSnapshot.getValue().toString();
                if (dataSnapshot.child(firebaseUser.getUid()).equals(firebaseUser.getUid())) {
                    try {
                        //get time in Millis
                        //get time in Millis
                        JSONObject jsonResponse = new JSONObject(json);
                        JSONObject jsonPremiumResponse = jsonResponse.getJSONObject(getString(R.string.db_key_Premium));
                        if (jsonPremiumResponse.has(getString(R.string.db_key_timestamp))){
                            Long storedTime = jsonPremiumResponse.getLong(getString(R.string.db_key_timestamp));
                            Long currentTime = System.currentTimeMillis();
                            if (currentTime>=storedTime){
                                //disable messaage service
                                isPaid= false;
                                Log.e(TAG, "onChildAdded: ============> has true" );
                                Log.e(TAG, "onChildAdded: time not completed  " );

                            } else {
                                //enable message service
                                isPaid= true;
                                Log.e(TAG, "onChildAdded: time completed  ");
                                 }

                        }
                        Log.e(TAG, "onChildAdded: json ==> " + json + " premium response " + jsonPremiumResponse + " isPaid " + isPaid);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
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
        return isPaid;
    }

    private void initFirebase() {
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference();
        mDatabase.getReference().keepSynced(true);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private Boolean exit = false;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (exit) {
                finish(); // finish activity
            } else {
                Toast.makeText(this, "Press Back again to Exit.",
                        Toast.LENGTH_SHORT).show();
                exit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exit = false;
                    }
                }, 3 * 1000);

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.item_general_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sign_out) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(GeneralActivity.this, LoginActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_articles) {
            ArticlesFragment articlesFragment = new ArticlesFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,articlesFragment).commit();

        } else if (id == R.id.nav_upgarade) {
            UpgradeFragment upgradeFragment = new UpgradeFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,upgradeFragment).commit();


        } else if (id == R.id.nav_nearby) {
            NearByFragment nearByFragment = new NearByFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,nearByFragment).commit();


        } else if (id == R.id.nav_specialpg) {
            SpecialPageFragment specialPageFragment = new SpecialPageFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,specialPageFragment).commit();


        } else if (id == R.id.nav_settings) {
            SettingsFragment settingsFragment = new SettingsFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,settingsFragment).commit();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mReference.child(getString(R.string.db_key_users)).child(firebaseUser.getUid()).child("online").setValue(false);

    }

    @Override
    protected void onStop() {
        super.onStop();
        mReference.child(getString(R.string.db_key_users)).child(firebaseUser.getUid()).child("online").setValue(false);
    }

}
