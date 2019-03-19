package com.sarlomps.evemento.api

import com.sarlomps.evemento.R
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ImageAPI {

    private val imageApi: ImageApiInterface
    private var currentCall: Call<ImageResponse>? = null

    init {
        val apiClient = ImageApiClient.client
        imageApi = apiClient.create(ImageApiInterface::class.java)
    }

    fun cancelCurrentCall() {
        currentCall?.cancel()
    }

    private fun pushImageCallback(callback: (String?, Int?) -> Unit) = object : Callback<ImageResponse> {
        override fun onResponse(call: Call<ImageResponse>?, response: Response<ImageResponse>?) {
            if (response != null && response.isSuccessful) {
                response.body()?.let {
                    if (it.error) {
                        callback(null, R.string.api_error_pushing_data)
                    } else {
                        callback(it.image, null)
                    }
                    return
                }
            }
            currentCall = null
            callback(null, R.string.api_error_pushing_data)
        }

        override fun onFailure(call: Call<ImageResponse>?, t: Throwable?) {
            call?.let {
                if (it.isCanceled) callback(null, R.string.empty)
                currentCall = null
                return
            }
            currentCall = null
            callback(null, R.string.api_error_pushing_data)
        }
    }

    /// IMAGES
    fun uploadImage(image: File, callback: (String?, Int?) -> (Unit)) {

        val filePart = MultipartBody.Part.createFormData("file", image.name, RequestBody.create(MediaType.parse("image/*"), image))
        currentCall = imageApi.uploadImage(filePart)
        currentCall?.enqueue(pushImageCallback(callback))
    }

}