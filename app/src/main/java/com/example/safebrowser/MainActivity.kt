package com.example.safebrowser

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.InetSocketAddress
import java.net.Proxy
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
        webView.webViewClient = CustomDnsWebViewClient()

        // OPTIONAL: If you also want to handle custom error pages, you can override
        // onReceivedError(...) or onReceivedHttpError(...) in your WebViewClient.


        urlEditText = findViewById(R.id.urlEditText)
        loadButton = findViewById(R.id.loadButton)

        loadButton.setOnClickListener {
            val url = urlEditText.text.toString()
            if (url.isNotBlank()) {
                webView.loadUrl(processUrl(url))
            } else {
                Toast.makeText(this, "Please enter a valid URL", Toast.LENGTH_SHORT).show()
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

    // Check proxy access
    private fun checkProxyAccess(url: String) {
        // Configure the proxy
        val proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress(proxyIP, proxyPort))

        // Configure OkHttpClient with the proxy
        val client = OkHttpClient.Builder()
            .proxy(proxy)
            .build()

        // Create the request
        val request = Request.Builder()
            .url(url)
            .build()

        // Perform the network request in a background thread
        Thread {
            try {
                val response = client.newCall(request).execute()
                val statusCode = response.code
                val message = if (statusCode == 200) {
                    "Proxy is working! Response Code: $statusCode"
                } else {
                    "Proxy failed with Response Code: $statusCode"
                }
                runOnUiThread {
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }
                response.close()
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Request failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }






}

