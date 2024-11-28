package com.example.fattrack.view.notifications

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fattrack.R
import com.example.fattrack.databinding.ActivityNotificationsBinding

class NotificationsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // init ViewBinding
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // back button clicked
        binding.backButton.setOnClickListener {
            finish()
        }
    }
}