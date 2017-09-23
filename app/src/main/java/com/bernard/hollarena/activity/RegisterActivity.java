package com.bernard.hollarena.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import cn.pedant.SweetAlert.SweetAlertDialog;



public class RegisterActivity extends Activity {
    Button mButtonSubmit,btnSignIn;
    EditText mEmailEdit, mPhoneEdit, mPasswordEdit;
    SweetAlertDialog pDialog;
    FirebaseAuth mAuth;
    private EditText mUserName;
    private Button btnResetPassword;
    String TAG = ".RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.bernard.hollarena.R.layout.activity_register);

        initViews();

        //check if user has already signed up
        if (mAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(this,LocationActivity.class));
        }

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mButtonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (valid()) {
                    pDialog = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialog.setTitleText("Authenticating...");
                    pDialog.setCancelable(false);
                    pDialog.show();

                    if (ConnectToNetwork()) {
                        signIn(mEmailEdit.getText().toString(),mPasswordEdit.getText().toString());

                    } else {
                        pDialog.dismiss();
                        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(com.bernard.hollarena.R.id.coordinatorLayout);
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "No Internet Connection!", Snackbar.LENGTH_SHORT);
                        snackbar.setActionTextColor(Color.WHITE);
                        snackbar.show();
                    }

                  /* new Thread(new Runnable() {
                       @Override
                       public void run() {
                           try {
                                if(ConnectToNetwork()){
                                    //check location
                                    Log.e("in if==================","=");
                                } else {
                                    progressDialog.dismiss();
                                    CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
                                    Snackbar snackbar = Snackbar.make(coordinatorLayout,"No Internet Connection!",Snackbar.LENGTH_SHORT);
                                    snackbar.show();
                                }
                           }catch (Exception e){
                               e.printStackTrace();
                           }
                       }
                   }).start();
                  */
                } else {
                    Toast.makeText(RegisterActivity.this, "Not valid Input", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    //set UserDisplay name
    private void setUserProfile(){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().
                    setDisplayName(mUserName.getText().toString().trim()).build();

            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Log.e(TAG, "user profile updated" );
                    }
                }
            });
        }
    }
    //create firebase account
    private void signUp(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.e(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            pDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, com.bernard.hollarena.R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        } else{
                            setUserProfile();
                        }

                        // ...
                    }
                });
    }

    //sign in with firebase account

    private void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            pDialog.dismiss();
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(RegisterActivity.this, com.bernard.hollarena.R.string.auth_failed + " "+task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                     else {
                            Intent intent = new Intent(RegisterActivity.this, PointOfInterestActivity.class);
                            startActivity(intent);
                        }
                    }
                });
    }
    private void initViews() {
        mAuth = FirebaseAuth.getInstance();

        mUserName = (EditText)findViewById(com.bernard.hollarena.R.id.signup_user_name);
        mEmailEdit = (EditText) findViewById(com.bernard.hollarena.R.id.signup_input_email);
        mPhoneEdit = (EditText) findViewById(com.bernard.hollarena.R.id.register_phone_number);
        mPasswordEdit = (EditText) findViewById(com.bernard.hollarena.R.id.signup_input_password);
        mButtonSubmit = (Button) findViewById(com.bernard.hollarena.R.id.btn_submit);
        btnSignIn = (Button) findViewById(com.bernard.hollarena.R.id.sign_in_button);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pDialog!= null)
        pDialog.dismiss();
    }

    private boolean ConnectToNetwork() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connMgr.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connMgr.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connMgr.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connMgr.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {

            // if connected with internet
            signUp(mEmailEdit.getText().toString(),mPasswordEdit.getText().toString());
            Toast.makeText(this, " Connected ", Toast.LENGTH_LONG).show();
            return true;

        } else if (
                connMgr.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connMgr.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {

            Toast.makeText(this, " Not Connected ", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;

    }

    private boolean valid() {
        boolean valid = true;
        String email = mEmailEdit.getText().toString();
        String phone = mPhoneEdit.getText().toString();
        String password = mPasswordEdit.getText().toString();


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailEdit.setError("Enter a valid email address");
            valid = false;
        } else {
            mEmailEdit.setError(null);
        }

        if (phone.isEmpty() || phone.length() != 10) {
            mPhoneEdit.setError("Enter valid 10 digit phone number");
            valid = false;
        } else {
            mPhoneEdit.setError(null);
        }
        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            mPasswordEdit.setError("Between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            mPasswordEdit.setError(null);
        }

        return valid;
    }
    @Override
    public void onBackPressed() {
        finish();
    }

}
