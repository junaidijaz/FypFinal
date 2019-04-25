package com.example.Saad.MyFYPProject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
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
import java.util.concurrent.ExecutionException;

public class ProductDetailsActivity extends AppCompatActivity {

    TextView _nameTextView,_priceTextView,_descriptionTextView;
    String productId;
    ArrayList<String> feedbackList;
    ListView listView;
    Button submitButton;
    EditText _feedBackDescription;
    String feedDescription;
    ArrayAdapter<String> arrayAdapter = null;
    String userId="",userName ="";
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            setContentView(R.layout.activity_product_details);

            preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            userId = preferences.getString("id","");
            userName = preferences.getString("name","");
            listView = findViewById(R.id.feedBackListView);
            _nameTextView = findViewById(R.id.productNameTextView);
            _priceTextView = findViewById(R.id.productPriceTextView);
            _descriptionTextView = findViewById(R.id.productDescriptionTextView);
            submitButton = findViewById(R.id.feedBackButton);
            _feedBackDescription = findViewById(R.id.feedBackDescription);
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    feedDescription =_feedBackDescription.getText().toString();
                    if(feedDescription.equals("")){
                        _feedBackDescription.setError("Description is required");

                    }else{
                        _feedBackDescription.setError(null);
                        SubmitFeedBack submitFeedBack = new SubmitFeedBack();
                        try {
                            String result = submitFeedBack.execute((Void) null).get();
                            if(result == null){
                                Toast.makeText(ProductDetailsActivity.this, "Can't connect with server", Toast.LENGTH_SHORT).show();
                            }else if(result.equals("0")){
                                Toast.makeText(ProductDetailsActivity.this, "Parameter is missing", Toast.LENGTH_SHORT).show();
                            }else if(result.equals("2")){
                                Toast.makeText(ProductDetailsActivity.this, "Sorry! Can't submit your feedback.", Toast.LENGTH_SHORT).show();
                            } else if(result.equals("1")){
                                if(arrayAdapter == null) {
                                    feedbackList = new ArrayList<>();
                                    feedbackList.add(userName+"\n"+feedDescription);
                                    arrayAdapter = new ArrayAdapter<>(ProductDetailsActivity.this,android.R.layout.simple_list_item_1, feedbackList);
                                    listView.setAdapter(arrayAdapter);
                                }else{
                                    feedbackList.add(userName+"\n"+feedDescription);
                                    arrayAdapter.notifyDataSetChanged();
                                }
                                _feedBackDescription.setText(null);
                                feedDescription = "";
                                Toast.makeText(ProductDetailsActivity.this, "Your feed back is submit", Toast.LENGTH_SHORT).show();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });



            productId = getIntent().getStringExtra("id");

            _nameTextView.setText(getIntent().getStringExtra("name"));
            _priceTextView.setText(getIntent().getStringExtra("price"));
            _descriptionTextView.setText(getIntent().getStringExtra("description"));

            if(CheckInternet()){
                GetProductFeedBack getProductFeedBack = new GetProductFeedBack();
              String result =  getProductFeedBack.execute((Void) null).get();
              if(result == null){
                  Toast.makeText(this, "Can't connect with server.", Toast.LENGTH_SHORT).show();
              }else{
                  switch (result) {
                      case "0":
                          Toast.makeText(this, "Parameter is missing", Toast.LENGTH_SHORT).show();
                          break;
                      case "1":
                          Toast.makeText(this, "No feedback on this product yet.", Toast.LENGTH_SHORT).show();
                          break;

                      default:
                          try{
                              feedbackList = new ArrayList<>();
                              JSONObject jsonObject = new JSONObject(result);
                              JSONArray jsonArray = jsonObject.getJSONArray("Feedback");
                              JSONObject jsonObject1;
                              for (int i =0; i<jsonArray.length();i++){
                                  jsonObject1=(JSONObject) jsonArray.get(i);
                                  feedbackList.add(jsonObject1.optString("name")+"\n"+jsonObject1.optString("comment"));

                              }
                               arrayAdapter =
                                      new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, feedbackList);
                              listView.setAdapter(arrayAdapter);

                          }
                          catch (Exception ex){
                              Log.e("Exception",ex.toString());
                              Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
                              break;
                          }
                  }
              }

            }else{
                Toast.makeText(this, "No internet connection to load feedback of product.", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception ex){
            Log.e("Exception",ex.toString());
            Toast.makeText(this, "Error while creating activity", Toast.LENGTH_SHORT).show();
        }
        
        

    }


    public boolean CheckInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }//end of check internet
    @SuppressLint("StaticFieldLeak")
    private class SubmitFeedBack extends AsyncTask<Void,Void,String> {
        String token_url;
        @Override
        protected void onPreExecute() {
            token_url = getString(R.string.SubmitFeedBack);
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

                String post_data = URLEncoder.encode("ProductId", "UTF-8") + "=" + URLEncoder.encode(productId, "UTF-8")+"&"+
                        URLEncoder.encode("Description", "UTF-8") + "=" + URLEncoder.encode(feedDescription, "UTF-8") +"&"+
                        URLEncoder.encode("UserId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");

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
    }

    @Override
    public void onResume() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onResume();
    }

    @Override
    public void onStart() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onStart();
    }
    @SuppressLint("StaticFieldLeak")
    private class GetProductFeedBack extends AsyncTask<Void,Void,String> {
        String token_url;
        @Override
        protected void onPreExecute() {
            token_url = getString(R.string.GetProductFeedBack);
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

                String post_data = URLEncoder.encode("ProductId", "UTF-8") + "=" + URLEncoder.encode(productId, "UTF-8");

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
    }


}
