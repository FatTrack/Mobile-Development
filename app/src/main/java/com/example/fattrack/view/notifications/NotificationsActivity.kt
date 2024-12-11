package com.example.fattrack.view.notifications

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fattrack.R
import com.example.fattrack.data.ViewModelFactory
import com.example.fattrack.data.adapter.NotificationAdapter
import com.example.fattrack.data.viewmodel.NotificationViewModel
import com.example.fattrack.databinding.ActivityNotificationsBinding
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.coroutines.launch

class NotificationsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationsBinding
    private val notificationViewModel: NotificationViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // init ViewBinding
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // back button clicked
        binding.backButton.setOnClickListener {
            finish()
        }

        setupRecyclerView()
        loadNotifications()
        setupDeleteButton()
    }

    private fun setupRecyclerView() {
        binding.rvNotification.layoutManager = LinearLayoutManager(this)
    }

    private fun loadNotifications() {
        notificationViewModel.viewModelScope.launch {
            val notifications = notificationViewModel.getNotifications()

            if (notifications.isEmpty()) {
                binding.rvNotification.visibility = View.GONE
                binding.tvEmptyNotification.visibility = View.VISIBLE
            } else {
                binding.rvNotification.visibility = View.VISIBLE
                binding.tvEmptyNotification.visibility = View.GONE
                binding.rvNotification.adapter = NotificationAdapter(notifications)
            }
        }
    }

    private fun setupDeleteButton() {
        binding.deleteNotification.setOnClickListener {
            // Tampilkan dialog konfirmasi
            AlertDialog.Builder(this)
                .setTitle("Hapus Notifikasi")
                .setMessage("Apakah kamu yakin ingin menghapus semua notifikasi?")
                .setPositiveButton("OK") { _, _ ->
                    notificationViewModel.deleteAllNotifications()
                    showToast("Notification cleared")
                    loadNotifications()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun showToast(message: String) {
        val toastCustom = StyleableToast.makeText(applicationContext, message, R.style.StyleableToast)
        toastCustom.show()
    }
    }
