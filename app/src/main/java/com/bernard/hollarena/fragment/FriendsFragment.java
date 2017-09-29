package com.bernard.hollarena.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bernard.hollarena.ProfileActivity;
import com.bernard.hollarena.R;
import com.bernard.hollarena.activity.ChatActivity;
import com.bernard.hollarena.activity.UserListActivity;
import com.bernard.hollarena.model.Friends;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.attr.key;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    String mCurrent_user_id;
    RecyclerView mFriendsListRecyclerView;
    DatabaseReference mFriendsDatabaseReference;
    DatabaseReference mUserDatabaseReference;
    FirebaseAuth mAuth;

    View mView;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =inflater.inflate(R.layout.fragment_friends, container, false);

        mFriendsListRecyclerView = (RecyclerView) mView.findViewById(R.id.friends_list);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mFriendsDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("token").child(mCurrent_user_id);
        mUserDatabaseReference = FirebaseDatabase.getInstance().getReference().
                child(getString(R.string.db_key_users));

        mFriendsListRecyclerView.setHasFixedSize(true);
        mFriendsListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Friends,FriendsViewHolder> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(
                Friends.class,
                R.layout.layout_all_users,
                FriendsViewHolder.class,
                mFriendsDatabaseReference
        ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder viewHolder, final Friends model, int position) {


                final String list_user_id = getRef(position).getKey();

                mUserDatabaseReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String userName = dataSnapshot.child(getString(R.string.db_key_name))
                                .getValue().toString();
                        if (dataSnapshot.hasChild("thumb_image")) {
                            final String thumb_image = dataSnapshot.child(getString(R.string.db_key_thumb_image))
                                    .getValue().toString();
                            viewHolder.setUserImage(thumb_image, getContext());
                        }
                        String user_online = dataSnapshot.child(getString(R.string.db_key_online)).getValue().toString();

                        viewHolder.setDate(userName+"\n"+model.getDate());
                        viewHolder.setUserOnlineImage(user_online);

                        viewHolder.mHolderView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[]{"Open Profile","Send message"};
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                builder.setTitle("Select Options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //click event for each item
                                        if (which==0){
                                            Intent profileIntent =new Intent(FriendsFragment.this.getActivity(), ProfileActivity.class);
                                            profileIntent.putExtra(getString(R.string.intent_key_user_id),list_user_id);
                                            startActivity(profileIntent);

                                        }else {
                                            Intent chatIntent = new Intent(getContext(),ChatActivity.class);
                                            chatIntent.putExtra(getString(R.string.intent_key_user_id),list_user_id);
                                            chatIntent.putExtra(getString(R.string.intent_key_user_name),userName);
                                            chatIntent.putExtra(getString(R.string.intent_key_user_image),"https://upload.wikimedia.org/wikipedia/commons/1/1e/Default-avatar.jpg");
                                            startActivity(chatIntent);
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        mFriendsListRecyclerView.setAdapter(friendsRecyclerViewAdapter);
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {
        private String date;
        View mHolderView;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            mHolderView = itemView;
        }

        public void setDate(String date) {
            TextView userNameView = (TextView) mHolderView.findViewById(R.id.all_user_name);
            userNameView.setText(date);
        }
        public void setUserImage(String thumb_image, Context applicationContext) {
            CircleImageView userImageView = (CircleImageView) mHolderView.findViewById(R.id.all_user_profile_pic);
            Picasso.with(applicationContext).load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);
        }

        public void setUserOnlineImage(String userOnline) {
            ImageView userOnlineImage = (ImageView) mHolderView.findViewById(R.id.user_online_icon);
            if (userOnline.equals("true"))
                userOnlineImage.setVisibility(View.VISIBLE);
            else
                userOnlineImage.setVisibility(View.GONE);

        }
    }
}
