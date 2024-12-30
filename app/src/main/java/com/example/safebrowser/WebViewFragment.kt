package com.example.safebrowser

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.safebrowser.databinding.FragmentLandingBinding
import com.example.safebrowser.databinding.FragmentWebViewBinding
import com.example.safebrowser.viewmodel.WebViewModel

class WebViewFragment : Fragment() {

    lateinit var binding: FragmentWebViewBinding
    private var webViewModel: WebViewModel? = null
    private lateinit var webView: WebView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentWebViewBinding.inflate(layoutInflater)
        webViewModel = ViewModelProvider(requireActivity())[WebViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView = binding.webView

//         Basic WebView settings
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW
        webView.clearCache(true)

//         Use our custom WebViewClient that intercepts requests
        webView.webViewClient = CustomDnsWebViewClient() /// kahf guard client
//        webView.webViewClient = WebViewClient() /// normal client

        webViewModel?.inputUrl?.let { webView.loadUrl(it) }

        binding.homeBtn.setOnClickListener{
            findNavController().navigateUp()
        }
        binding.leftBtn.setOnClickListener{
            if(webView.canGoBack()){
                webView.goBack()
            }
        }
        binding.rightBtn.setOnClickListener{
            if(webView.canGoForward()){
                webView.goForward()
            }
        }
    }

}