package com.walton.productionlinedisplay.di

import com.walton.productionlinedisplay.api.ApiResponse
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
    fun providesRetrofitBuilder(): Retrofit.Builder{
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(Constant.BASE_URL)
    }

    @Singleton
    @Provides
    fun providesApi(retrofitBuilder: Retrofit.Builder, okHttpClient: OkHttpClient): ApiResponse{
        return retrofitBuilder.client(okHttpClient).build().create(ApiResponse::class.java)
    }

    @Singleton
    @Provides
    fun provideOkHTTPClient():OkHttpClient{
        return OkHttpClient.Builder().connectTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS).readTimeout(120, TimeUnit.SECONDS).socketFactory(RestrictedSocketFactory(
                DEFAULT_BUFFER_SIZE
            )).build()
    }
}