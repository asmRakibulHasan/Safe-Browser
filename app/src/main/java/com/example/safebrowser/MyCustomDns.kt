package com.example.safebrowser

import org.xbill.DNS.*
import java.net.InetAddress
import java.net.UnknownHostException

object MyCustomDnsResolver {

    private const val CUSTOM_DNS_SERVER = "51.142.0.101" //  IP or URL

    /**
     * Manually resolve the given [hostname] by querying the custom DNS server.
     *
     * Returns a list of InetAddress if successful, or throws UnknownHostException on failure.
     */
    @Throws(UnknownHostException::class)
    fun resolve(hostname: String): List<InetAddress> {
        if (hostname.isBlank()) {
            throw UnknownHostException("Hostname is blank.")
        }

        return try {
            // We create a simple resolver pointing to the custom DNS server.
            val resolver = SimpleResolver(CUSTOM_DNS_SERVER).apply {
                // Optionally set a timeout, e.g.
                // timeout = 5
            }

            // We do a lookup for A records (IPv4). For IPv6, you’d also do AAAA, etc.
            val lookup = Lookup(hostname, Type.A)
            lookup.setResolver(resolver)

            val result = lookup.run()
            if (result == null || lookup.result == Lookup.HOST_NOT_FOUND || lookup.result == Lookup.TYPE_NOT_FOUND) {
                throw UnknownHostException("No DNS records found for $hostname on $CUSTOM_DNS_SERVER")
            }

            // Convert answers to InetAddress
            val addresses = result.mapNotNull { record ->
                if (record is ARecord) {
                    // ARecord gives IPv4 address
                    InetAddress.getByAddress(hostname, record.address.address)
                } else null
            }

            if (addresses.isEmpty()) {
                throw UnknownHostException("No valid A records for $hostname on $CUSTOM_DNS_SERVER")
            }
            addresses
        } catch (e: Exception) {
            e.printStackTrace()
            throw UnknownHostException("Failed DNS lookup for $hostname on $CUSTOM_DNS_SERVER. Error: ${e.message}")
        }
    }
}
