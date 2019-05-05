package com.example.Saad.MyFYPProject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.Saad.MyFYPProject.models.UniversalRes;
import com.example.Saad.MyFYPProject.models.checkoutRes.Cart;
import com.example.Saad.MyFYPProject.models.checkoutRes.CheckoutRes;
import com.example.Saad.MyFYPProject.models.items.Item;
import com.example.Saad.MyFYPProject.models.retrofit.RetrofitClient;
import com.example.Saad.MyFYPProject.room.DatabaseClient;
import com.example.Saad.MyFYPProject.utils.Constants;
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
import io.realm.exceptions.RealmMigrationNeededException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyCartActivity extends AppCompatActivity implements View.OnClickListener {

    ListView listView;
    ArrayList<Item> itemsList;
    private Realm realmDB = null;
    Button proceedButton;
    JSONArray JsonArray = null;
    JSONObject JsonData = null;
    //    RealmResults<ItemClass> cartItem = null;
    SharedPreferences preferences;
    String UserID = "";
    String Address = "";
    Integer totalBill = 0;
    MyItemCustomAdapter adapter;

    public static final String TAG = "MyCartActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cart);

//        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//        UserID = preferences.getString("id","");

        itemsList = new ArrayList<>();
        listView = findViewById(R.id.myCartListView);

        itemsList = (ArrayList<Item>) DatabaseClient.getAppDatabase(this).userDao().getAllItems();

        adapter = new MyItemCustomAdapter(this, R.layout.productlayout, itemsList);
        listView.setAdapter(adapter);

//        try{
//
////            Realm.init(this);
////            realmDB = buildDatabase();
////            if (!realmDB.isInTransaction()) {
////                realmDB.beginTransaction();
////            }
        proceedButton = findViewById(R.id.ProceedButton);
        proceedButton.setOnClickListener(this);
////            final RealmResults<ItemClass> AllItems = realmDB.where(ItemClass.class).findAll();
////            if(AllItems.size()<1){
////                proceedButton.setEnabled(false);
////            }
////            for (ItemClass item:AllItems) {
////                itemsList.add(new ItemClass(item.getId(),item.getName(),
////                        item.getPrice(),"",item.getImage(),item.getCategory(),item.getCount()
////                ));
////            }//end of for loop
//
//
//        }catch (Exception ex){
//            Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT).show();
//        }
    }

    public Realm buildDatabase() {

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();

        try {
            return Realm.getInstance(realmConfiguration);
        } catch (RealmMigrationNeededException e) {
            try {
                Realm.deleteRealm(realmConfiguration);
                //Realm file has been deleted.
                return Realm.getInstance(realmConfiguration);
            } catch (Exception ex) {
                Log.e("Exception", ex.toString());
                return null;
                //No Realm file to remove.
            }
        }
    }

    private void showAddressDialog() {

        android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(this);
        dialog.setTitle("Address");
        dialog.setMessage("Please enter Address");

        LayoutInflater inflater = LayoutInflater.from(this);
        View register_layout = inflater.inflate(R.layout.layout_get_address, null);
        final EditText etAddress = register_layout.findViewById(R.id.etAddress);
        final EditText etState = register_layout.findViewById(R.id.etState);
        final EditText etZipCode = register_layout.findViewById(R.id.etZipCode);
        final EditText etCity = register_layout.findViewById(R.id.etCity);
        dialog.setView(register_layout);
        dialog.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (TextUtils.isEmpty(etAddress.getText().toString().trim())) {
                    Toast.makeText(MyCartActivity.this, "Please Enter Address", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(etState.getText().toString().trim())) {
                    Toast.makeText(MyCartActivity.this, "Please Enter State", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(etZipCode.getText().toString().trim())) {
                    Toast.makeText(MyCartActivity.this, "Please Enter ZipCode", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(etCity.getText().toString().trim())) {
                    Toast.makeText(MyCartActivity.this, "Please Enter City", Toast.LENGTH_SHORT).show();
                    return;
                }

                checkout(etAddress.getText().toString()
                        , etZipCode.getText().toString()
                        , etState.getText().toString()
                        , etCity.getText().toString());

            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        dialog.show();
    }

    private void checkout(String address, String zip, String state, String city) {

        CheckoutRes body = new CheckoutRes();

        body.setToken(Constants.token);
        body.setAddress(address);
        body.setCity(city);
        body.setState(state);
        body.setZip_code(zip);

        ArrayList<Cart> cart = new ArrayList<>();
        int totalQuantity = 0;
        float totalPrice = 0;

        for (int i = 0; i < itemsList.size(); i++) {
            Cart cart1 = new Cart();
            cart1.setItem(itemsList.get(i).getName());
            cart1.setPrice(itemsList.get(i).getPrice());
            cart1.setQty(String.valueOf(itemsList.get(i).getCount() + 1));
            totalQuantity += itemsList.get(i).getCount() + 1;
            totalPrice += itemsList.get(i).getCount() * Float.parseFloat(itemsList.get(i).getPrice());
            cart.add(cart1);
        }

        body.setCart(cart);
        body.setTotalPrice(String.valueOf(totalPrice));
        body.setTotalQty(String.valueOf(totalQuantity));
        proceedOrder(body);



    }

    private void proceedOrder(CheckoutRes body) {
        Call<UniversalRes> call = RetrofitClient.getInstance().getApi().proceedOrder(body);

        call.enqueue(new Callback<UniversalRes>() {
            @Override
            public void onResponse(Call<UniversalRes> call, Response<UniversalRes> response) {
                if (response.body().isResult()) {
                    DatabaseClient.getAppDatabase(getApplicationContext()).userDao().deleteAllItems();
                    itemsList.clear();
                    adapter.notifyDataSetChanged();
                    Toast.makeText(MyCartActivity.this, "Order Placed Successfully", Toast.LENGTH_SHORT).show();

                } else {
                    preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("token", "");
                    editor.apply();
                    Intent _intent = new Intent(MyCartActivity.this, LoginActivity.class);
                    finish();
                    Toast.makeText(getApplicationContext(), "Token Expired Please Login Again...", Toast.LENGTH_SHORT).show();
                    startActivity(_intent);
                }

            }

            @Override
            public void onFailure(Call<UniversalRes> call, Throwable t) {
                Log.d(TAG, "onResponse: " + t.getMessage());
            }
        });

    }


    @Override
    public void onClick(View view) {
        if (view == proceedButton) {

            if(itemsList.size()<1){
                Toast.makeText(this, "No items in cart...", Toast.LENGTH_SHORT).show();
                return;
            }
            showAddressDialog();

        }

//            final EditText taskEditText = new EditText(this);
//            taskEditText.setHint("Address");
//
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
//        }
    }

    public void DeleteCartData() {

        realmDB = Realm.getDefaultInstance();
        if (!realmDB.isInTransaction()) {
            realmDB.beginTransaction();
        }
        realmDB.delete(ItemClass.class);
        realmDB.commitTransaction();
        realmDB.close();
        itemsList = new ArrayList<>();
        proceedButton.setEnabled(false);
        MyItemCustomAdapter adapter = new MyItemCustomAdapter(this, R.layout.productlayout, itemsList);
        listView.setAdapter(adapter);
    }

    private void CreateJson() {

//        cartItem = realmDB.where(ItemClass.class).findAll();
//        if (cartItem != null) {
//
//            try {
//                totalBill = 0;
//                JsonArray = new JSONArray();
//                JsonData = new JSONObject();
//                JSONObject jsonObjectForArray;
//                for (ItemClass itemClass : cartItem) {
//                    jsonObjectForArray = new JSONObject();
//                    jsonObjectForArray.put("Id", itemClass.getId());
//                    jsonObjectForArray.put("Quantity", itemClass.getCount());
//                    totalBill += Integer.valueOf(itemClass.getPrice());
//                    JsonArray.put(jsonObjectForArray);
//                }
//                JsonData.put("Cart", JsonArray);
//                Log.e("Data", JsonData.toString());
//                ShowTotalBill(totalBill.toString());
//            } catch (JSONException ex) {
//                Log.e("Json Error", ex.toString());
//            }
//        } else {
//            Toast.makeText(this, "Please add product to cart.", Toast.LENGTH_SHORT).show();
//        }
    }

    public void ShowTotalBill(String totalBill) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("TotalBill")
                .setMessage("Your total bill is " + totalBill)
                .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (CheckInternet()) {
                            PlaceOrderClass placeOrderClass = new PlaceOrderClass(MyCartActivity.this);
                            placeOrderClass.execute((Void) null);
                        } else {
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
    private class PlaceOrderClass extends AsyncTask<Void, Void, String> {

        Context context;
        ProgressDialog progressDialog;

        PlaceOrderClass(Context _con) {
            this.context = _con;
        }

        @Override
        protected String doInBackground(Void... params) {
            String pass_url;
            pass_url = getString(R.string.PlaceOrder);
            try {
                URL url = new URL(pass_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("Cart", "UTF-8") + "=" + URLEncoder.encode(JsonData.toString(), "UTF-8") + "&" +
                        URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(UserID, "UTF-8") + "&" +
                        URLEncoder.encode("Address", "UTF-8") + "=" + URLEncoder.encode(Address, "UTF-8") + "&" +
                        URLEncoder.encode("TotalBill", "UTF-8") + "=" + URLEncoder.encode(totalBill.toString(), "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String line;
                StringBuilder result = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line);
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
            } else {
                try {
                    Gson GsonObject = new Gson();
                    SimpleJSONReturn simpleJSONReturn = GsonObject.fromJson(result, SimpleJSONReturn.class);

                    if (simpleJSONReturn.getTitle().equals("3") || simpleJSONReturn.getTitle().equals("2")) {
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
                    } else {
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
                } catch (Exception ex) {
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
