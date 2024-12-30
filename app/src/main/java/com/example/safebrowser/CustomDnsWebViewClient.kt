package com.example.safebrowser

import android.graphics.Bitmap
import android.net.http.SslError
import android.util.Log
import android.webkit.SslErrorHandler
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import okhttp3.Request

class CustomDnsWebViewClient : WebViewClient() {

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
    }

    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest
    ): WebResourceResponse? {
        val requestUrl = request.url.toString()
        val client = NetworkClient.okHttpClient

        val builder = Request.Builder().url(requestUrl)
        request.requestHeaders.forEach { (key, value) ->
            builder.addHeader(key, value)
        }

        Log.d("RakibWebCheck","Query Url: $requestUrl")

        try {
            val response = client.newCall(builder.build()).execute()

            if (!response.isSuccessful) {
                response.close()
                return null
            }

            val contentType = response.header("Content-Type") ?: "text/html"
            val mimeType = getMimeType(contentType)
            val encoding = getCharset(contentType)
            val inputStream = response.body?.byteStream()

            val statusCode = response.code
            val reasonPhrase = response.message.ifEmpty { "OK" }

            val responseHeaders = mutableMapOf<String, String>()
            for ((name, value) in response.headers) {
                responseHeaders[name] = value
            }

            return WebResourceResponse(
                mimeType,
                encoding,
                statusCode,
                reasonPhrase,
                responseHeaders,
                inputStream
            )
        } catch (sslEx: javax.net.ssl.SSLPeerUnverifiedException) {
            // SSL error => block and also stop loading
            Log.d("RakibWebCheck","SSLPeerUnverifiedException Url: $requestUrl")
//            view.post { view.stopLoading() }
//            return null
            return WebResourceResponse(
                null,             // mimeType
                null,             // encoding
                403,              // statusCode
                "SSL Error",      // reasonPhrase MUST NOT be an empty string
                null,             // responseHeaders
                null              // inputStream
            )
        } catch (ex: Exception) {
            // Other error => block
            Log.d("RakibWebCheck","OtherException Url: $requestUrl")
            return null
        }
    }


    private fun getMimeType(contentType: String): String {
        return contentType.split(";").firstOrNull() ?: "text/html"
    }

    private fun getCharset(contentType: String): String {
        val parts = contentType.split(";").map { it.trim() }
        val charsetPart = parts.find { it.startsWith("charset=", ignoreCase = true) }
        return charsetPart?.substringAfter("charset=") ?: "UTF-8"
    }

    override fun onReceivedSslError(
        view: WebView?,
        handler: SslErrorHandler?,
        error: SslError?
    ) {
        handler?.cancel()

    }



}
