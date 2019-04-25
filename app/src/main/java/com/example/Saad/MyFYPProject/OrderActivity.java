package com.example.Saad.MyFYPProject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.Saad.MyFYPProject.models.orders.Orders;
import com.example.Saad.MyFYPProject.models.orders.OrdersRes;
import com.example.Saad.MyFYPProject.models.retrofit.RetrofitClient;
import com.example.Saad.MyFYPProject.models.user.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.Saad.MyFYPProject.utils.Constants.token;

public class OrderActivity extends AppCompatActivity {
    String OrderDetails;
    ArrayList<Orders> orderDetailsList;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

//        OrderDetails = getIntent().getStringExtra("order");
//        Log.e("Json",OrderDetails);
        listView = findViewById(R.id.recyclerView);
        fetchOrdersFromApi();


//        try {
//            orderDetailsList= new ArrayList<>();
//            JSONObject jsonObject = new JSONObject(OrderDetails);
//            JSONArray jsonArray = jsonObject.getJSONArray("Orders");
//            JSONObject jsonObject1 ;
//            for (int i= 0; i<jsonArray.length(); i++){
//                jsonObject1 = (JSONObject)  jsonArray.get(i);
//                orderDetailsList.add(new OrderDetailsClass(Integer.valueOf(jsonObject1.getString("id")),jsonObject1.getString("address"),
//                        jsonObject1.getString("grand_total"),jsonObject1.getString("order_status")));
//
//            }
//
//            OrderDetailsCustomAdapter orderDetailsCustomAdapter =new OrderDetailsCustomAdapter(this,R.layout.order_layout,orderDetailsList);
//            listView.setAdapter(orderDetailsCustomAdapter);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Error in Json", Toast.LENGTH_SHORT).show();
//        }
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//                Intent intent = new Intent(OrderActivity.this,ShowProductOfOrder.class);
//                intent.putExtra("Id",String.valueOf(orderDetailsList.get(i).getId()));
//                startActivity(intent);
//
//            }
//        });

    }

    private void fetchOrdersFromApi() {

        Call<OrdersRes> call = RetrofitClient.getInstance().getApi().getAllCompletedOrders(token);

        call.enqueue(new Callback<OrdersRes>() {
            @Override
            public void onResponse(Call<OrdersRes> call, Response<OrdersRes> response) {
                if (response.body().isResult()) {
                    orderDetailsList = response.body().getOrders();
                    OrderDetailsCustomAdapter orderDetailsCustomAdapter = new OrderDetailsCustomAdapter(OrderActivity.this, R.layout.order_layout, orderDetailsList);
                    listView.setAdapter(orderDetailsCustomAdapter);
                } else {
                    new MainActivity().logoutUser();
                }
            }

            @Override
            public void onFailure(Call<OrdersRes> call, Throwable t) {
                Toast.makeText(OrderActivity.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
