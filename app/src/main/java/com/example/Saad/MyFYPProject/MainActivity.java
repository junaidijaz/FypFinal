package com.example.Saad.MyFYPProject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Saad.MyFYPProject.adapter.ItemsAdapter;
import com.example.Saad.MyFYPProject.adapter.OnItemClickListener;
import com.example.Saad.MyFYPProject.models.items.Item;
import com.example.Saad.MyFYPProject.models.items.ItemsRes;
import com.example.Saad.MyFYPProject.models.retrofit.RetrofitClient;
import com.example.Saad.MyFYPProject.models.user.User;
import com.example.Saad.MyFYPProject.models.user.UserRes;
import com.example.Saad.MyFYPProject.room.DatabaseClient;

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

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmMigrationNeededException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.Saad.MyFYPProject.utils.Constants.token;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    //    AsyncDataClass _asyncClass;
    RecyclerView recyclerView;
    //    ArrayList<ItemClass> ItemsList
    ArrayList<Item> ItemsList;
    ItemsAdapter adapter = null;

    String userId = "";
    SharedPreferences preferences;
    //    private Realm realmDB = null;
    User user;
    private View view;
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.rvAllItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

//        /*View view=navigationView.inflateHeaderView(R.layout.nav_header_main);*/
//        TextView nameTextView = header.findViewById(R.id.headerNameTextView);
//        TextView emailTextView = header.findViewById(R.id.headerEmailTextView);
//
//        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//        userId = preferences.getString("id", "");
//        nameTextView.setText(preferences.getString("name", ""));
//        emailTextView.setText(preferences.getString("email", ""));
//        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//        token = Constants.token;
//        if (CheckInternet()) {
//            if (token != null && !token.equals("") && userId != null && !userId.equals("")) {
//                new UpdateToken().execute();
//            } else {
//                Toast.makeText(this, "Token or userID is null", Toast.LENGTH_SHORT).show();
//            }
//        }


        if (token.equalsIgnoreCase("")) {
            preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            token = preferences.getString("token", "");

            if (token.equalsIgnoreCase("")) {
                Toast.makeText(this, "Please Login Again....", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        getUserInfo(header);
        getAllItems();

//        recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Intent intent = new Intent(MainActivity.this, ProductDetailsActivity.class);
//
//                intent.putExtra("id", String.valueOf(ItemsList.get(i).getId()));
//
//                intent.putExtra("name", ItemsList.get(i).getName());
//
//                intent.putExtra("price", ItemsList.get(i).getPrice());
//
//                intent.putExtra("description", ItemsList.get(i).getDescription());
//                startActivity(intent);
//            }
//        });


//        if (realmDB == null) {
//            Realm.init(this);
//            realmDB = buildDatabase();
//        }
//        if (CheckInternet()) {
//            if (_asyncClass == null) {
//                _asyncClass = new AsyncDataClass(this);
//                _asyncClass.execute((Void) null);
//            } else {
//                Toast.makeText(this, "Please Wait.......", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Toast.makeText(this, "No Internet Connected", Toast.LENGTH_SHORT).show();
//        }


    }

    private void buildAllItemsRecyclerView() {
        adapter = new ItemsAdapter(this, ItemsList);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, @Nullable View v, View Count) {
                if (v.getId() == R.id.btnAddToCart) {
                    Toast.makeText(MainActivity.this, "Item Added to Cart", Toast.LENGTH_SHORT).show();

                    Item temp = DatabaseClient.getAppDatabase(getApplicationContext()).userDao().getItembyId(ItemsList.get(position).getId());
                    if(temp == null)
                    {
                        DatabaseClient.getAppDatabase(getApplicationContext()).userDao().insertItem(ItemsList.get(position));
                    }else {
                        DatabaseClient.getAppDatabase(getApplicationContext()).userDao().updateItem(ItemsList.get(position));
                    }

                } else if (v.getId() == R.id.plus) {
                    TextView textView = (TextView) Count;
                    ItemsList.get(position).AddQuantity();
                    textView.setText((Integer.parseInt(textView.getText().toString()) + 1) + "");
                } else {
                    TextView textView = (TextView) Count;

                    int c = Integer.parseInt(textView.getText().toString());
                    if(c<=0)
                     return;
                    ItemsList.get(position).MinusQuantity();
                    c--;
                    textView.setText(c+"");
                }
            }
        });
    }

    private void getUserInfo(final View header) {
        Call<UserRes> call = RetrofitClient.getInstance().getApi().getUserDetail(token);

        call.enqueue(new Callback<UserRes>() {
            @Override
            public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                if (response.body().getResult()) {
                    user = response.body().getUser();

                    TextView nameTextView = header.findViewById(R.id.headerNameTextView);
                    TextView emailTextView = header.findViewById(R.id.headerEmailTextView);
                    nameTextView.setText(user.getName());
                    emailTextView.setText(user.getEmail());

                } else {
                    Toast.makeText(MainActivity.this, "Token Expired Please Login Again...", Toast.LENGTH_SHORT).show();
                    logoutUser();
                }
            }

            @Override
            public void onFailure(Call<UserRes> call, Throwable t) {

            }
        });
    }

    private void getAllItems() {
        Call<ItemsRes> call = RetrofitClient.getInstance().getApi().getAllItems(token);
        call.enqueue(new Callback<ItemsRes>() {
            @Override
            public void onResponse(Call<ItemsRes> call, Response<ItemsRes> response) {
                if (response.body().isResult()) {
                    ItemsList = response.body().getItems();
                    buildAllItemsRecyclerView();
                }else {
                    logoutUser();
                }
            }

            @Override
            public void onFailure(Call<ItemsRes> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
            }
        });

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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.Orders) {
            Log.d(TAG, "onNavigationItemSelected: ");
            startActivity( new Intent(MainActivity.this, OrderActivity.class));

//            if (CheckInternet()) {
//
//                GetCompletedOrderOfUser getCompletedOrderOfUser = new GetCompletedOrderOfUser();
//                try {
//                    String result = getCompletedOrderOfUser.execute().get();
//                    if (result == null) {
//                        Toast.makeText(this, "Can't Connect with Server.", Toast.LENGTH_SHORT).show();
//                    } else {
//
//                        if (result.equals("0")) {
//                            Toast.makeText(this, "Parameter is missing.", Toast.LENGTH_SHORT).show();
//                        } else if (result.equals("1")) {
//                            Toast.makeText(this, "Place Order before see your orders.", Toast.LENGTH_SHORT).show();
//                        } else if (!result.equals("")) {
//                            Intent intent = new Intent(this, OrderActivity.class);
//                            intent.putExtra("order", result);
//                            Log.e("CompletedOrder", result);
//                            startActivity(intent);
//                        } else {
//                            Toast.makeText(this, "Place Order before see your orders." + result, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                Toast.makeText(this, "No Internet Connection.Please check your internet connection.", Toast.LENGTH_SHORT).show();
//            }

        } else if (id == R.id.nav_view_needed_item) {
            if (CheckInternet()) {
                Intent intent = new Intent(this, ViewNeededItemsActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "No internet connection.Please turn on internet.", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_add_needed_item) {
            if (CheckInternet()) {
                Intent intent = new Intent(this, AddNeededItemActivity.class);
                startActivity(intent);
//                CheckUserRequest checkUserRequest = new CheckUserRequest(this);
//                checkUserRequest.execute((Void) null);
            } else {
                Toast.makeText(this, "No internet connection.Please turn on internet.", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_my_cart) {
            Intent intent = new Intent(this, MyCartActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_log_out) {
            logoutUser();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void logoutUser() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", "");
        editor.apply();
        Intent _intent = new Intent(this, LoginActivity.class);
        finish();
        startActivity(_intent);
    }

    @SuppressLint("StaticFieldLeak")
    private class UpdateToken extends AsyncTask<Void, Void, String> {
        String token_url;

        @Override
        protected void onPreExecute() {

            token_url = getString(R.string.UpdateToken);
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

                String post_data = URLEncoder.encode("token", "UTF-8") + "=" + URLEncoder.encode(token, "UTF-8") + "&"
                        + URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");
                Log.e("Token", post_data);
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
                // makeAndShowDialogBox("Unable to Connect to the internet").show();
            } catch (IOException e) {
                e.printStackTrace();

            }


            return null;
        }
    }

    public boolean CheckInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }//end of check internet


    @SuppressLint("StaticFieldLeak")
    private class GetCompletedOrderOfUser extends AsyncTask<Void, Void, String> {
        String token_url;

        @Override
        protected void onPreExecute() {
            token_url = getString(R.string.GetOrders);
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

                String post_data = URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");
                Log.e("Url", token_url + post_data);
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
                // makeAndShowDialogBox("Unable to Connect to the internet").show();
            } catch (IOException e) {
                e.printStackTrace();

            }


            return null;
        }
    }


    public void PlusCount(View view) {
        Log.d("", "PlusCount: ");
        Integer position = (Integer) view.getTag();
        ItemsList.get(position).AddQuantity();

//        try {
//            if (!realmDB.isInTransaction()) {
//                realmDB.beginTransaction();
//            }
//            final RealmResults<ItemClass> allItems = realmDB.where(ItemClass.class).findAll();
//
//            ItemClass userSchedule = allItems.where().equalTo("id", ItemsList.get(position).getId()).findFirst();
//
//            if (userSchedule != null) {
//                userSchedule.setCount(ItemsList.get(position).getCount());
//                //  Log.e("Count1",userSchedule.getId() + " "+String.valueOf(userSchedule.getCount()));
////                realmDB.commitTransaction();
//            } else {
//////                ItemClass us = realmDB.createObject(.class);
//////                us.setId(ItemsList.get(position).getId());
//////                us.setImage(ItemsList.get(position).getImage());
//////                us.setName(ItemsList.get(position).getName());
//////                us.setCount(ItemsList.get(position).getCount());
//////                us.setPrice(ItemsList.get(position).getPrice());
//////                realmDB.commitTransaction();
//            }
//            adapter.notifyDataSetChanged();
//        } catch (Exception ex) {
//            Log.d("RError", ex.toString());
//            Toast.makeText(this, "Error in realm", Toast.LENGTH_SHORT).show();
//        }

    }

    public void MinusCount(View view) {

        Integer position = (Integer) view.getTag();
        ItemsList.get(position).MinusQuantity();

//        if (ItemsList.get(position).getCount() != 0) {
//            try {
//
//                final RealmResults<ItemClass> allScheduele = realmDB.where(ItemClass.class).findAll();
//
//                ItemClass userSchedule = allScheduele.where().equalTo("id", ItemsList.get(position).getId()).findFirst();
//
//                if (userSchedule != null) {
//                    ItemsList.get(position).MinusQuantity();
//
//                    if (!realmDB.isInTransaction()) {
//                        realmDB.beginTransaction();
//                    }
//                    if (ItemsList.get(position).getCount() == 0) {
//                        //then remove the product from cart
////                        userSchedule.deleteFromRealm();
//                    } else {
//                        //increase quantity of that product
//                        userSchedule.setCount(ItemsList.get(position).getCount());
//                    }
//                    realmDB.commitTransaction();
//                    adapter.notifyDataSetChanged();
//
//                }
//            } catch (Exception ex) {
//                Log.d("RError", ex.toString());
//                Toast.makeText(this, "Error in realm", Toast.LENGTH_SHORT).show();
//            }
//
//
//        }
    }

    @SuppressLint("StaticFieldLeak")
//    private class AsyncDataClass extends AsyncTask<Void, Void, String> {
//        String imagePath = getString(R.string.ImagePathUrl);
//        Context context;
//        String result = "";
//        ProgressDialog progressDialog;
//
//        AsyncDataClass(Context _con) {
//            this.context = _con;
//        }
//
//
//        @Override
//        protected String doInBackground(Void... params) {
//            String pass_url;
//            pass_url = getString(R.string.GetItems);
//            try {
//                URL url = new URL(pass_url);
//                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//                httpURLConnection.setRequestMethod("POST");
//                // httpURLConnection.setDoOutput(true);
//                httpURLConnection.setDoInput(true);
//
//                InputStream inputStream = httpURLConnection.getInputStream();
//                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
//
//                String line;
//                while ((line = bufferedReader.readLine()) != null) {
//                    result += line;
//                    Log.e("Result", result);
//                }
//                bufferedReader.close();
//                inputStream.close();
//                httpURLConnection.disconnect();
//                return result;
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            progressDialog = new ProgressDialog(context);
//            progressDialog.setTitle("Loading Data");
//            progressDialog.setCancelable(false);
//            progressDialog.setMessage("Please wait.........");
//            progressDialog.show();
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//
//
////            _asyncClass = null;
//
//            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
//            progressDialog.dismiss();
//
//            if (result == null) {
//                alertDialog.setTitle("Error Message");
//                alertDialog.setMessage("Can't Connect With Server");
//                alertDialog.show();
//            } else if (result.equals("0")) {
//                alertDialog.setTitle("Error Message");
//                alertDialog.setMessage("No Items added yet.");
//                alertDialog.show();
//            } else if (!result.equals("")) {
//
//                ItemsList = new ArrayList<>();
//                try {
//                    JSONObject jsonobject = new JSONObject(result);
//                    JSONArray JO = jsonobject.getJSONArray("Items");
//                    JSONObject object;
//                    int count;
////                    final RealmResults<ItemClass> allItem = realmDB.where(ItemClass.class).findAll();
//
//                    for (int i = 0; i < JO.length(); i++) {
//                        object = (JSONObject) JO.get(i);
//                        count = 0;
//                        try {
//                            if (allItem != null) {
//                                ItemClass userSchedule = allItem.where().equalTo("id", Integer.valueOf(object.optString("id"))).findFirst();
//
//                                if (userSchedule != null) {
//                                    count = userSchedule.getCount();
//                                    Log.e("Count", userSchedule.getId() + " " + String.valueOf(userSchedule.getCount()));
//                                }
//                            }
//
//                        } catch (Exception ex) {
//                            Log.d("RError", ex.toString());
//                            Toast.makeText(context, "Error in Realm", Toast.LENGTH_SHORT).show();
//                        }
//
////                        ItemsList.add(new ItemClass(Integer.valueOf(object.optString("id")), object.optString("name"),
////                                object.optString("price"), object.optString("description"), imagePath + object.optString("image"), object.optString("category"), count
////                        ));
//                    }//end of for loop
//
////                    adapter = new ItemCustomAdapter(context, R.layout.productlayout, ItemsList);
//                    recyclerView.setAdapter(adapter);
//                } catch (Exception e) {
//                    alertDialog.setTitle("Error");
//                    alertDialog.setMessage(result);
//                    alertDialog.show();
//                } //end of catch
//            } //end of else if
//            else {
//                alertDialog.setTitle("Error");
//                alertDialog.setMessage(result);
//                alertDialog.show();
//            }
//
//        }//end of on Post Execute Function
//
//        @Override
//        protected void onCancelled() {
////            _asyncClass = null;
//            // showProgress(false);
//        }// end of onCancelled Function
//
//
//        @Override
//        protected void onProgressUpdate(Void... values) {
//            super.onProgressUpdate(values);
//        }
//    }//end of AsyncClass


    private class CheckUserRequest extends AsyncTask<Void, Void, String> {
        ProgressDialog progressDialog;
        private Context _context;

        CheckUserRequest(Context context) {
            this._context = context;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(_context);
            progressDialog.setTitle("Checking");
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please wait.........");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            //  attempt authentication against a network service.
            try {

                URL url = new URL(getString(R.string.CheckUserRequestForNeedItem));
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");
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
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            if (result == null) {
                Toast.makeText(_context, "Can't Connect with server. Please check your internet connection", Toast.LENGTH_SHORT).show();

            } else {
                switch (result) {
                    case "2":
                        Toast.makeText(_context, "You can't request for needed item now...", Toast.LENGTH_SHORT).show();
                        break;
                    case "1":
                        StartAddNewNeededItemActivity();
                        break;
                    default:
                        Toast.makeText(_context, result, Toast.LENGTH_SHORT).show();
                        break;
                }
            }

        }

        @Override
        protected void onCancelled() {

        }
    }

    private void StartAddNewNeededItemActivity() {
        Intent intent = new Intent(this, AddNeededItemActivity.class);
        finish();
        startActivity(intent);
    }


}
