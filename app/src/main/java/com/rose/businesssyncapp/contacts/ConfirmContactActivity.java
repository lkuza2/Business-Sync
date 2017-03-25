package com.rose.businesssyncapp.contacts;

import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.rose.businesssyncapp.R;
import com.rose.businesssyncapp.card.CardListAdapter;
import com.rose.businesssyncapp.card.RegisterCardFragment;
import com.rose.businesssyncapp.user.User;

import java.util.ArrayList;

import static android.nfc.NfcAdapter.FLAG_READER_NFC_A;
import static android.nfc.NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;

/**
 * Created by kuzalj on 3/25/2017.
 */
public class ConfirmContactActivity extends AppCompatActivity {

    private DatabaseReference database;
    private FirebaseAuth auth;
    String userID = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_contact);

        database = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        String cardID = getIntent().getStringExtra("com.rose.businesssyncapp.cardID");
        database.child("cards").child(cardID).child("UserID").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userID = dataSnapshot.getValue().toString();
                showUser();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
}
