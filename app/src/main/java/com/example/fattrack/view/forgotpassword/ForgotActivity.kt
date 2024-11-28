package com.example.fattrack.view.forgotpassword

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fattrack.databinding.ActivityForgotBinding // Auto-generated binding class

class ForgotActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotBinding // Declare binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize View Binding
        binding = ActivityForgotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle window insets for edge-to-edge design
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set click listener for the Continue button
        binding.btnContinue.setOnClickListener {
            // Navigate to VerifyActivity
            val intent = Intent(this, VerifyActivity::class.java)
            startActivity(intent)
        }
    }
}
