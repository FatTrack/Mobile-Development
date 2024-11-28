package com.example.fattrack.view.profile

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fattrack.R
import com.example.fattrack.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //init binding
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}