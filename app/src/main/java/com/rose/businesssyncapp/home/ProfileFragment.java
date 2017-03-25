package com.rose.businesssyncapp.home;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.rose.businesssyncapp.R;
import com.rose.businesssyncapp.user.User;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by kuzalj on 3/25/2017.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    private DatabaseReference database;
    FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view  = inflater.inflate(R.layout.fragment_profile, container, false);
        database = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
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
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        database.child("Users").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(user.firstName != null){
                    ((TextView) getView().findViewById(R.id.first_name)).setText(user.firstName);
                }

                if(user.lastName != null){
                    ((TextView) getView().findViewById(R.id.last_name)).setText(user.lastName);
                }
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
    public void onClick(View v) {
        switch(v.getId()){
            case  R.id.save_profile_button:
                ((Button) getView().findViewById(R.id.save_profile_button)).setEnabled(false);
                User user = new User(auth.getCurrentUser().getEmail(),
                        ((TextView) getView().findViewById(R.id.first_name)).getText().toString(),
                        ((TextView) getView().findViewById(R.id.last_name)).getText().toString());

                database.child("Users").child(auth.getCurrentUser().getUid()).setValue(user);
                break;
            default:
                break;
        }
    }
}
