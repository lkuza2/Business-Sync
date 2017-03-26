package com.rose.businesssyncapp.contacts;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rose.businesssyncapp.LoginActivity;
import com.rose.businesssyncapp.R;
import com.rose.businesssyncapp.home.HomeActivity;

import java.util.ArrayList;

import static android.nfc.NfcAdapter.FLAG_READER_NFC_A;
import static android.nfc.NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;

/**
 * Created by kuzalj on 3/25/2017.
 */
public class AddContactFragment extends AppCompatActivity implements NfcAdapter.ReaderCallback{

    private NfcAdapter nfcAdapter;
    private DatabaseReference database;
    private FirebaseAuth auth;
    private static final int RESULT_CONFIRM = 66;

    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_contact);
        database = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.enableReaderMode(this, this, FLAG_READER_NFC_A | FLAG_READER_SKIP_NDEF_CHECK, null);
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
    public void onTagDiscovered(Tag tag) {
        byte[] tagValue = tag.getId();
        final StringBuilder string = new StringBuilder();
        final String cardID;
        for(byte octet : tagValue) {
            string.append(String.format("%02x", octet));
        }
        cardID = string.toString();
        Intent intent = new Intent(this, ConfirmContactActivity.class);
        intent.putExtra("com.rose.businesssyncapp.cardID", cardID);
        startActivityForResult(intent,RESULT_CONFIRM);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_CONFIRM) {

        }
    }
}
