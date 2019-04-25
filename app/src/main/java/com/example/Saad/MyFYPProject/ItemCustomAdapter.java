package com.example.Saad.MyFYPProject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Saad on 2/11/2018.
 */

public class ItemCustomAdapter extends ArrayAdapter<Item> {

    private Context mContext;

    private int mResource;

    private static class ViewHandler{
        TextView nameTextView;
        //this will contain schedeule Id but user will not see
        TextView priceTTextView;
        ImageView imageView;
        TextView quantityTextView;
        TextView plusTextView;
        TextView minusTextView;

        TextView categoryTextView;
    }
    ItemCustomAdapter(Context context, int resource,@NonNull ArrayList<Item> object) {
        super(context,resource,object);
        mContext = context;
        mResource = resource;
        setupImageLoader();
    }

    private void setupImageLoader(){
        // UNIVERSAL IMAGE LOADER SETUP
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                mContext)
                .defaultDisplayImageOptions(defaultOptions)
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
//            int itemCount= getItem(position).getCount();
            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(mResource, parent, false);
                holder= new ViewHandler();
                holder.nameTextView =  convertView.findViewById(R.id.itemName);
                holder.priceTTextView= convertView.findViewById(R.id.itemPrice);
                holder.imageView = convertView.findViewById(R.id.itemPicture);
                holder.quantityTextView = convertView.findViewById(R.id.countTextView);
                holder.plusTextView = convertView.findViewById(R.id.plus);
                holder.minusTextView = convertView.findViewById(R.id.minus);
                holder.categoryTextView = convertView.findViewById(R.id.categoryTextView);

//                holder.quantityTextView.setText(String.valueOf(itemCount));
                //Toast.makeText(mContext,"Toast"+ String.valueOf(itemCount), Toast.LENGTH_SHORT).show();
                holder.nameTextView.setText(getItem(position).getName());
                holder.priceTTextView.setText(getItem(position).getPrice());

                holder.plusTextView.setTag(position);
                holder.minusTextView.setTag(position);
                holder.categoryTextView.setText(getItem(position).getCategory());

                convertView.setTag(holder);

                ImageLoader imageLoader = ImageLoader.getInstance();

                int defaultImage = mContext.getResources().getIdentifier("@drawable/image_failed",null,mContext.getPackageName());

                //create display options
                DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                        .cacheOnDisc(true).resetViewBeforeLoading(true)
                        .showImageForEmptyUri(defaultImage)
                        .showImageOnFail(defaultImage)
                        .showImageOnLoading(defaultImage).build();

                //download and display image from url
                imageLoader.displayImage(getItem(position).getImage(), holder.imageView, options);

            }
            else{
                holder = ( ViewHandler) convertView.getTag();
//                holder.quantityTextView.setText(String.valueOf(itemCount));
                //Toast.makeText(mContext,"Toast"+ String.valueOf(itemCount), Toast.LENGTH_SHORT).show();
                holder.nameTextView.setText(getItem(position).getName());
                holder.priceTTextView.setText(getItem(position).getPrice());

                holder.categoryTextView.setText(getItem(position).getCategory());
                holder.plusTextView.setTag(position);
                holder.minusTextView.setTag(position);

                convertView.setTag(holder);

                ImageLoader imageLoader = ImageLoader.getInstance();

                int defaultImage = mContext.getResources().getIdentifier("@drawable/image_failed",null,mContext.getPackageName());

                //create display options
                DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                        .cacheOnDisc(true).resetViewBeforeLoading(true)
                        .showImageForEmptyUri(defaultImage)
                        .showImageOnFail(defaultImage)
                        .showImageOnLoading(defaultImage).build();

                //download and display image from url
                imageLoader.displayImage(getItem(position).getImage(), holder.imageView, options);

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
