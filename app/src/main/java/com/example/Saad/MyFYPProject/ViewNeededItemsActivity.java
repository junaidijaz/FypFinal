package com.example.Saad.MyFYPProject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.Saad.MyFYPProject.models.UniversalRes;
import com.example.Saad.MyFYPProject.models.items.Item;
import com.example.Saad.MyFYPProject.models.retrofit.RetrofitClient;
import com.example.Saad.MyFYPProject.neededitems.NeededItemsRes;
import com.example.Saad.MyFYPProject.utils.Constants;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewNeededItemsActivity extends AppCompatActivity {
    AsyncDataClass _asyncClass;
    ListView listView;
    ProgressDialog progressDialog;
    ArrayList<Item> ItemsList;
    ItemNeededCustomAdapter adapter = null;
    SharedPreferences preferences;
    private String userID = "";
    String ProductId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_need_items);

        listView = findViewById(R.id.viewNeededItemListView);

//        preferences= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//        userID = preferences.getString("id","");
//        if(CheckInternet()){
//            if(_asyncClass == null){
//                _asyncClass = new AsyncDataClass(this);
//                _asyncClass.execute((Void) null);
//            } else{
//                Toast.makeText(this, "Please Wait.......", Toast.LENGTH_SHORT).show();
//            }
//        } else{
//            Toast.makeText(this, "No Internet Connected", Toast.LENGTH_SHORT).show();
//        }
        
        fetchNeededItems();
    }

    private void fetchNeededItems() {
        Call<NeededItemsRes> call = RetrofitClient.getInstance().getApi().getNeededItems(Constants.token);
        call.enqueue(new Callback<NeededItemsRes>() {
            @Override
            public void onResponse(Call<NeededItemsRes> call, Response<NeededItemsRes> response) {
                if(response.body().isResult())
                {
                    ItemsList = response.body().getItems();
                    adapter = new ItemNeededCustomAdapter(ViewNeededItemsActivity.this, R.layout.view_need_item_layout, ItemsList);
                    listView.setAdapter(adapter);
                }else {
                    new MainActivity().logoutUser();
                }
            }

            @Override
            public void onFailure(Call<NeededItemsRes> call, Throwable t) {
                Toast.makeText(ViewNeededItemsActivity.this, "SomeThing went wrong...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void VoteButtonClick(View view) {
        Integer position = (Integer) view.getTag();

        voteItem(ItemsList.get(position).getId(), position);

//        if (CheckInternet()) {
//
////            ProductId = ItemsList.get(position).getId();
//            VoteProductClass voteProductClass = new VoteProductClass(this);
//            voteProductClass.execute((Void) null);
//        } else {
//            Toast.makeText(this, "No internet connection. Please turn on your internet connection.", Toast.LENGTH_SHORT).show();
//        }
    }

    private void voteItem(String id , final int pos) {

        progressDialog = new ProgressDialog(ViewNeededItemsActivity.this);
        progressDialog.setTitle("Loading Data");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait.........");
        progressDialog.show();
        Log.d("TAG", "voteItem: " + id +" " + pos);

        Call<UniversalRes> call = RetrofitClient.getInstance().getApi().voteNeededItems(Constants.token,id);
        call.enqueue(new Callback<UniversalRes>() {
            @Override
            public void onResponse(Call<UniversalRes> call, Response<UniversalRes> response) {
                if (response.body().isResult())
                {
                    Toast.makeText(ViewNeededItemsActivity.this, "Your vote has been submitted.", Toast.LENGTH_SHORT).show();
                    ItemsList.get(pos).addVotes();
                    adapter.notifyDataSetChanged();
                }else {
                    new MainActivity().logoutUser();
                }
                progressDialog.hide();
            }

            @Override
            public void onFailure(Call<UniversalRes> call, Throwable t) {
                progressDialog.hide();
                Toast.makeText(ViewNeededItemsActivity.this, "Check your internet connection or you can't vote right now...", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @SuppressLint("StaticFieldLeak")
    private class AsyncDataClass extends AsyncTask<Void, Void, String> {
        String imagePath = getString(R.string.ImagePathUrl);
        Context context;
        String result = "";
        ProgressDialog progressDialog;

        AsyncDataClass(Context _con) {
            this.context = _con;
        }


        @Override
        protected String doInBackground(Void... params) {
            String pass_url;
            pass_url = getString(R.string.GetNeedItems);
            try {
                URL url = new URL(pass_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                    Log.e("Result", result);
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;
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
            progressDialog.setTitle("Loading Data");
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please wait.........");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String result) {

            _asyncClass = null;

            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            progressDialog.dismiss();

            if (result == null) {
                alertDialog.setTitle("Error Message");
                alertDialog.setMessage("Can't Connect With Server");
                alertDialog.show();
            } else if (result.equals("0")) {
                alertDialog.setTitle("Error Message");
                alertDialog.setMessage("No Items added yet.");
                alertDialog.show();
            } else if (!result.equals("")) {

                ItemsList = new ArrayList<>();
                try {
                    JSONObject jsonobject = new JSONObject(result);
                    JSONArray JO = jsonobject.getJSONArray("Items");
                    JSONObject object;

                    for (int i = 0; i < JO.length(); i++) {
                        object = (JSONObject) JO.get(i);

//                        ItemsList.add(new ItemNeedClass(object.optString("id"), object.optString("name"),
//                                object.optString("votes"), object.optString("description"), imagePath + object.optString("image")
//                        ));
                    }//end of for loop

                    adapter = new ItemNeededCustomAdapter(context, R.layout.view_need_item_layout, ItemsList);
                    listView.setAdapter(adapter);
                } catch (Exception e) {
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage(result);
                    alertDialog.show();
                } //end of catch
            } //end of else if
            else {
                alertDialog.setTitle("Error");
                alertDialog.setMessage(result);
                alertDialog.show();
            }

        }//end of on Post Execute Function

        @Override
        protected void onCancelled() {
            _asyncClass = null;
            progressDialog.dismiss();
        }// end of onCancelled Function


        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }//end of AsyncClass

    @SuppressLint("StaticFieldLeak")
    private class VoteProductClass extends AsyncTask<Void, Void, String> {
        Context context;
        ProgressDialog progressDialog;

        VoteProductClass(Context _con) {
            this.context = _con;
        }


        @Override
        protected String doInBackground(Void... params) {
            String pass_url;
            pass_url = getString(R.string.VoteProduct);
            try {
                URL url = new URL(pass_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userID, "UTF-8") + "&"
                        + URLEncoder.encode("productId", "UTF-8") + "=" + URLEncoder.encode(ProductId, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));


                StringBuilder result = new StringBuilder();
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
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Voting");
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please wait.........");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String result) {

            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            progressDialog.dismiss();

            if (result == null) {
                alertDialog.setTitle("Error Message");
                alertDialog.setMessage("Can't Connect With Server");
                alertDialog.show();
            } else if (result.equals("0")) {
                alertDialog.setTitle("Error Message");
                alertDialog.setMessage("Parameter is missing.");
                alertDialog.show();
            } else if (result.equals("1")) {
                alertDialog.setTitle("Error Message");
                alertDialog.setMessage("You can't vote now.");
                alertDialog.show();
            } else if (result.equals("2")) {
                alertDialog.setTitle("Message");
                alertDialog.setMessage("Can't vote due to query execution.");
                alertDialog.show();
            } else if (result.equals("3")) {
                alertDialog.setTitle("Message");
                alertDialog.setMessage("Can't add vote of this product.");
                alertDialog.show();
            } else if (result.equals("4")) {
                alertDialog.setTitle("Message");
                alertDialog.setMessage("Your vote has been submitted.");
                alertDialog.show();
            } //end of else if
            else {
                alertDialog.setTitle("Error");
                alertDialog.setMessage(result);
                alertDialog.show();
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
    }//end of AsyncClass

    public boolean CheckInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }//end of check internet


}
