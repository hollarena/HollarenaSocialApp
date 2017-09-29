package com.bernard.hollarena;

import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class BasicActivity extends AppCompatActivity {
    LocationManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.bernard.hollarena.R.layout.activity_basic);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
