package com.taxitime.rideo.Retrofit;


import com.taxitime.rideo.Helper.URLHelper;

import retrofit2.Retrofit;



public class RetrofitClient {
    public static final String BASE_URL = "https://maps.googleapis.com/maps/api/geocode/";
    private static Retrofit retrofit = null;


    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
//                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getLiveTrackingClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(URLHelper.base)
                    .build();
        }
        return retrofit;
    }
}
