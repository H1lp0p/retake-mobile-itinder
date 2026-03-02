package com.stuf.itinder

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.stuf.itinder.databinding.AboutSelfFragmentBinding
import com.stuf.itinder.utils.hapticClick

class AboutSelfFragment : Fragment() {

    private var _binding: AboutSelfFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AboutSelfFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.PlaceholderBtn.setOnClickListener {
            it.hapticClick()
            val context = requireContext()
            val intent = Intent(context, MainScreenActivity::class.java)
            startActivity(intent)
        }
    }
}
