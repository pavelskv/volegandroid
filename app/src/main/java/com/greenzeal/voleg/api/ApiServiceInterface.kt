package com.greenzeal.voleg.api

import com.google.gson.GsonBuilder
import com.greenzeal.voleg.models.Advs
import com.greenzeal.voleg.models.NewsList
import com.greenzeal.voleg.util.Constants
import io.reactivex.Completable
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface ApiServiceInterface {

    @GET("api/advs/list")
    fun getAdvList(@Query("pageno") page: Int): Observable<Advs>

    @Multipart
    @POST("/api/feedbacks/add/")
    fun submitFeedback(@Header("boundary") boundary: String, @Part feedback: MultipartBody.Part): Completable

    @Multipart
    @POST("/api/advs/add/")
    fun submitAdv(@Header("boundary") boundary: String, @Part adv: MultipartBody.Part, @Part image: MultipartBody.Part): Completable

    @Multipart
    @POST("/api/advs/add/")
    fun submitAdv(@Header("boundary") boundary: String, @Part adv: MultipartBody.Part): Completable

    @GET("api/news/list")
    fun getNewsList(@Query("pageno") page: Int): Observable<NewsList>

    companion object Factory {
        fun create(): ApiServiceInterface {
            val gson = GsonBuilder()
                .setLenient()
                .create()


            val retrofit = retrofit2.Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(Constants.BASE_URL)
                .client(getOkHttpCleint(getHttpLoggingInterceptor()))
                .build()

            return retrofit.create(ApiServiceInterface::class.java)
        }

        private fun getHttpLoggingInterceptor(): HttpLoggingInterceptor? {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            return httpLoggingInterceptor
        }

        private fun getOkHttpCleint(httpLoggingInterceptor: HttpLoggingInterceptor?): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor!!)
                .build()
        }

    }
}