package com.bernard.hollarena.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.bernard.hollarena.AppStatus;
import com.bernard.hollarena.R;
import com.bernard.hollarena.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;
import co.paystack.android.model.Transaction;

public class PayByPaystack extends Activity implements View.OnClickListener {
    EditText mEditEmail, mEditCard, mEditCVV;
    TextView mTextAmount;
    Spinner mSpinnerExpairyMonth;
    Spinner mSpinnerExpairyYear;
    Button btPay;
    ProgressDialog progressDialog;
    TextView textView;
    int noOfDays;

    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    private DatabaseReference databaseReference;
    FirebaseUser user;
    private int amount;

    String TAG = "PAYSTACK ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        Intent UFIntent = getIntent();
        noOfDays = UFIntent.getIntExtra(String.valueOf(R.string.intent_key_no_of_days), 1);


        Log.e(TAG, "onCreate: no of days==== " + noOfDays);
        PaystackSdk.initialize(getApplicationContext());
        PaystackSdk.setPublicKey(getString(R.string.paystack_public_key));
        viewInit();

        initFireBase();

        btPay.setOnClickListener(this);
    }


    private void initFireBase() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        databaseReference = mDatabase.getReference();
        user = mAuth.getCurrentUser();

    }

    //View Initialization
    private void viewInit() {
        mEditEmail = (EditText) findViewById(R.id.etEmail);
        mEditCard = (EditText) findViewById(R.id.cardNumber);
        mSpinnerExpairyMonth = (Spinner) findViewById(R.id.spExpiryMonth);
        mSpinnerExpairyYear = (Spinner) findViewById(R.id.spExpiryYear);
        mTextAmount = (TextView) findViewById(R.id.amountText);
        mEditCVV = (EditText) findViewById(R.id.editCVV);
        btPay = (Button) findViewById(R.id.btPay);

        setAmount();
        //Set Array Of Months To Spinner
        ArrayList<String> monthsList = new ArrayList<String>();
        String[] months = new DateFormatSymbols().getMonths();
        for (int i = 0; i < months.length; i++) {
            monthsList.add(months[i]);
        }
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, monthsList);
        mSpinnerExpairyMonth.setAdapter(monthAdapter);
        //Set Array Of Years To Spinner
        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = thisYear; i <= 2050; i++) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, years);
        mSpinnerExpairyYear.setAdapter(yearAdapter);
    }

    private void setAmount() {
        switch (noOfDays) {
            case 1:
                amount = 50;
                mTextAmount.setText("Pay Amount " + amount + "₦");
                break;
            case 7:
                amount = 300;
                mTextAmount.setText("Pay Amount " + amount + "₦");
                break;
            case 30:
                amount = 1000;
                mTextAmount.setText("Pay Amount " + amount + "₦");

        }
    }

    private void payInit() {
        Util util = new Util();
        if (!util.isEmailValid(mEditEmail.getText().toString().trim())) {
            mEditEmail.setError("Please Input Valid Email Address");
        } else if (mEditCard.getText().toString().trim().isEmpty()) {
            mEditCard.setError("Please Input Card Number");
        } else if (mEditCVV.getText().toString().trim().length() < 3 || mEditCVV.getText().toString().trim().length() > 3) {
            mEditCVV.setError("Please Input Valid Card CVV");
        } else {
            //Check If user is connected to the internet or not
            if (AppStatus.getInstance(this).isOnline()) {
                String email = mEditEmail.getText().toString();
                String cardNumber = mEditCard.getText().toString();
                int exMonth = util.getMonthInt(mSpinnerExpairyMonth.getSelectedItem().toString().toLowerCase());//convert month(String) to Int
                int exYear = Integer.parseInt(mSpinnerExpairyYear.getSelectedItem().toString());
                String cvc = mEditCVV.getText().toString();
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Please Wait...");
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                Pay(email, cardNumber, exMonth, exYear, amount, cvc);
            } else {
                //Network Connection Error
                AlertDialog.Builder builder = new AlertDialog.Builder(PayByPaystack.this);
                builder.setTitle("Network Error");
                builder.setMessage("Please Connect To The Internet And Try Again");

                String positiveText = getString(android.R.string.ok);
                builder.setPositiveButton(positiveText,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog dialog = builder.create();
                // display dialog
                dialog.show();
            }
        }
    }

    //Method To Process Payment
    private void Pay(String email, String cardNumber, int expiryMonth, int expiryYear, int amount, String cvc) {
        //create a charge
        Charge charge = new Charge();
        charge.setCard(new Card.Builder(cardNumber, expiryMonth, expiryYear, cvc).build());
        charge.setAmount(amount);
        charge.setEmail(email);
        PaystackSdk.chargeCard(this, charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction) {
                // This is called only after transaction is deemed successful
                // retrieve the transaction, and send its reference to your server
                // for verification.
                progressDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(PayByPaystack.this);
                builder.setTitle("Success");
                builder.setMessage("Payment Processed Successfully");

                String positiveText = getString(android.R.string.ok);
                builder.setPositiveButton(positiveText,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });




                long cutoff = new Date(System.currentTimeMillis()+noOfDays*60*60*1000).getTime();

                databaseReference.child(getString(R.string.db_key_users)).child(user.getUid()).child(getString(R.string.db_key_Premium)).child(getString(R.string.db_key_isPaid)).setValue(true);
                databaseReference.child(getString(R.string.db_key_users)).child(user.getUid()).child(getString(R.string.db_key_Premium)).child(getString(R.string.db_key_timestamp)).setValue(cutoff);

                Log.e(TAG, ": get cutoff" + cutoff);



               /* long cutoff = new Date().getTime() - 2*60*1000;
                Query oldBug = databaseReference.child("users").child(user.getDisplayName()).child("Premium").endAt(cutoff);
                oldBug.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot itemSnapshot: snapshot.getChildren()) {
                            itemSnapshot.getRef().setValue(false);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });*/


                AlertDialog dialog = builder.create();
                // display dialog
                dialog.show();
            }

            @Override
            public void beforeValidate(Transaction transaction) {
                // This is called only before requesting OTP
                // Save reference so you may send to server. If
                // error occurs with OTP, you should still verify on server
            }

            @Override
            public void onError(Throwable error) {
                //handle Payment Error Here
                progressDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(PayByPaystack.this);
                builder.setTitle("Payment Error ");
                builder.setMessage(error.getMessage());

                String positiveText = getString(android.R.string.ok);
                builder.setPositiveButton(positiveText,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        });

                AlertDialog dialog = builder.create();
                // display dialog
                dialog.show();
            }

        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btPay:
                payInit();//Start Payment Process
                break;
        }
    }
}