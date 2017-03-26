package com.rose.businesssyncapp.contacts;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.rose.businesssyncapp.R;
import com.rose.businesssyncapp.user.User;

import java.util.ArrayList;

/**
 * Created by kuzalj on 3/25/2017.
 */
public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder>{

    private ArrayList<User> users;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CardView view;

        public ViewHolder(CardView v) {
            super(v);
            this.view = v;
        }
    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public ContactsAdapter(ArrayList<User> users) {
        this.users = users;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
       CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //holder.view
        CardView cardView = holder.view;
        User user = users.get(position);
        ((TextView) cardView.findViewById(R.id.contact_full_name)).setText(user.firstName + " " + user.lastName);
        ((TextView) cardView.findViewById(R.id.contact_company_name)).setText(user.company);
        ((TextView) cardView.findViewById(R.id.contact_phone)).setText(user.phone);
        ((TextView) cardView.findViewById(R.id.contact_wrk_email)).setText(user.wrkemail);
        ((ImageView) cardView.findViewById(R.id.contact_profile_image)).setImageBitmap(user.bitmap);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return users.size();
    }
}
