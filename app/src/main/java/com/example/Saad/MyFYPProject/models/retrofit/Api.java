package com.example.Saad.MyFYPProject.models.retrofit;

import com.example.Saad.MyFYPProject.models.UniversalRes;
import com.example.Saad.MyFYPProject.models.items.ItemsRes;
import com.example.Saad.MyFYPProject.models.orders.OrdersRes;
import com.example.Saad.MyFYPProject.models.user.UserRes;
import com.example.Saad.MyFYPProject.neededitems.NeededItemsRes;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Api {

    @FormUrlEncoded
    @POST("login")
    Call<UniversalRes> login(
            @Field("email") String email,
            @Field("password") String pass);


    @FormUrlEncoded
    @POST("register")
    Call<UniversalRes> register(
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String pass);

    @FormUrlEncoded
    @POST("get_user_details")
    Call<UserRes> getUserDetail(
            @Field("token") String token);


    @FormUrlEncoded
    @POST("items")
    Call<ItemsRes> getAllItems(
            @Field("token") String token);


    @FormUrlEncoded
    @POST("my-orders")
    Call<OrdersRes> getAllCompletedOrders(
            @Field("token") String token);

    @FormUrlEncoded
    @POST("needed-items")
    Call<NeededItemsRes> getNeededItems(
            @Field("token") String token);


    @FormUrlEncoded
    @POST("vote-needed-item")
    Call<UniversalRes> voteNeededItems(
            @Field("token") String token,
            @Field("item_id") String item_id);


    @FormUrlEncoded
    @POST("add-needed-item")
    Call<UniversalRes> addNeededItems(
            @Field("name") String name,
            @Field("price") String price,
            @Field("description") String description,
            @Field("image") String image,
            @Field("token") String token
            );

}
