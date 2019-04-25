package com.example.Saad.MyFYPProject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.Saad.MyFYPProject.models.items.Item;
import com.example.Saad.MyFYPProject.room.DatabaseClient;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;

public class MyCartActivity extends AppCompatActivity implements View.OnClickListener {

    ListView listView;
    ArrayList<Item> ItemsList;
    private Realm realmDB=null;
    Button proceedButton;
    JSONArray JsonArray = null;
    JSONObject JsonData = null;
    RealmResults<ItemClass> cartItem = null;
    SharedPreferences preferences;
    String UserID="";
    String Address ="";
    Integer totalBill=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cart);

//        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//        UserID = preferences.getString("id","");

        ItemsList = new ArrayList<>();
        listView =  findViewById(R.id.myCartListView);

       ItemsList = (ArrayList<Item>)  DatabaseClient.getAppDatabase(this).userDao().getAllItems();

        MyItemCustomAdapter adapter = new MyItemCustomAdapter(this, R.layout.productlayout, ItemsList);
        listView.setAdapter(adapter);

//        try{
//
////            Realm.init(this);
////            realmDB = buildDatabase();
////            if (!realmDB.isInTransaction()) {
////                realmDB.beginTransaction();
////            }
////            proceedButton = findViewById(R.id.ProceedButton);
////            proceedButton.setOnClickListener(this);
////            final RealmResults<ItemClass> AllItems = realmDB.where(ItemClass.class).findAll();
////            if(AllItems.size()<1){
////                proceedButton.setEnabled(false);
////            }
////            for (ItemClass item:AllItems) {
////                ItemsList.add(new ItemClass(item.getId(),item.getName(),
////                        item.getPrice(),"",item.getImage(),item.getCategory(),item.getCount()
////                ));
////            }//end of for loop
//
//
//        }catch (Exception ex){
//            Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT).show();
//        }
    }

    public Realm buildDatabase(){

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();

        try {
            return Realm.getInstance(realmConfiguration);
        } catch (RealmMigrationNeededException e){
            try {
                Realm.deleteRealm(realmConfiguration);
                //Realm file has been deleted.
                return Realm.getInstance(realmConfiguration);
            } catch (Exception ex){
                Log.e("Exception",ex.toString());
                return null;
                //No Realm file to remove.
            }
        }
    }
    @Override
    public void onClick(View view) {
        if(view == proceedButton) {

//            final EditText taskEditText = new EditText(this);
//            AlertDialog dialog = new AlertDialog.Builder(this)
//                    .setTitle("Address")
//                    .setMessage("Enter Address")
//                    .setView(taskEditText)
//                    .setPositiveButton("Place Order", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Address = String.valueOf(taskEditText.getText());
//                            if (Address.equals("")) {
//                                Toast.makeText(MyCartActivity.this, "Please Enter address to place order.", Toast.LENGTH_SHORT).show();
//                            } else {
//                                dialog.dismiss();
//                                CreateJson();
//                            }
//                        }
//                    })
//                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    })
//                    .create();
//            dialog.show();
        }
    }

    public void DeleteCartData(){

            realmDB = Realm.getDefaultInstance();
            if (!realmDB.isInTransaction()) {
                realmDB.beginTransaction();
            }
            realmDB.delete(ItemClass.class);
            realmDB.commitTransaction();
            realmDB.close();
            ItemsList = new ArrayList<>();
            proceedButton.setEnabled(false);
            MyItemCustomAdapter adapter = new MyItemCustomAdapter(this, R.layout.productlayout, ItemsList);
            listView.setAdapter(adapter);
    }

    private void CreateJson() {

            cartItem = realmDB.where(ItemClass.class).findAll();
            if(cartItem!=null) {

                try {
                    totalBill=0;
                    JsonArray = new JSONArray();
                    JsonData = new JSONObject();
                    JSONObject jsonObjectForArray;
                    for (ItemClass itemClass : cartItem) {
                        jsonObjectForArray = new JSONObject();
                        jsonObjectForArray.put("Id",itemClass.getId());
                        jsonObjectForArray.put("Quantity",itemClass.getCount());
                        totalBill+=Integer.valueOf(itemClass.getPrice());
                        JsonArray.put(jsonObjectForArray);
                    }
                    JsonData.put("Cart", JsonArray);
                    Log.e("Data",JsonData.toString());
                    ShowTotalBill(totalBill.toString());
                } catch (JSONException ex) {
                    Log.e("Json Error", ex.toString());
                }
            }else{
                Toast.makeText(this, "Please add product to cart.", Toast.LENGTH_SHORT).show();
            }
    }

    public void ShowTotalBill(String totalBill){
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("TotalBill")
                .setMessage("Your total bill is "+totalBill)
                .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        if(CheckInternet()) {
                            PlaceOrderClass placeOrderClass = new PlaceOrderClass(MyCartActivity.this);
                            placeOrderClass.execute((Void) null);
                        }
                        else{
                            Toast.makeText(MyCartActivity.this, "Please Turn On Internet.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    public boolean CheckInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }//end of check internet

    @SuppressLint("StaticFieldLeak")
    private class PlaceOrderClass extends AsyncTask<Void,Void,String> {

        Context context;
        ProgressDialog progressDialog;

        PlaceOrderClass (Context _con) {
            this.context = _con;
        }

        @Override
        protected String doInBackground(Void... params) {
            String pass_url;
            pass_url =getString(R.string.PlaceOrder);
            try {
                URL url = new URL(pass_url);
                HttpURLConnection httpURLConnection =(HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("Cart", "UTF-8") + "=" + URLEncoder.encode(JsonData.toString(), "UTF-8")+"&"+
                        URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(UserID, "UTF-8")+"&"+
                        URLEncoder.encode("Address", "UTF-8") + "=" + URLEncoder.encode(Address, "UTF-8")+"&"+
                        URLEncoder.encode("TotalBill", "UTF-8") + "=" + URLEncoder.encode(totalBill.toString(), "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String line;
                StringBuilder result=new StringBuilder();
                while((line=bufferedReader.readLine())!=null)
                {
                    result.append( line);
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Placing Order");
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please wait.........");
            progressDialog.show();

            progressDialog.isIndeterminate();
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();

            final AlertDialog alertDialog = new AlertDialog.Builder(context).create();

            if (result == null) {
                alertDialog.setTitle("Error Message");
                alertDialog.setMessage("Can't Connect With Server");
                alertDialog.show();
            } else{
                try{
                    Gson GsonObject=new Gson();
                    SimpleJSONReturn simpleJSONReturn=GsonObject.fromJson(result,SimpleJSONReturn.class);

                    if(simpleJSONReturn.getTitle().equals("3")||simpleJSONReturn.getTitle().equals("2")){
                        alertDialog.setTitle("Success");
                        alertDialog.setMessage(simpleJSONReturn.getMessage());
                        alertDialog.setCancelable(false);
                        alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                alertDialog.dismiss();
                                DeleteCartData();
                            }
                        });
                        alertDialog.show();
                    }else{
                        alertDialog.setTitle("Error");
                        alertDialog.setMessage(simpleJSONReturn.getMessage());
                        alertDialog.setCancelable(false);
                        alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                alertDialog.dismiss();
                            }
                        });
                        alertDialog.show();
                    }
                }catch (Exception ex){
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage(result);
                    alertDialog.show();
                }
            }
        }//end of on Post Execute Function

        @Override
        protected void onCancelled() {
            progressDialog.dismiss();
        }// end of onCancelled Function

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

    }//end of Async Class


}
