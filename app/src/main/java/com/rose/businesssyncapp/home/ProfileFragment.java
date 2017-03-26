package com.rose.businesssyncapp.home;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rose.businesssyncapp.R;
import com.rose.businesssyncapp.user.User;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by kuzalj on 3/25/2017.
 */
@RuntimePermissions
public class ProfileFragment extends Fragment implements View.OnClickListener {

    private DatabaseReference database;
    FirebaseAuth auth;
    FirebaseStorage storage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view  = inflater.inflate(R.layout.fragment_profile, container, false);
        database = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                User user = dataSnapshot.getValue(User.class);
                if(getView() == null){
                    Log.d("BusinessSync", "NULL");
                }
                ((Button) view.findViewById(R.id.save_profile_button)).setEnabled(true);
                Log.d("BusinessSync", "Data read");
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("BusinessSync", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        database.child("Users").child(auth.getCurrentUser().getUid()).addValueEventListener(userListener);

        view.findViewById((R.id.save_profile_button)).setOnClickListener(this);
        view.findViewById(R.id.profile_image).setOnClickListener(this);
        startUp(view);
        return view;
    }

    private void startUp(final View view){
        // Reference to an image file in Firebase Storage
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        StorageReference userRef = storageRef.child(auth.getCurrentUser().getUid() + "/" + "profile.jpg");
        // Load the image using Glide
        if(!userRef.getName().equals("")) {
            Glide.with(this /* context */)
                    .using(new FirebaseImageLoader())
                    .load(userRef)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into((ImageView) view.findViewById(R.id.profile_image));
        }
        database.child("Users").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(user.firstName != null){
                    ((TextView) view.findViewById(R.id.first_name)).setText(user.firstName);
                }

                if(user.lastName != null){
                    ((TextView) view.findViewById(R.id.last_name)).setText(user.lastName);
                }

                if(user.phone != null){
                    ((TextView) view.findViewById(R.id.phone_text)).setText(user.phone);
                }

                if(user.wrkemail != null){
                    ((TextView) view.findViewById(R.id.email_text)).setText(user.wrkemail);
                }

                if(user.company != null){
                    ((TextView) view.findViewById(R.id.company_textt)).setText(user.company);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    // PICK_PHOTO_CODE is a constant integer
    public final static int PICK_PHOTO_CODE = 1046;

    // Trigger gallery selection for a photo
    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void onPickPhoto() {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Uri photoUri = data.getData();
            // Do something with the photo based on Uri
            Bitmap selectedImage = null;
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
                selectedImage = RotateBitmap(selectedImage, 90);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Load the selected image into a preview
            ImageView ivPreview = (ImageView) (getView().findViewById(R.id.profile_image));
            ivPreview.setImageBitmap(selectedImage);

            // Create a storage reference from our app
            StorageReference storageRef = storage.getReference();

// Create a reference to "mountains.jpg"
            StorageReference userRef = storageRef.child(auth.getCurrentUser().getUid() + "/" + "profile.jpg");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] dataBytes = baos.toByteArray();

            UploadTask uploadTask = userRef.putBytes(dataBytes);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                }
            });
        }
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0,
                source.getWidth(), source.getHeight(),
                matrix, true);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case  R.id.save_profile_button:
                ((Button) getView().findViewById(R.id.save_profile_button)).setEnabled(false);
                String email = auth.getCurrentUser().getEmail();
                String firstName = ((TextView) getView().findViewById(R.id.first_name)).getText().toString();
                String lastName = ((TextView) getView().findViewById(R.id.last_name)).getText().toString();
                String emailText = ((TextView) getView().findViewById(R.id.email_text)).getText().toString();
                String phoneText = ((TextView) getView().findViewById(R.id.phone_text)).getText().toString();
                String companyText = ((TextView) getView().findViewById(R.id.company_textt)).getText().toString();



                database.child("Users").child(auth.getCurrentUser().getUid()).child("firstName").setValue(firstName);
                database.child("Users").child(auth.getCurrentUser().getUid()).child("lastName").setValue(lastName);
                database.child("Users").child(auth.getCurrentUser().getUid()).child("email").setValue(email);
                database.child("Users").child(auth.getCurrentUser().getUid()).child("phone").setValue(phoneText);
                database.child("Users").child(auth.getCurrentUser().getUid()).child("wrkemail").setValue(emailText);
                database.child("Users").child(auth.getCurrentUser().getUid()).child("company").setValue(companyText);

                break;
            case R.id.profile_image:
                ProfileFragmentPermissionsDispatcher.onPickPhotoWithCheck(ProfileFragment.this);
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        ProfileFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
