package com.example.fattrack.data.services.retrofit

import com.example.fattrack.data.services.responses.ResponseArticle
import com.example.fattrack.data.services.responses.ResponseLogin
import com.example.fattrack.data.services.responses.ResponseRegister
import com.example.fattrack.data.services.responses.ResponseScanImage
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    //AUTH
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("email") email: String,
        @Field("nama") nama: String,
        @Field("password") password: String,
    ) : Response<ResponseRegister>

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ) : Response<ResponseLogin>


    //Predict / scan image
    @Multipart
    @POST("predict_image")
    suspend fun predict(
        @Part("uploaded_file") image: MultipartBody.Part,
        @Part("user_id") userId: String,
    ) : Response<ResponseScanImage>

    //articles
    @GET("articles")
    suspend fun getArticles(): Response<ResponseArticle>
}

