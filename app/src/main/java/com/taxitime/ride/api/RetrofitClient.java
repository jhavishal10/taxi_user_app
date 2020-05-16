package com.taxitime.ride.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.taxitime.ride.Helper.URLHelper;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static String BASE_URL =  URLHelper.base +"cabuser/public/" ;
    private static RetrofitClient mInstance;
    private Retrofit retrofit;
    OkHttpClient client = new OkHttpClient();
    Gson gson = new GsonBuilder()
            .setLenient()
            .create();
    private RetrofitClient(){
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
    public static synchronized RetrofitClient getInstance(){
        if(mInstance == null){
            mInstance = new RetrofitClient();
        }
        return mInstance;
    }
    public ApiService getApi(){
        return retrofit.create(ApiService.class);
    }
}
