package com.abhi41.downloadpdf.data.remote

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface RetrofitServiceInstance {

    @GET
    fun downloadPdfFile(@Url pdfUrl: String): Call<ResponseBody>

}