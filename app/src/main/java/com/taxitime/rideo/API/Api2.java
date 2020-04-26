package com.taxitime.rideo.API;

import com.taxitime.rideo.Models.ChatResponse;
import com.taxitime.rideo.Models.CityResponse;
import com.taxitime.rideo.Models.Errorresponse;
import com.taxitime.rideo.Models.UserResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface Api2 {
    @FormUrlEncoded
    @POST("staffloginbyphone")
    Call<UserResponse> userlogin(
            @Field("phone") String phone
    );

    @FormUrlEncoded
    @POST("deletefav")
    Call<Errorresponse> deletefav(
            @Field("id") String id
    );

    @GET("getcity")
    Call<CityResponse> getcity();

    @FormUrlEncoded
    @POST("addchat")
    Call<Errorresponse> addchat(
            @Field("booking_id") String booking_id,
            @Field("uid") String uid,
            @Field("pid") String pid,
            @Field("message") String message,
            @Field("type") String type
    );

    @GET("getchat/{booking_id}")
    Call<ChatResponse> getchat(
            @Path("booking_id") String booking_id
    );

}
