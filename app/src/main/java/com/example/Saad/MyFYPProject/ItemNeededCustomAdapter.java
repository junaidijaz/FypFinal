package com.example.Saad.MyFYPProject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Saad.MyFYPProject.models.items.Item;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;


public class ItemNeededCustomAdapter extends ArrayAdapter<Item> {
    private Context mContext;
    int defaultImage =0;
    private int mResource;
    //create display options
    DisplayImageOptions options=null;
    private static class ViewHandler{
        ImageView imageView;
        TextView nameTextView;
        TextView voteTextView;
        TextView descriptionTextView;
        Button voteButton;
    }
    ItemNeededCustomAdapter(Context context, int resource,@NonNull ArrayList<Item> object) {
        super(context,resource,object);
        mContext = context;
        mResource = resource;
        setupImageLoader();

        defaultImage = mContext.getResources().getIdentifier("@drawable/image_failed",null,mContext.getPackageName());


        options = new DisplayImageOptions.Builder().cacheInMemory(false)
                .cacheOnDisc(false).resetViewBeforeLoading(true)
                .showImageForEmptyUri(defaultImage)
                .showImageOnFail(defaultImage)
                .showImageOnLoading(defaultImage).build();
    }

    private void setupImageLoader(){

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                mContext)
                .defaultDisplayImageOptions(options)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(150 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);
        // END - UNIVERSAL IMAGE LOADER SETUP
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        try{
            //ViewHolder object
            final ViewHandler holder;
            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(mResource, parent, false);
                holder= new  ViewHandler();
                holder.nameTextView =  convertView.findViewById(R.id.productNameTextView);
                holder.imageView = convertView.findViewById(R.id.productNeededImageView);
                holder.voteTextView = convertView.findViewById(R.id.productVoteTextView);
                holder.descriptionTextView = convertView.findViewById(R.id.productDescriptionTextView);
                holder.voteButton= convertView.findViewById(R.id.voteButton);
                convertView.setTag(holder);

                holder.voteButton.setTag(position);

                holder.nameTextView.setText(getItem(position).getName());
                holder.voteTextView.setText(getItem(position).getVotes());
                holder.descriptionTextView.setText(getItem(position).getDescription());

                ImageLoader imageLoader = ImageLoader.getInstance();

                //download and display image from url
//                imageLoader.displayImage(getItem(position).getImagePath(), holder.imageView, options);

            }
            else{
                holder = (ViewHandler) convertView.getTag();
                convertView.setTag(holder);
                holder.voteButton.setTag(position);
                holder.nameTextView.setText(getItem(position).getName());
                holder.voteTextView.setText(getItem(position).getVotes());
                holder.descriptionTextView.setText(getItem(position).getDescription());


                Picasso.get().load(getItem(position).getImage()).into(holder.imageView);

                //download and display image from url
//                imageLoader.displayImage(getItem(position).getImagePath(), holder.imageView, options);

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
