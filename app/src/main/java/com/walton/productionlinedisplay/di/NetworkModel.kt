package com.walton.productionlinedisplay.di

import com.walton.productionlinedisplay.api.ApiResponse
import com.walton.productionlinedisplay.api.ApiResponseFinalQc
import com.walton.productionlinedisplay.utils.Constant
import com.walton.productionlinedisplay.utils.RestrictedSocketFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okio.Timeout
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class NetworkModel {

    @Singleton
    @Provides
    fun provideOkHTTPClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .socketFactory(RestrictedSocketFactory(DEFAULT_BUFFER_SIZE))
            .build()
    }

    @BaseUrlOne
    @Singleton
    @Provides
    fun provideRetrofitOne(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @BaseUrlTwo
    @Singleton
    @Provides
    fun provideRetrofitTwo(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constant.BASE_URL_FINAL_QC_APPROVAL) // Replace with actual second base URL
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Singleton
    @Provides
    fun provideApiResponse(@BaseUrlOne retrofit: Retrofit): ApiResponse {
        return retrofit.create(ApiResponse::class.java)
    }

    @Singleton
    @Provides
    fun provideOtherApiResponse(@BaseUrlTwo retrofit: Retrofit): ApiResponseFinalQc {
        return retrofit.create(ApiResponseFinalQc::class.java)
    }
}
