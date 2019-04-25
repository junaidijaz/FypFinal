package com.example.Saad.MyFYPProject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ProductCustomAdaptor extends ArrayAdapter<ProductClass> {

    private Context mContext;
    private int mResourceID;

    static  class ViewHolder{
        TextView nameTextView;
        TextView priceTextView;
        TextView quantityTextView;
        TextView subTotalTextView;

    }

    public ProductCustomAdaptor(@NonNull Context context, int resource, @NonNull ArrayList objects) {
        super(context, resource, objects);
        mContext = context;
        mResourceID = resource;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        try {
            //ViewHolder object
            final ViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(mResourceID, parent, false);
                holder = new ViewHolder();
                holder.nameTextView = convertView.findViewById(R.id.orderProductNameTextView);
                holder.priceTextView = convertView.findViewById(R.id.orderProductPriceTextView);
                holder.quantityTextView = convertView.findViewById(R.id.orderProductQuantityTextView);
                holder.subTotalTextView = convertView.findViewById(R.id.orderProductSubtotalTextView);

                holder.nameTextView.setText(getItem(position).getName());
                holder.priceTextView.setText(String.valueOf(getItem(position).getPrice()));

                holder.quantityTextView.setText(String.valueOf(getItem(position).getQuantity()));

                holder.subTotalTextView.setText(String.valueOf(getItem(position).getSubTotal()));
                convertView.setTag(holder);


            }else{
                holder = (ViewHolder) convertView.getTag();
                holder.nameTextView.setText(getItem(position).getName());
                holder.priceTextView.setText(String.valueOf(getItem(position).getPrice()));

                holder.quantityTextView.setText(String.valueOf(getItem(position).getQuantity()));

                holder.subTotalTextView.setText(String.valueOf(getItem(position).getSubTotal()));

            }
            return convertView;
        }catch (Exception ex){
            ex.printStackTrace();
            Toast.makeText(mContext, "Error in Custom Adaptor", Toast.LENGTH_SHORT).show();
            return null;
        }

    }
}
