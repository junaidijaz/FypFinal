package com.example.Saad.MyFYPProject;

import android.annotation.SuppressLint;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.Saad.MyFYPProject.models.UniversalRes;
import com.example.Saad.MyFYPProject.models.retrofit.RetrofitClient;
import com.google.gson.Gson;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class    SignUp extends AppCompatActivity {

    EditText nameEditText,emailEditText,passwordEditText,confirmEditText;
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        nameEditText =  findViewById(R.id.user_name);
        emailEditText =  findViewById(R.id.user_email);
        passwordEditText =   findViewById(R.id.user_pass);
        confirmEditText = findViewById(R.id.user_confirm_password);
    }

    public void SignUp(View view) {

        if(TextUtils.isEmpty(nameEditText.getText().toString())){
            nameEditText.setError("Please enter name.");
            nameEditText.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(emailEditText.getText().toString())){
            emailEditText.setError("Please enter email.");
            emailEditText.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(passwordEditText.getText().toString())){
            passwordEditText.setError("Please enter password.");
            passwordEditText.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(confirmEditText.getText().toString())){
            confirmEditText.setError("Please enter password again.");
            confirmEditText.requestFocus();
            return;
        }


        if(nameEditText.getText().toString().length()<3){
            nameEditText.setError("Name should be greater than 2 char.");
            nameEditText.requestFocus();
            return;
        }
        if(!isEmailValid(emailEditText.getText().toString())){
            emailEditText.setError("Please enter valid email.");
            emailEditText.requestFocus();
            return;
        }

        if(passwordEditText.getText().toString().length()<5){
            passwordEditText.setError("Password should be greater than 4 char.");
            passwordEditText.requestFocus();
            return;
        }

        if(!passwordEditText.getText().toString().equals(confirmEditText.getText().toString())){
            confirmEditText.setError("Password doesn't matched!");
            confirmEditText.requestFocus();
            return;
        }
        String user_name,user_email,user_pass;
        user_name = nameEditText.getText().toString();
        user_pass = passwordEditText.getText().toString();
        user_email = emailEditText.getText().toString();

        if(CheckInternet()){
//            new BackgroundSignUp(this).execute(user_name,user_pass,user_email);
            signupUser(user_name,user_email,user_pass);

        }else{
            Toast.makeText(this, "No Internet Connection.", Toast.LENGTH_SHORT).show();
        }
    }

    private void signupUser(String user_name, String user_email, String user_pass) {

        Call<UniversalRes> call = RetrofitClient.getInstance().getApi().register(user_name,user_email,user_pass);

        call.enqueue(new Callback<UniversalRes>() {
            @Override
            public void onResponse(Call<UniversalRes> call, Response<UniversalRes> response) {
                if(response.body().isResult())
                {
                    startActivity(new Intent(SignUp.this,LoginActivity.class));
                    finish();
                    Toast.makeText(getApplicationContext(), "User Registered successfully...", Toast.LENGTH_SHORT).show();
                }else
                {
                    Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<UniversalRes> call, Throwable t) {
                Toast.makeText(SignUp.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public boolean CheckInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }//end of check internet

    private boolean isEmailValid(String email) {

        char c=email.charAt(0);
            if(c == '@'){
                return false;
            }
        return email.contains("@");

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;

    }

    @SuppressLint("StaticFieldLeak")
    public class BackgroundSignUp extends AsyncTask<String,Void,String> {
        Context context;
        ProgressDialog progressDialog;
        String result = "";

        BackgroundSignUp(Context ctx) {
            context = ctx;
        }

        @Override
        protected String doInBackground(String... voids) {



            try {
                String name, pass, email;
                name = voids[0];
                pass = voids[1];
                email = voids[2];

                URL url = new URL(getString(R.string.AppSignUpApi));
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                OutputStream outputStream = httpURLConnection.getOutputStream();
                Log.d("BG", "Output Stream");
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                Log.d("BG", "Posting");
                String post_data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8") + "&"
                        + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(pass, "UTF-8") + "&"
                        + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8")   ;
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
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
            progressDialog = new ProgressDialog(SignUp.this);
            progressDialog.setMessage("Singing Up");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            if (result == null) {
                final AlertDialog  alert = new AlertDialog.Builder(SignUp.this)
                        .setTitle("Sorry")
                        .setMessage("No Internet Connection Or Bad Connection.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //  Toast.makeText(GroundAdd.this , "ho gya hai" , Toast.LENGTH_SHORT).show();
                            }
                        }) .create();
                alert.show();
            }else if( result!=null ){
                Gson GsonObject= new Gson();
                try{
                    Log.e("Error",result);
                    SimpleJSONReturn jsonReturn = GsonObject.fromJson(result,SimpleJSONReturn.class);

                    if(jsonReturn.getTitle().equals("2")){
                        Toast.makeText(context, "Created Successfully!", Toast.LENGTH_SHORT).show();
                        ShowLoginActivity();
                    }else{
                        ShowDialog(context,jsonReturn);
                    }//end of else
                }catch(Exception ex){
                    Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
                    Log.e("Error",result);
                }

            }
            else{
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
            }//end of else

        }//end of on Post execute
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    private void ShowLoginActivity() {
        Intent i = new Intent(SignUp.this,LoginActivity.class);
        startActivity(i);
        finish();
    }

    private void ShowDialog(Context context, SimpleJSONReturn jsonReturn) {
        String alertTitle;
        if(jsonReturn.getTitle().equals("0")){
            alertTitle="Missing";
        }else{
            alertTitle="Add";
        }

        final AlertDialog alertDialog=new AlertDialog.Builder(context)
                .setMessage(jsonReturn.getMessage())
                .setTitle(alertTitle)
                .setCancelable(false)
                .setPositiveButton("Ok",null)
                .create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button okButton=(alertDialog).getButton(AlertDialog.BUTTON_POSITIVE);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
            }
        });

        alertDialog.show();

    }

}
