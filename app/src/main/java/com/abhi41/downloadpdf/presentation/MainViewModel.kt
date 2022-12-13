package com.abhi41.downloadpdf.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhi41.downloadpdf.data.remote.RetrofitServiceInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    val apiService: RetrofitServiceInstance,
    val fileDir: File
) : ViewModel() {

    private var pdfFileName: File
    private var dirPath: String
    private var fileName: String
    var isFileReadyObserver = MutableLiveData<Boolean>()

    init {
        dirPath = "${fileDir}/cert/pdfFiles"
        val dirFile = File(dirPath)
        if (!dirFile.exists()) {
            dirFile.mkdirs()
        }

        fileName = "DemoGraphs.pdf"
        val file = "${dirPath}/${fileName}"
        pdfFileName = File(file)

        if (pdfFileName.exists()) {
            pdfFileName.delete()
        }

    }

    fun getPdfFileUri(): File = pdfFileName

    fun downloadPdfFile(pdfUrl: String) {
        viewModelScope.launch {
            apiService.downloadPdfFile(pdfUrl = pdfUrl.trim()).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        isFileReadyObserver.postValue(true)
                        val body = response.body()
                        val inputStream = body!!.byteStream()
                        writeToFile(inputStream)
                    }

                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    isFileReadyObserver.postValue(false)
                }

            })
        }
    }

    private fun writeToFile(inputStream: InputStream) {
        try {
            val fileReder = ByteArray(4096)
            var fileSizeDownloaded = 0
            val fos: OutputStream = FileOutputStream(pdfFileName)

            do {
                val read = inputStream.read(fileReder)
                if (read != -1) {
                    fos.write(fileReder, 0, read)
                    fileSizeDownloaded += read
                }
            } while (read != -1)
            fos.flush()
            fos.close()
            isFileReadyObserver.postValue(true)
        } catch (e: Exception) {
            e.printStackTrace()
            isFileReadyObserver.postValue(false)
        }
    }


}