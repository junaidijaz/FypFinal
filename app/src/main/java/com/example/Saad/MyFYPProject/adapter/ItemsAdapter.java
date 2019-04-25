package com.example.Saad.MyFYPProject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.Saad.MyFYPProject.R;
import com.example.Saad.MyFYPProject.models.items.Item;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ExampleViewHolder> {
    private ArrayList<Item> data;
    private OnItemClickListener mListener;
    Context context;

    private static final String TAG = "ItemsAdapter";


    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {

        private TextView itemPrice, itemName, categoryTextView, btnMinus, btnPlus, countTextView;
        LinearLayout btnSendFeedBack;
        ImageView itemPicture;
        Button btnAddToCart;


        public ExampleViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);

            itemPrice = itemView.findViewById(R.id.itemPrice);
            itemName = itemView.findViewById(R.id.itemName);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            btnMinus = itemView.findViewById(R.id.minus);
            countTextView = itemView.findViewById(R.id.countTextView);
            btnPlus = itemView.findViewById(R.id.plus);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
            itemPicture = itemView.findViewById(R.id.itemPicture);


            btnPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position, btnPlus, countTextView);
                        }
                    }
                }
            });

            btnMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position, btnMinus, countTextView);
                        }
                    }
                }
            });

            btnAddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position, btnAddToCart, null);
                        }
                    }
                }
            });
        }
    }

    public ItemsAdapter(Context context, ArrayList<Item> exampleList) {
        data = exampleList;
        this.context = context;
    }

    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.productlayout, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        Item currentItem = data.get(position);
        holder.itemName.setText(currentItem.getName());
        holder.itemPrice.setText(currentItem.getPrice());
        holder.categoryTextView.setText(currentItem.getCategory());
        holder.countTextView.setText("1");

        if (!TextUtils.isEmpty(currentItem.getImage())) {

            Picasso.get().load(currentItem.getImage()).into(holder.itemPicture);
//            Glide.with(context)
//                    .load(currentItem.getImage())
////                    .centerCrop()
////                    .placeholder(R.drawable.dummy_dp)
//                    .into(holder.itemPicture);
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
