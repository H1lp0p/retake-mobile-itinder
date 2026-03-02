package com.stuf.itinder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.stuf.itinder.databinding.MainScreenFragmentBinding

class MainScreenActivity : AppCompatActivity() {

    private lateinit var binding: MainScreenFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MainScreenFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
