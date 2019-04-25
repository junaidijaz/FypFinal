package com.example.Saad.MyFYPProject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Saad.MyFYPProject.models.orders.Orders;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class OrderDetailsCustomAdapter extends ArrayAdapter<Orders> {
    private Context mContext;

    private int mResource;

    private static class ViewHandler{
        TextView addressTextView;
        TextView totalBillTextView;

        TextView statusTextView;

    }
    OrderDetailsCustomAdapter(Context context, int resource,@NonNull ArrayList<Orders> object) {
        super(context,resource,object);
        mContext = context;
        mResource = resource;

    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        try{
            //ViewHolder object
            final  ViewHandler holder;
            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(mResource, parent, false);
                holder= new  ViewHandler();
                holder.addressTextView =  convertView.findViewById(R.id.orderAddressTextView);
                holder.totalBillTextView = convertView.findViewById(R.id.orderTotalBillTextView);
                holder.statusTextView = convertView.findViewById(R.id.orderStatusTextView);
                convertView.setTag(holder);


                holder.addressTextView.setText(getItem(position).getAddress());
                holder.totalBillTextView.setText(getItem(position).getGrand_total());
                if(getItem(position).getOrder_status().equals("1")){
                    holder.statusTextView.setText("Completed");
                }else{
                    holder.statusTextView.setText("Pending");
                }

            }
            else{
                holder = (ViewHandler) convertView.getTag();
                convertView.setTag(holder);
                holder.addressTextView.setText(getItem(position).getAddress());
                holder.totalBillTextView.setText(getItem(position).getGrand_total());
                if(getItem(position).getOrder_status().equals("1")){
                    holder.statusTextView.setText("Completed");
                }else{
                    holder.statusTextView.setText("Pending");
                }
            }
            return convertView;
        }catch (IllegalArgumentException e){
            Log.e(TAG, "getView: IllegalArgumentException: " + e.getMessage() );
            return convertView;
        }catch (java.lang.NullPointerException ex){
            Toast.makeText(mContext, ex.toString(), Toast.LENGTH_SHORT).show();
            Log.e("Error",ex.toString());
            return convertView;
        }

    }//end of getView





}//end of class
