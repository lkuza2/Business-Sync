package com.rose.businesssyncapp.contacts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.rose.businesssyncapp.R;
import com.rose.businesssyncapp.user.User;

import java.util.ArrayList;

/**
 * Created by kuzalj on 3/25/2017.
 */
public class ViewContactsFragment extends Fragment implements LoadContactsTask.OnReadContactsCompleteListener {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view  = inflater.inflate(R.layout.fragment_view_contacts, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        new LoadContactsTask(getContext(), this).execute();

        // specify an adapter (see also next example)
        ((SwipeRefreshLayout) view.findViewById(R.id.swiperefresh)).setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("BusinessSync", "onRefresh called from SwipeRefreshLayout");

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        new LoadContactsTask(ViewContactsFragment.this.getContext(), ViewContactsFragment.this).execute();
                    }
                }
        );


        return view;
    }




    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
    public void onContactsReadComplete(final ArrayList<User> users) {
          getActivity().runOnUiThread(new Runnable() {
              @Override
              public void run() {
                  adapter = new ContactsAdapter(users, getContext());
                  recyclerView.setAdapter(adapter);
                  ViewContactsFragment.this.getView().findViewById(R.id.contact_progress_bar).setVisibility(View.INVISIBLE);
                  ((SwipeRefreshLayout) getView().findViewById(R.id.swiperefresh)).setRefreshing(false);
              }
          });
    }
}
