package com.example.safebrowser

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.safebrowser.databinding.FragmentLandingBinding
import com.example.safebrowser.viewmodel.WebViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL


class LandingFragment : Fragment() {

    lateinit var binding: FragmentLandingBinding

    private var webViewModel: WebViewModel? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLandingBinding.inflate(layoutInflater)
        webViewModel = ViewModelProvider(requireActivity())[WebViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loadButton.setOnClickListener {
            val url = binding.urlEditText.text.toString()
            if (url.isNotBlank()) {
                checkForDns(processUrl(url))
            } else {
                Toast.makeText(requireActivity(), "Please enter a valid URL", Toast.LENGTH_SHORT).show()
            }
        }

    }


    private fun checkForDns(url: String){
        lifecycleScope.launch(Dispatchers.IO) {
            val canResolve = canResolveUrl(url)
            withContext(Dispatchers.Main) {
                if (canResolve) {
                    webViewModel?.inputUrl = url
                    findNavController().navigate(R.id.action_landingFragment_to_webViewFragment)
                } else {
                    Toast.makeText(
                        requireActivity(),
                        "DNS resolution failed for $url",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


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