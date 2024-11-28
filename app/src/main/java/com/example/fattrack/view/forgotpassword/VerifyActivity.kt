package com.example.fattrack.view.forgotpassword

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fattrack.databinding.ActivityVerifyBinding

class VerifyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerifyBinding // Declare binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Initialize View Binding
        binding = ActivityVerifyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnVerify.setOnClickListener {
            // Navigate to VerifyActivity
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent)
        }
    }
}