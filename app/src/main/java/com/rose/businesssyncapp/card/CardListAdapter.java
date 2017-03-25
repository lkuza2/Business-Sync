package com.rose.businesssyncapp.card;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.rose.businesssyncapp.R;

import java.util.ArrayList;

/**
 * Created by kuzalj on 3/25/2017.
 */
public class CardListAdapter extends BaseAdapter{

    public CardListAdapter(Context context, ArrayList<String> cards){
        this.context = context;
        this.cards = cards;
    }

    private ArrayList<String> cards;
    private Context context;

    @Override
    public int getCount() {
        return cards.size();
    }

    @Override
    public Object getItem(int position) {
        return cards.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.card, null);
        }

        ((TextView) view.findViewById(R.id.card_id)).setText(getItem(position).toString());

        return view;
    }
}
