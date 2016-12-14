package com.renegades.labs.partygo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Виталик on 04.12.2016.
 */

public class MyListViewAdapter extends BaseAdapter {

    Context mContext;
    List<MyContact> contactsList;

    public MyListViewAdapter(Context mContext, List<MyContact> contactsList) {
        this.mContext = mContext;
        this.contactsList = contactsList;
    }

    static class ViewHolder {
        TextView name;
        TextView phone;
    }

    @Override
    public int getCount() {
        return contactsList.size();
    }

    @Override
    public Object getItem(int i) {
        return contactsList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        if (view == null){
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.contacts_list_item, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.contact_name);
            viewHolder.phone = (TextView) view.findViewById(R.id.contact_phone);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.name.setText(((MyContact) getItem(i)).getName());
        viewHolder.phone.setText(((MyContact) getItem(i)).getPhone());

        return view;
    }
}
