package com.example.toreading.api;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

//Call for the api from server.
public interface Api {

    @GET("api/itembins/reading/device/5d07470cdcbb6dcfd1bf68e6")
    Call<ResponseBody> getMatData(
            @Header("Authorization") String bearerToken
    );

}
