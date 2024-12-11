package com.example.fattrack.data.services.retrofit

import com.example.fattrack.data.services.responses.ResponseArticle
import com.example.fattrack.data.services.responses.ResponseDashboardMonth
import com.example.fattrack.data.services.responses.ResponseDashboardWeek
import com.example.fattrack.data.services.responses.ResponseDetailArticle
import com.example.fattrack.data.services.responses.ResponseHistory
import com.example.fattrack.data.services.responses.ResponseHome
import com.example.fattrack.data.services.responses.ResponseLogin
import com.example.fattrack.data.services.responses.ResponsePhoto
import com.example.fattrack.data.services.responses.ResponseRegister
import com.example.fattrack.data.services.responses.ResponseResetPassword
import com.example.fattrack.data.services.responses.ResponseScanImage
import com.example.fattrack.data.services.responses.ResponseSearchArticle
import com.example.fattrack.data.services.responses.ResponseSearchFood
import com.example.fattrack.data.services.responses.ResponseUpdateProfile
import com.example.fattrack.data.services.responses.ResponseUser
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

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


    //reset password
    @FormUrlEncoded
    @POST("forgot-password")
    suspend fun forgotPassword(
        @Field("email") email: String
    ) : Response<ResponseResetPassword>


    //Predict / scan image
    @Multipart
    @POST
    suspend fun predict(
        @Url url:String,
        @Header("Authorization") token: String,
        @Part("user_id") userId: RequestBody,
        @Part file: MultipartBody.Part,
    ) : Response<ResponseScanImage>

    //search
    @GET("makanan")
    suspend fun searhFood(
        @Header("Authorization") token: String,
        @Query("nama") nama: String
    ) : Response<ResponseSearchFood>

    //user by id
    @GET("users/{userId}")
    suspend fun getUserById(
        @Header("Authorization") token: String,
        @Path("userId") userId: String
    ): Response<ResponseUser>

    //update profile
    @Multipart
    @POST("updateProfile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Part("userId") userId: RequestBody,
        @Part("newName") newName: RequestBody,
        @Part file: MultipartBody.Part? = null,
    ) : Response<ResponseUpdateProfile>

    //Home
    @GET("home/histories/{userId}")
    suspend fun getHome(
        @Header("Authorization") token: String,
        @Path("userId") userId: String
    ): Response<ResponseHome>

    //history
    @GET("dashboard/check-history/three-days/{userId}")
    suspend fun getHistory(
        @Header("Authorization") token: String,
        @Path("userId") userId: String
    ): Response<ResponseHistory>

    //Dashboard Week
    @GET("dashboard/seven-days/{userId}")
    suspend fun getDashboardWeek(
        @Header("Authorization") token: String,
        @Path("userId") userId: String
    ): Response<ResponseDashboardWeek>

    //Dashboard Month
    @GET("dashboard/thirty-days/{userId}")
    suspend fun getDashboardMonth(
        @Header("Authorization") token: String,
        @Path("userId") userId: String
    ): Response<ResponseDashboardMonth>

    //upload photo
    @Multipart
    @POST("photo")
    suspend fun uploadPhoto(
        @Header("Authorization") token: String,
        @Part("user_id") userId: RequestBody,
        @Part file: MultipartBody.Part,
    ) : Response<ResponsePhoto>


    //articles
    @GET("articles")
    suspend fun getArticles(
        @Header("Authorization") token: String
    ): Response<ResponseArticle>

    //detail article
    @GET("articles/{id}")
    suspend fun getDetailArticle(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<ResponseDetailArticle>

    //search article
    @GET("articles/search")
    suspend fun searchArticle(
        @Header("Authorization") token: String,
        @Query("title") title: String
    ) : Response<ResponseSearchArticle>
}

