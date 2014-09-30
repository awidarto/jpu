package com.kickstartlab.android.jayonpickup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by awidarto on 7/20/14.
 */
public class OrderAdapter extends BaseAdapter {
    private LayoutInflater myInflater;
    private List<Orders> list;

    public OrderAdapter(Context context) {
        myInflater = LayoutInflater.from(context);
    }

    public void setData(List<Orders> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        convertView     = myInflater.inflate(R.layout.order_row, null);
        holder          = new ViewHolder();
        holder.head   = (TextView) convertView.findViewById(R.id.head);
        holder.subhead     = (TextView) convertView.findViewById(R.id.subhead);

        convertView.setTag(holder);

        holder.head.setText(list.get(position).buyer_name);
        holder.subhead.setText(list.get(position).pickup_status);

        return convertView;
    }

    static class ViewHolder {
        TextView head;
        TextView subhead ;
    }

}
