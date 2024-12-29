package com.example.safebrowser

import okhttp3.OkHttpClient

object NetworkClient {
    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .dns(MyOkHttpDns)  // <-- Use our custom DNS
            .build()
    }
}
