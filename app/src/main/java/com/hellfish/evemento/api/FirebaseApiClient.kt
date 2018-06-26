package com.hellfish.evemento.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder



class FirebaseApiClient {


    companion object {

        val baseURL: String = "https://deep-hook-204120.firebaseio.com"
        var retofit: Retrofit? = null

        val client: Retrofit
            get() {

                if (retofit == null) {
                    retofit = Retrofit.Builder()
                            .baseUrl(baseURL)
                            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().serializeNulls().create()))
                            .build()
                }
                return retofit!!
            }
    }
}
