package com.example.a3danimation.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface Api {
    @GET("api/itembins/reading/device/5d07470cdcbb6dcfd1bf68e6")
    Call<ResponseBody> getMatData(
            @Header("Authorization") String bearerToken
    );
}
