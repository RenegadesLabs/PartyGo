package com.renegades.labs.partygo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Виталик on 02.04.2017.
 */

public class MyListViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<MyContact> contactsList;

    public MyListViewAdapter(Context mContext, List<MyContact> contactsList) {
        this.mContext = mContext;
        this.contactsList = contactsList;
    }

    static class ViewHolder {
        TextView name;
        TextView phone;
        CheckBox checkBox;
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

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater)
                    mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.contacts_list_item, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.contact_name);
            viewHolder.phone = (TextView) view.findViewById(R.id.contact_phone);
            viewHolder.checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.name.setText(contactsList.get(i).getName());
        viewHolder.phone.setText(contactsList.get(i).getPhone());
        viewHolder.checkBox.setChecked(contactsList.get(i).isChecked());

        return view;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }
}
