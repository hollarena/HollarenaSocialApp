package com.bernard.hollarena.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bernard.hollarena.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static android.R.attr.bitmap;
import static android.app.Activity.RESULT_OK;


public class SettingsFragment extends Fragment {

    private static final int GALLARY_PIC = 1;
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    FirebaseUser mFireBaseUSer;
    DatabaseReference mRef;

    StorageReference mStorageReference;

    CircleImageView mProfileImage;
    private ProgressDialog mProgressDialog;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(com.bernard.hollarena.R.layout.fragment_settings, container, false);

        mFireBaseUSer = FirebaseAuth.getInstance().getCurrentUser();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.db_key_users)).child(mFireBaseUSer.getUid());

        TextView mDisplayNameText, mEmailText;
        CircleImageView mEditImage;

        mDisplayNameText = (TextView) view.findViewById(R.id.display_name);
        mEmailText = (TextView) view.findViewById(R.id.email_id_text);
        mEditImage = (CircleImageView) view.findViewById(R.id.edit_image);
        mProfileImage = (CircleImageView) view.findViewById(R.id.profile_image);

        mDisplayNameText.setText(mFireBaseUSer.getDisplayName());
        mEmailText.setText(mFireBaseUSer.getEmail());

        mRef.keepSynced(true);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.child(getString(R.string.db_key_image)).getValue() != null) {
                    final String image = dataSnapshot.child(getString(R.string.db_key_image)).getValue().toString();
//                    Picasso.with(SettingsFragment.this.getActivity()).load(image).placeholder(R.drawable.default_avatar).into(mProfileImage);
                    Picasso.with(SettingsFragment.this.getActivity()).load(image)
                            .networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_avatar)
                            .into(mProfileImage, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                    Picasso.with(SettingsFragment.this.getActivity()).load(image).placeholder(R.drawable.default_avatar).into(mProfileImage);

                                }
                            });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mEditImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLARY_PIC);

//                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
//                        .start(getContext(),SettingsFragment.this);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLARY_PIC && resultCode == RESULT_OK) {
            Uri imageURI = data.getData();
            CropImage.activity(imageURI)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(500, 500)
                    .start(getContext(), SettingsFragment.this);
        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mProgressDialog = new ProgressDialog(SettingsFragment.this.getActivity());
                mProgressDialog.setTitle("Uploading image...");
                mProgressDialog.setMessage("Please wait while we upload your profile pic.");
                mProgressDialog.setCancelable(false);

                Uri resultUri = result.getUri();

                File thumb_filePath = new File(resultUri.getPath());
                try {

                    Bitmap thumb_bitmap = new Compressor(SettingsFragment.this.getActivity())
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(80)
                            .compressToBitmap(thumb_filePath);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final byte[] thumb_byte = baos.toByteArray();


                    StorageReference filePath = mStorageReference.child("profile_images").child(mFireBaseUSer.getUid() + ".jpg");
                    final StorageReference thumb_filepath = mStorageReference.child("profile_images").child("thumbs").child(randomVal() + ".jpg");

                    filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(SettingsFragment.this.getActivity(), "Profile pic changed", Toast.LENGTH_SHORT).show();
                                final String download_url = task.getResult().getDownloadUrl().toString();

                                UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                        String thumb_downloadurl = thumb_task.getResult().getDownloadUrl().toString();
                                        if (thumb_task.isSuccessful()) {

                                            Map update_hashmap = new HashMap();
                                            update_hashmap.put(getString(R.string.db_key_image), download_url);
                                            update_hashmap.put("thumb_image", thumb_downloadurl);

                                            mRef.updateChildren(update_hashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        mProgressDialog.dismiss();

                                                    } else {
                                                        mProgressDialog.dismiss();
                                                        Toast.makeText(SettingsFragment.this.getActivity(), "Can't upload. Please try again.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }

                                    }
                                });

                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }

        }
    }

    private String randomVal() {

        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(5);
        char tempChar;
        for (int i = 0; i < randomLength; i++) {
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();

    }
}
