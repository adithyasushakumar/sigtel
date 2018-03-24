package com.adhithya.track.service;

import com.adhithya.track.responce.Locationresponce;
import com.adhithya.track.responce.LoginResponce;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Hari Group Unity on 16-03-2018.
 */

public interface ApiServices {

    @FormUrlEncoded
    @POST("loginapi.php")
    Call<LoginResponce> userLogin(
            @Field("username") String username,
            @Field("password") String password,
            @Field("key") String keu);

    @FormUrlEncoded
    @POST("location_details.php")
    Call<Locationresponce> sendLocation(@Field("key") String key,
                                        @Field("uid") String uid,
                                        @Field("lattitude") String lattitude,
                                        @Field("longtitude") String longtitude);


}
