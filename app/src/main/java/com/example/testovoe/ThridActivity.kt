package com.example.testovoe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.testovoe.databinding.ActivityThridBinding

class ThridActivity : AppCompatActivity() {

    private lateinit var binding: ActivityThridBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThridBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}