package com.rose.businesssyncapp.card;


import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.rose.businesssyncapp.R;
import com.rose.businesssyncapp.user.User;

import java.util.ArrayList;

import static android.nfc.NfcAdapter.FLAG_READER_NFC_A;
import static android.nfc.NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;

/**
 * Created by kuzalj on 3/25/2017.
 */
public class RegisterCardFragment extends Fragment implements NfcAdapter.ReaderCallback {

    private NfcAdapter nfcAdapter;
    private DatabaseReference database;
    private FirebaseAuth auth;
    private ArrayList<String> cards;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view  = inflater.inflate(R.layout.fragment_register_card, container, false);
        database = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        nfcAdapter = NfcAdapter.getDefaultAdapter(getContext());
        nfcAdapter.enableReaderMode(getActivity(), this, FLAG_READER_NFC_A | FLAG_READER_SKIP_NDEF_CHECK, null);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        database.child("Users").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User USER = dataSnapshot.getValue(User.class);
                cards = USER.cards;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(cards == null)
                            cards = new ArrayList<String>();
                        ((ListView) RegisterCardFragment.this.getView().findViewById(R.id.card_scroll)).setAdapter(
                                new CardListAdapter(getContext(), cards));
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Card card = new Card(auth.getCurrentUser().getUid());
                database.child("cards").child(string.toString()).setValue(card);

                boolean removed = false;
                for(int i = 0; i < cards.size(); i++){
                    if(cards.get(i).equals(cardID)) {
                        cards.remove(i);
                        removed = true;
                    }
                }
                if(!removed)
                    cards.add(cardID);
                else
                    database.child("cards").child(cardID).removeValue();

                database.child("Users").child(auth.getCurrentUser().getUid()).child("cards").setValue(cards);
                ((ListView) RegisterCardFragment.this.getView().findViewById(R.id.card_scroll)).setAdapter(
                        new CardListAdapter(getContext(), cards));
            }
        });
    }
}
