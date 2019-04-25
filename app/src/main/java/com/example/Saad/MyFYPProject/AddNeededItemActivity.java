package com.example.Saad.MyFYPProject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.Saad.MyFYPProject.models.UniversalRes;
import com.example.Saad.MyFYPProject.models.retrofit.RetrofitClient;
import com.example.Saad.MyFYPProject.utils.Constants;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNeededItemActivity extends AppCompatActivity implements View.OnClickListener {
    EditText nameEditText, priceEditText, descriptionEditText;
    ImageView imageView;
    Button addButton, selectImageButton;
    String base64Image = "";
    String name = "", price = "", description = "";
    String userID = "";
    ProgressDialog progressDialog;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_needed_item);
        nameEditText = findViewById(R.id.product_name);
        priceEditText = findViewById(R.id.product_price);
        descriptionEditText = findViewById(R.id.product_description);
        imageView = findViewById(R.id.productImageView);
        addButton = findViewById(R.id.AddNeededItemButton);
        selectImageButton = findViewById(R.id.SelectImageButton);
        addButton.setOnClickListener(this);
        selectImageButton.setOnClickListener(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        userID = preferences.getString("id", "");
    }

    public boolean CheckInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }//end of check internet


    @Override
    public void onClick(View view) {
        if (view == addButton) {

            if (Validation()) {
                addNeededProduct();
//                    AddNeededProductClass addNeededProductClass=new AddNeededProductClass(this);
//                    addNeededProductClass.execute((Void) null);
            }

        } else if (view == selectImageButton) {
            Intent PhotoPicker = new Intent(Intent.ACTION_PICK);
            PhotoPicker.setType("image/*");
            startActivityForResult(PhotoPicker, 1);
        }
    }

    private void addNeededProduct() {
        progressDialog = new ProgressDialog(AddNeededItemActivity.this);
        progressDialog.setTitle("Adding");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait.........");
        progressDialog.show();
        Call<UniversalRes> call = RetrofitClient.getInstance().getApi().addNeededItems(name, price, description, "", Constants.token);
        call.enqueue(new Callback<UniversalRes>() {
            @Override
            public void onResponse(Call<UniversalRes> call, Response<UniversalRes> response) {
                if (response.body().isResult()) {
                    Toast.makeText(AddNeededItemActivity.this, "Item added to voting list", Toast.LENGTH_SHORT).show();
                    progressDialog.show();
                } else {
                    new MainActivity().logoutUser();
                }
            }

            @Override
            public void onFailure(Call<UniversalRes> call, Throwable t) {
                Toast.makeText(AddNeededItemActivity.this, "Check Internet connection or you can't add items right now...", Toast.LENGTH_SHORT).show();
                Log.e("", "onFailure: " + t.getMessage());
                progressDialog.hide();
            }
        });

    }

    private boolean Validation() {
        if (nameEditText.getText().toString().trim().equals("")) {
            nameEditText.setError("Please enter product name");
            return false;
        }
        nameEditText.setError(null);
        if (priceEditText.getText().toString().trim().equals("")) {
            priceEditText.setError("Please enter product price");
            return false;
        }
        priceEditText.setError(null);
        if (descriptionEditText.getText().toString().trim().equals("")) {
            descriptionEditText.setError("Please enter product description");
            return false;
        }
        descriptionEditText.setError(null);
//        if(base64Image.equals("")){
//            Toast.makeText(this, "Please select product image.", Toast.LENGTH_SHORT).show();
//            return false;
//        }
        name = nameEditText.getText().toString();
        price = priceEditText.getText().toString();
        description = descriptionEditText.getText().toString();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(selectedImage);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream); //bm is the bitmap object
                byte[] b = byteArrayOutputStream.toByteArray();
                base64Image = Base64.encodeToString(b, Base64.DEFAULT);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class AddNeededProductClass extends AsyncTask<Void, Void, String> {
        Context context;
        ProgressDialog progressDialog;

        AddNeededProductClass(Context _con) {
            this.context = _con;
        }


        @Override
        protected String doInBackground(Void... params) {
            String pass_url;
            pass_url = getString(R.string.AddNeededItem);
            try {
                URL url = new URL(pass_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8") + "&"
                        + URLEncoder.encode("price", "UTF-8") + "=" + URLEncoder.encode(price, "UTF-8") + "&"
                        + URLEncoder.encode("description", "UTF-8") + "=" + URLEncoder.encode(description, "UTF-8") + "&"
                        + URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(base64Image, "UTF-8") + "&"
                        + URLEncoder.encode("userID", "UTF-8") + "=" + URLEncoder.encode(userID, "UTF-8");
                bufferedWriter.write(post_data);
                Log.e("Data", post_data);
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
            progressDialog.setTitle("Adding");
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
            } else {
                Gson GsonObject = new Gson();
                try {
                    SimpleJSONReturn simpleJSONReturn = GsonObject.fromJson(result, SimpleJSONReturn.class);
                    if (simpleJSONReturn.getTitle().equals("3")) {
                        Toast.makeText(context, simpleJSONReturn.getMessage(), Toast.LENGTH_SHORT).show();
                        StartMainActivity();
                    } else {
                        alertDialog.setTitle("Result");
                        alertDialog.setMessage(simpleJSONReturn.getMessage());
                        alertDialog.show();
                    }
                } catch (Exception ex) {
                    alertDialog.setTitle("Exception");
                    alertDialog.setMessage(result + "\n" + ex.toString());
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
    }//end of AsyncClass

    private void StartMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        StartMainActivity();
    }


}
