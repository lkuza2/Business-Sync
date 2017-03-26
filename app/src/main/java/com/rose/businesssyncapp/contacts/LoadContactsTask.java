package com.rose.businesssyncapp.contacts;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rose.businesssyncapp.R;
import com.rose.businesssyncapp.user.User;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by kuzalj on 3/25/2017.
 */
public class LoadContactsTask extends AsyncTask<Void, Void, Void>{

    private DatabaseReference database;
    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private ArrayList<User> users = new ArrayList<User>();
    private Context context;
    private long size = Integer.MAX_VALUE;
    private int read = 0;
    private boolean readComplete = false;
    OnReadContactsCompleteListener listener;

    public LoadContactsTask(Context context, OnReadContactsCompleteListener listener){
        this.context = context;
        database = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        getContacts();
        while(read < size){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        for(User user : users){
            StorageReference storageRef = storage.getReference();

            StorageReference userRef = storageRef.child(user.userID + "/" + "profile.jpg");

            // Load the image using Glide
            if(!userRef.getName().equals("")) {
                try {
                    Bitmap bitmap = Glide.with(LoadContactsTask.this.context)
                            .using(new FirebaseImageLoader())
                            .load(userRef)
                            .asBitmap()
                            .centerCrop()
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(200, 200)
                            .get();
                    user.bitmap = bitmap;
                } catch (InterruptedException | ExecutionException e) {
                    Log.e("BusinessSync", Log.getStackTraceString(e));
                }
            }
        }

        listener.onContactsReadComplete(users);
        return null;
    }

    private void getContacts(){
        // My top posts by number of stars
        database.child("Users").child(auth.getCurrentUser().getUid()).child("contacts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                size = dataSnapshot.getChildrenCount();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String uid = postSnapshot.getValue().toString();
                    loadUserData(uid, i);
                    i++;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("BusinessSync", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });

    }

    private void loadUserData(final String uid, final int index){
        database.child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User USER = dataSnapshot.getValue(User.class);
                USER.userID = uid;
                users.add(USER);

                read++;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("BusinessSync", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });
    }

    public interface OnReadContactsCompleteListener{

        void onContactsReadComplete(ArrayList<User> users);
    }
}
