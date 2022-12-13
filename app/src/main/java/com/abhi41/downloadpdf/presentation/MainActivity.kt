package com.abhi41.downloadpdf.presentation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.abhi41.downloadpdf.BuildConfig
import com.abhi41.downloadpdf.R
import com.abhi41.downloadpdf.databinding.ActivityMainBinding
import com.abhi41.downloadpdf.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        observers()
        onClickListeners()


    }

    private fun observers() {
        mainViewModel.isFileReadyObserver.observe(this) { isReady ->

            if (!isReady) {
                Toast.makeText(this@MainActivity, getString(R.string.pdf_download_fail), Toast.LENGTH_SHORT)
                    .show()
                binding.progressDownload.visibility = View.GONE
            } else {
                Toast.makeText(this@MainActivity, getString(R.string.pdf_download_success), Toast.LENGTH_SHORT)
                    .show()
                try {
                    binding.PDFView.fromUri(
                        FileProvider.getUriForFile(
                            applicationContext,
                            BuildConfig.APPLICATION_ID + ".provider",
                            mainViewModel.getPdfFileUri()
                        )
                    ).load()
                    binding.progressDownload.visibility = View.GONE
                } catch (e: Exception) {
                    e.printStackTrace()
                    binding.progressDownload.visibility = View.GONE
                    Toast.makeText(this@MainActivity, getString(R.string.pdf_download_fail), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    }
    private fun onClickListeners() {
        binding.btnDownload.setOnClickListener {
            binding.progressDownload.visibility = View.VISIBLE
            mainViewModel.downloadPdfFile(Constants.PDF_URL)
        }
    }

}