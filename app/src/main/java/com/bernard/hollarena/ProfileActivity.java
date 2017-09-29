package com.bernard.hollarena;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.bernard.hollarena.AppStatus.context;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    ImageView mProfileImage;
    TextView mProfileDisplayName, mProfileEmail;
    Button mProfileSendToken;

    DatabaseReference mDatabaseReference;
    DatabaseReference mTokenRequestDatabaseReference;
    DatabaseReference mTokenDatabaseReference;
    DatabaseReference mRootRef;

    FirebaseUser mCurrentUser;
    private ProgressDialog mProgressDialog;
    private int mCurrent_state = -1; //-1 token not sent:
    // 0 token sent :
    // 1 token received
    // 2:token accepted

    private boolean canSendToken = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id = getIntent().getStringExtra("user_id");

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.db_key_users)).child(user_id);
        mTokenRequestDatabaseReference = FirebaseDatabase.getInstance().getReference().child("token_request");
        mTokenDatabaseReference = FirebaseDatabase.getInstance().getReference().child("token");
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        initViews();


        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading user data");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String display_name = dataSnapshot.child(getString(R.string.db_key_name)).getValue().toString();
                String profile_email = dataSnapshot.child(getString(R.string.db_key_email)).getValue().toString();
                String profile_image = "";
                if (dataSnapshot.hasChild(getString(R.string.db_key_thumb_image))) {
                    profile_image = dataSnapshot.child(getString(R.string.db_key_thumb_image)).getValue().toString();
                    Picasso.with(ProfileActivity.this).load(profile_image).placeholder(R.drawable.default_avatar).into(mProfileImage);
                }
                mProfileDisplayName.setText(display_name);
                mProfileEmail.setText(profile_email);

                //token list feature
                mTokenRequestDatabaseReference.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(user_id)) {
                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                            if (req_type.equals("received")) {
                                mCurrent_state = 1;
                                mProfileSendToken.setText
                                        ("Accept Token");

                            } else if (req_type.equals("sent")) {
                                mCurrent_state = 0;
                                mProfileSendToken.setText("Cancel Token");
                            }
                        } else{
                            mTokenDatabaseReference.child(mCurrentUser.getUid())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChild(user_id)){
                                                mCurrent_state = 2;
                                                mProfileSendToken.setVisibility(View.GONE);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }

                        mProgressDialog.dismiss();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mProfileSendToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProfileSendToken.setEnabled(false);
                mProfileSendToken.setBackgroundColor(getResources().getColor(R.color.gray_btn_bg_color));
                mRootRef.child(getString(R.string.db_key_users)).child(mCurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String token_sent_count = dataSnapshot.child(getString(R.string.db_key_token_count)).getValue().toString();
                        String premium_member = dataSnapshot.child(getString(R.string.db_key_Premium)).getValue().toString();

                        if (premium_member.equals("true") && Integer.parseInt(token_sent_count) <25){
                            Log.e(TAG, "onDataChange: is premium and value of token less than 25" );
                            canSendToken = true;
                        } else if (premium_member.equals("false") && Integer.parseInt(token_sent_count) < 5){
                            Log.e(TAG, "onDataChange: is not premium and value of token less than 5" );
                            canSendToken = true;

                        } else {
                            Log.e(TAG, "onDataChange: reached limit==> "+Integer.parseInt(token_sent_count));
                            canSendToken = false;

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                switch (mCurrent_state) {
                    case -1://token not sent
                       /* Map requestMap = new HashMap();
                        requestMap.put("token_request/"+mCurrentUser.getUid()+"/"+user_id+"/request_type","sent");
                        requestMap.put("token_request/"+user_id+"/"+mCurrentUser.getUid()+"/request_type","received");

                        mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError==null){
                                    Toast.makeText(ProfileActivity.this, "no errr", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ProfileActivity.this, " errr", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });*/
                       if (canSendToken) {
                           mTokenRequestDatabaseReference.child(mCurrentUser.getUid()).child(user_id)
                                   .child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   if (task.isSuccessful()) {
                                       Toast.makeText(ProfileActivity.this, "Successfully sent", Toast.LENGTH_SHORT).show();
                                       mTokenRequestDatabaseReference.child(user_id).child(mCurrentUser.getUid())
                                               .child("request_type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {

                                           @RequiresApi(api = Build.VERSION_CODES.M)
                                           @Override
                                           public void onSuccess(Void aVoid) {
                                               Toast.makeText(ProfileActivity.this, "received successfully", Toast.LENGTH_SHORT).show();
                                               mCurrent_state = 0;
                                               mProfileSendToken.setEnabled(true);
                                               mProfileSendToken.setText("Cancel Token");
                                               mProfileSendToken.setBackgroundColor(ContextCompat.getColor(ProfileActivity.this, R.color.red_btn_bg_color));

                                               //increase token count

                                               mRootRef.child(getString(R.string.db_key_users)).child(mCurrentUser.getUid()).child(getString(R.string.db_key_token_count)).addListenerForSingleValueEvent(new ValueEventListener() {
                                                   @Override
                                                   public void onDataChange(DataSnapshot dataSnapshot) {
                                                       int token_count = Integer.parseInt(dataSnapshot.getValue().toString());
                                                       token_count++;
                                                       mRootRef.child(getString(R.string.db_key_users)).child(mCurrentUser.getUid()).child(getString(R.string.db_key_token_count)).setValue(token_count);
                                                   }

                                                   @Override
                                                   public void onCancelled(DatabaseError databaseError) {

                                                   }
                                               });

                                           }
                                       });
                                   } else {
                                       Toast.makeText(ProfileActivity.this, "Failed to sent", Toast.LENGTH_SHORT).show();

                                   }
                               }
                           });
                       } else {
                           Toast.makeText(ProfileActivity.this, "You reached maximum limit", Toast.LENGTH_SHORT).show();
                       }
                        break;
                    case 0: //Cancel request state
                        mTokenRequestDatabaseReference.child(mCurrentUser.getUid()).child(user_id)
                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    mTokenRequestDatabaseReference.child(user_id).child(mCurrentUser.getUid())
                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                mProfileSendToken.setEnabled(true);
                                                mCurrent_state = -1;
                                                mProfileSendToken.setText(getString(R.string.send_token));
                                                mProfileSendToken.setBackgroundColor(ContextCompat.getColor(ProfileActivity.this, R.color.blue));
                                            }

                                        }
                                    });
                                }
                            }
                        });
                        break;
                    case 1: //token received and accepted
                        final String current_date = DateFormat.getDateTimeInstance().format(new Date());
                        mTokenDatabaseReference.child(mCurrentUser.getUid())
                                .child(user_id).child("date").setValue(current_date)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            mTokenDatabaseReference.child(user_id).
                                                    child(mCurrentUser.getUid()).child("date").setValue(current_date)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                mTokenRequestDatabaseReference.child(mCurrentUser.getUid()).child(user_id)
                                                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            mTokenRequestDatabaseReference.child(user_id).child(mCurrentUser.getUid())
                                                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        mProfileSendToken.setEnabled(false);
                                                                                        mCurrent_state = 2;
                                                                                        mProfileSendToken.setVisibility(View.GONE);
//                                                                                                mProfileSendToken.setBackgroundColor(ContextCompat.getColor(ProfileActivity.this, R.color.blue));
                                                                                    }

                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });
                        break;
                }
            }
        });


    }

    private void initViews() {
        mProfileDisplayName = (TextView) findViewById(R.id.profile_display_name);
        mProfileEmail = (TextView) findViewById(R.id.id_profile_email);
        mProfileImage = (ImageView) findViewById(R.id.profile_image);
        mProfileSendToken = (Button) findViewById(R.id.token_bt);
    }
}
