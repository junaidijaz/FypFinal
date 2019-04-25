package com.example.Saad.MyFYPProject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

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

public class ShowProductOfOrder extends AppCompatActivity {

    String OrderId;
    ArrayList<ProductClass> productList = null;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_product_of_order);
        OrderId = getIntent().getStringExtra("Id");
        listView = findViewById(R.id.ProductListView);

        if(CheckInternet()) {
             GetOrderProduct getOrderProduct = new GetOrderProduct();
             getOrderProduct.execute((Void) null);
        }else{
            Toast.makeText(this, "No Internet connection.", Toast.LENGTH_SHORT).show();
        }

    }
    @SuppressLint("StaticFieldLeak")
    private class GetOrderProduct extends  AsyncTask<Void,Void,String> {
        String token_url;
        @Override
        protected void onPreExecute() {
            token_url = getString(R.string.GetOrderProducts);
        }

        @Override
        protected String doInBackground(Void... params) {

            try {
                URL url = new URL(token_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("OrderID", "UTF-8") + "=" + URLEncoder.encode(OrderId, "UTF-8");
                Log.e("Url",token_url+post_data);
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                StringBuilder result=new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line);
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return result.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                // makeAndShowDialogBox("Unable to Connect to the internet").show();
            } catch (IOException e) {
                e.printStackTrace();

            }


            return null;
        }
        @Override
        protected void onPostExecute(String result){
            if(result == null){
                Toast.makeText(ShowProductOfOrder.this, "Can't Connect with server", Toast.LENGTH_SHORT).show();
            }else if(result.equals("0")){
                Toast.makeText(ShowProductOfOrder.this, "Parameter is missing", Toast.LENGTH_SHORT).show();
            }else if(result.equals("0")){
                Toast.makeText(ShowProductOfOrder.this, "Sorry no products found", Toast.LENGTH_SHORT).show();
            }else{
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("OrderDetails");
                    JSONObject jsonObject1;
                    productList = new ArrayList<>();

                    for(int i= 0; i<jsonArray.length();i++){
                        jsonObject1 =  jsonArray.getJSONObject(i);
                        productList.add(new ProductClass(jsonObject1.getString("name"),Integer.valueOf(jsonObject1.getString("price")),
                                Integer.valueOf(jsonObject1.getString("Quantity"))));
                    }
                    ProductCustomAdaptor  productCustomAdaptor = new ProductCustomAdaptor(ShowProductOfOrder.this,R.layout.order_product_layout,productList);
                    listView.setAdapter(productCustomAdaptor);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Json Object",result);
                    Toast.makeText(ShowProductOfOrder.this, "Error in json", Toast.LENGTH_SHORT).show();
                }
            }
        }


    }


    public boolean CheckInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }//end of check internet



}
