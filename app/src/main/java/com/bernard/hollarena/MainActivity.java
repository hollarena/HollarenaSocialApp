package com.bernard.hollarena;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.bernard.hollarena.activity.UserListActivity;
import com.bernard.hollarena.adapter.SectionsPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    FirebaseUser firebaseUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    String uId;

    private Toolbar mToolbar;

    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private TabLayout mTabLayout;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.bernard.hollarena.R.layout.activity_main);


        mDatabase = FirebaseDatabase.getInstance();
        mDatabase.getReference().keepSynced(true);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mReference = mDatabase.getReference().child(getString(R.string.db_key_users)).child(firebaseUser.getUid());

        uId = firebaseUser.getUid();

        mToolbar = (Toolbar) findViewById(R.id.user_list_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("chat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Tabs
        mViewPager = (ViewPager)findViewById(R.id.main_tabPager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.item_chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId()==R.id.action_user_list){
            startActivity(new Intent(MainActivity.this, UserListActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mReference.child("online").setValue(false);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseUser == null){

        }else{
            mReference.child("online").setValue(true);
        }
    }
}
