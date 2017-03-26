package com.rose.businesssyncapp.contacts;

import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rose.businesssyncapp.R;
import com.rose.businesssyncapp.card.CardListAdapter;
import com.rose.businesssyncapp.card.RegisterCardFragment;
import com.rose.businesssyncapp.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.nfc.NfcAdapter.FLAG_READER_NFC_A;
import static android.nfc.NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;

/**
 * Created by kuzalj on 3/25/2017.
 */
public class ConfirmContactActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference database;
    private FirebaseAuth auth;
    FirebaseStorage storage;
    String userID = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_contact);

        database = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        String cardID = getIntent().getStringExtra("com.rose.businesssyncapp.cardID");
        database.child("cards").child(cardID).child("UserID").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userID = dataSnapshot.getValue().toString();
                showUser();

                StorageReference storageRef = storage.getReference();

                StorageReference userRef = storageRef.child(userID + "/" + "profile.jpg");
                // Load the image using Glide
                if(!userRef.getName().equals("")) {
                    Glide.with(ConfirmContactActivity.this /* context */)
                            .using(new FirebaseImageLoader())
                            .load(userRef)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into((ImageView) findViewById(R.id.profile_image_add));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        findViewById(R.id.add_button).setOnClickListener(this);
        findViewById(R.id.cancel_button).setOnClickListener(this);
    }

    private void showUser(){
        database.child("Users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User USER = dataSnapshot.getValue(User.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) findViewById(R.id.first_name_add)).setText(USER.firstName);
                        ((TextView) findViewById(R.id.last_name_add)).setText(USER.lastName);
                        ((TextView) findViewById(R.id.phone_add)).setText(USER.phone);
                        ((TextView) findViewById(R.id.wrk_email_add)).setText(USER.wrkemail);
                        ((TextView) findViewById(R.id.company_add)).setText(USER.company);

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        super.addContentView(view, params);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.add_button:
                String key = database.child("Users").child(auth.getCurrentUser().getUid()).child("contacts").push().getKey();

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/Users/" + auth.getCurrentUser().getUid() + "/contacts/" + key, userID);

                database.updateChildren(childUpdates);
                finish();
                break;
            case R.id.cancel_button:
                finish();
                break;
            default:
                break;
        }
    }
}
