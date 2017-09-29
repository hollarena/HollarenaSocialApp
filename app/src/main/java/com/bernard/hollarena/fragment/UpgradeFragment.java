package com.bernard.hollarena.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.bernard.hollarena.activity.PayByPaystack;
import com.bernard.hollarena.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import co.paystack.android.PaystackSdk;

import static android.content.ContentValues.TAG;

public class UpgradeFragment extends Fragment implements BillingProcessor.IBillingHandler, View.OnClickListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    FirebaseUser user;
    BillingProcessor billingProcessor;
    Button mDay, mWeek, mMonth;
    TextView mPremiumMemberText;
    private DatabaseReference databaseReference;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public UpgradeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        initFireBasae();
        PaystackSdk.initialize(getActivity());

    }

    private void initFireBasae() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        databaseReference = mDatabase.getReference();
        user = mAuth.getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upgrade, container, false);
        mDay = (Button) view.findViewById(R.id.day_subscription_bt);
        mWeek = (Button) view.findViewById(R.id.week_subscription_bt);
        mMonth = (Button) view.findViewById(R.id.month_subscription_bt);
        mPremiumMemberText = (TextView) view.findViewById(R.id.premium_member_text);

        mDay.setOnClickListener(this);
        mWeek.setOnClickListener(this);
        mMonth.setOnClickListener(this);
        checkPayment();
        return view;
    }

    private boolean checkPayment() {
        initFireBasae();
        databaseReference.child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equals(user.getUid())) {
                    String json = dataSnapshot.getValue().toString();
                    try {
                        //get time in Millis
                        JSONObject jsonResponse = new JSONObject(json);
                        Log.e(TAG, "onChildAdded: ============>jsonResponse "+jsonResponse );

                        JSONObject jsonPremiumResponse = jsonResponse.getJSONObject(getString(R.string.db_key_Premium));
                        if (jsonPremiumResponse.has(getString(R.string.db_key_timestamp))){
                        Long storedTime = jsonPremiumResponse.getLong(getString(R.string.db_key_timestamp));
                        Long currentTime = System.currentTimeMillis();
                        if (currentTime>=storedTime){
                            Log.e(TAG, "onChildAdded: ============> has true" );
                            Log.e(TAG, "onChildAdded: time not completed  " );
                            mDay.setEnabled(false);
                            mWeek.setEnabled(false);
                            mMonth.setEnabled(false);
                            mPremiumMemberText.setText(R.string.member_text);
                            mPremiumMemberText.setTextColor(Color.GREEN);

                        } else {
                            Log.e(TAG, "onChildAdded: time completed  ");
                            mDay.setEnabled(true);
                            mWeek.setEnabled(true);
                            mMonth.setEnabled(true);
                            mPremiumMemberText.setText(R.string.you_are_not_premium_member_select_package_to_become_member);
                            mPremiumMemberText.setTextColor(Color.RED);
                            databaseReference.child(getString(R.string.db_key_users)).child(user.getUid()).child(getString(R.string.db_key_Premium)).child(getString(R.string.db_key_isPaid)).setValue(false);
                            databaseReference.child(getString(R.string.db_key_users)).child(user.getUid()).child(getString(R.string.db_key_Premium)).child(getString(R.string.db_key_timestamp)).removeValue();
                        }

                        }

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
        return false;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {

    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {

    }

    @Override
    public void onBillingInitialized() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.day_subscription_bt:
                startPaymentActivity(1);
                break;

            case R.id.week_subscription_bt:
                startPaymentActivity(7);
                break;
            case R.id.month_subscription_bt:
                startPaymentActivity(30);
        }
    }

    private void startPaymentActivity(int noOfDay) {

        Intent putValueIntent = new Intent(getActivity(), PayByPaystack.class);
        putValueIntent.putExtra(String.valueOf(R.string.intent_key_no_of_days), noOfDay);
        startActivity(putValueIntent);

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
