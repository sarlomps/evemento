package com.sarlomps.evemento.api
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import okhttp3.MultipartBody
import retrofit2.http.POST
import retrofit2.http.Multipart



interface  ImageApiInterface {

    @Multipart
    @POST("/upload")
    fun uploadImage(@Part image: MultipartBody.Part): Call<ImageResponse>
}
