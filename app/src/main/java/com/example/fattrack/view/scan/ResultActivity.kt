package com.example.fattrack.view.scan

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fattrack.R
import com.example.fattrack.databinding.ActivityResultBinding
import com.example.fattrack.view.MyBottomSheetFragment
import io.github.muddz.styleabletoast.StyleableToast

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init binding
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get URI from Intent
        val imageUri = intent.getStringExtra("image_uri")?.let { Uri.parse(it) }

        //set URI
        if (imageUri != null) {
            binding.previewImageView.setImageURI(imageUri)
        } else {
            showToast("image not found.")
        }

        // Cek bottom sheet
        val showBottomSheet = intent.getBooleanExtra("SHOW_BOTTOM_SHEET", false)
        if (showBottomSheet) {
            displayBottomSheet()
        }
    }

    // display bottom sheet
    private fun displayBottomSheet(){
        val bottomSheetFragment = MyBottomSheetFragment()
        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
    }

    private fun showToast(message: String) {
        val toastCustom = StyleableToast.makeText(applicationContext, message, R.style.StyleableToast)
        toastCustom.show()
    }
}
