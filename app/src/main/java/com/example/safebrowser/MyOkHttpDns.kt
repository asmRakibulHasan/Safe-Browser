package com.example.safebrowser

import okhttp3.Dns
import java.net.InetAddress

object MyOkHttpDns : Dns {
    override fun lookup(hostname: String): List<InetAddress> {
        return MyCustomDnsResolver.resolve(hostname)
    }
}
