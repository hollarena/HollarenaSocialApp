package com.bernard.hollarena;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import static android.content.ContentValues.TAG;


public class SplashScreen extends Activity {
    FirebaseAuth mAuth;
    boolean animation = false;
    LocationManager locationManager;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.bernard.hollarena.R.layout.activity_splash_screen);

        final Typewritter writer = (Typewritter) findViewById(com.bernard.hollarena.R.id.typewiter);
        final Button registerBt = (Button) findViewById(com.bernard.hollarena.R.id.registerBt);
        mAuth = FirebaseAuth.getInstance();
        //Add a character every 300ms
        final Thread timer = new Thread() {
            public void run() {
                try {
                    writer.setCharacterDelay(300);
                    writer.animateText("HOLLARENA");
                    sleep(5000);
//                    checkGPS();

                  /*  if (mAuth.getCurrentUser() != null){
                        finish();
                        startActivity(new Intent(SplashScreen.this,LocationActivity.class));
                    } else {
                        finish();
                        startActivity(new Intent(SplashScreen.this,LoginActivity.class));
                    }
*/
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                    writer.postOnAnimation(new Runnable() {
                        @Override
                        public void run() {
                           /* Log.e(TAG, "for button animation================");
                            registerBt.setVisibility(View.VISIBLE);
                            Animation myFadeInAnimation = AnimationUtils.loadAnimation(SplashScreen.this, R.anim.fade);
                            myFadeInAnimation.setInterpolator(new BounceInterpolator());
                            registerBt.startAnimation(myFadeInAnimation);*/
                            animation = true;
                            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                            if (!isGPSEnabled || !isNetworkEnabled) {            //Ask the user to enable GPS
                                AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this);
                                builder.setTitle("Location Manager");
                                builder.setMessage("Would you like to enable GPS?");
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Launch settings, allowing user to make a change
                                        Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        startActivity(i);
                                    }
                                });
                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //No location service, no Activity
                                        finish();
                                    }
                                });
                                builder.create().show();
                            } else {
//                                startActivity(new Intent(SplashScreen.this, LocationActivity.class));

                                if (mAuth.getCurrentUser() != null) {
                                    finish();
                                    startActivity(new Intent(SplashScreen.this, LocationActivity.class));
                                } else {
                                    finish();
                                    startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                                }
                            }
                        }
                    });
                }
            }
        };
        timer.start();


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: =================");
        if (animation) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (mAuth.getCurrentUser() != null) {
                    finish();
                    startActivity(new Intent(SplashScreen.this, LocationActivity.class));
                } else {
                    finish();
                    startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                }
            }
        }
    }
}