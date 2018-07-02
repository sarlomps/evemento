package com.hellfish.evemento.api

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ImageApiClient {


    companion object {

        val baseURL: String = "https://vgy.me"
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



