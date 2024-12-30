package com.example.safebrowser

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var urlEditText: EditText
    private lateinit var loadButton: Button
    private val proxyIP = "92.222.153.172"
    private val proxyPort = 3128
    private val TAG = "MainActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)


        // Basic WebView settings
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW
        webView.clearCache(true)

        // Use our custom WebViewClient that intercepts requests
        webView.webViewClient = CustomDnsWebViewClient() /// kahf guard client
//        webView.webViewClient = WebViewClient() /// normal client



        urlEditText = findViewById(R.id.urlEditText)
        loadButton = findViewById(R.id.loadButton)

        loadButton.setOnClickListener {
            val url = urlEditText.text.toString()
            if (url.isNotBlank()) {
                checkForDns(processUrl(url))
            } else {
                Toast.makeText(this, "Please enter a valid URL", Toast.LENGTH_SHORT).show()
            }
        }



    }

    private fun checkForDns(url: String){
        lifecycleScope.launch(Dispatchers.IO) {
            val canResolve = canResolveUrl(url)
            withContext(Dispatchers.Main) {
                if (canResolve) {
                    webView.loadUrl(url)
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "DNS resolution failed for $url",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // Validate and format the URL
    private fun processUrl(input: String): String {
        var processedUrl = input
        if (!processedUrl.startsWith("http://") && !processedUrl.startsWith("https://")) {
            processedUrl = "https://$processedUrl"
        }
        return try {
            val url = URL(processedUrl)
            url.toString()
        } catch (e: Exception) {
            ""
        }
    }


    private fun canResolveUrl(url: String): Boolean {
        val host = Uri.parse(url).host.orEmpty()
        return try {
            val addresses = MyCustomDnsResolver.resolve(host)
            addresses.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }

}

