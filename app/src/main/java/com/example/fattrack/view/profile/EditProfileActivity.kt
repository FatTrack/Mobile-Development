package com.example.fattrack.view.profile

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.fattrack.R
import com.example.fattrack.data.ViewModelFactory
import com.example.fattrack.data.viewmodel.HomeViewModel
import com.example.fattrack.databinding.ActivityEditProfileBinding
import com.example.fattrack.view.ProfileFragment
import com.example.fattrack.view.loadingDialog.DialogLoading
import com.example.fattrack.view.scan.reduceFileImage
import com.example.fattrack.view.scan.uriToFile
import io.github.muddz.styleabletoast.StyleableToast

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private var imageUri: Uri? = null
    private val homeViewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var dialogLoading: DialogLoading
    private lateinit var defaultImage: String
    private lateinit var defaultName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //init binding
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init dialog loading
        dialogLoading = DialogLoading(this)

        initViewModel()
        setupValue()
        buttonClickHandle()
        observeViewModel()
    }


    //init
    private fun initViewModel() {
        homeViewModel.getUserById()
    }


    //setup
    private fun setupValue() {
        binding.apply {
            //set value
            homeViewModel.userResponse.observe(this@EditProfileActivity) { response ->
                if (response != null) {
                    editName.setText(response.nama)
                    editEmail.setText(response.email)

                    val imageSrc = response.fotoProfile
                    if (imageSrc != null) {
                        Glide.with(this@EditProfileActivity)
                            .load(imageSrc)
                            .apply(RequestOptions.circleCropTransform())
                            .placeholder(R.drawable.circle_background)
                            .error(R.drawable.circle_background)
                            .into(profileImage)
                    } else {
                        Glide.with(this@EditProfileActivity)
                            .load(R.drawable.default_pp)
                            .apply(RequestOptions.circleCropTransform())
                            .placeholder(R.drawable.circle_background)
                            .error(R.drawable.circle_background)
                            .into(profileImage)
                    }

                    //list init
                    if (imageSrc != null) {
                        defaultImage = imageSrc
                    }
                    defaultName = response.nama.toString()
                }
            }
        }
    }


    //button click handle
    private fun buttonClickHandle() {
        //cancel button
        binding.btnCancel.setOnClickListener {
            finish()
        }

        //edit photo
        binding.editProfileImage.setOnClickListener {
            startGallery()
        }

        //save button
        binding.btnSelesai.setOnClickListener {
            val name = binding.editName.text.toString().ifEmpty { defaultName }
            val photo = imageUri?.let {
                try {
                    uriToFile(it, this).reduceFileImage()
                } catch (e: Exception) {
                    null
                }
            }
            if (photo != null && name.isNotEmpty()) {
                homeViewModel.updateProfile(photo, name)
            } else if (name.isNotEmpty()) {
                //if name change
                homeViewModel.updateProfile(null, name)
            } else {
                //back to profile
                showSuccessDialog("Profile updated successfully")
            }
        }
    }


    //launcher gallery
    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            previewImage(uri)
            imageUri = uri //set imageURI
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }


    //preview image
    private fun previewImage(uri: Uri) {
        Glide.with(this@EditProfileActivity)
            .load(uri)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.circle_background)
            .error(R.drawable.circle_background)
            .into(binding.profileImage)
    }


    //observe
    private fun observeViewModel() {
        //response & result
        homeViewModel.updateProfileResponse.observe(this) { response ->
            if (response != null) {
                if (response.code == 200) {
                    showSuccessDialog("Profile updated successfully")
                } else {
                    showErrorDialog(response.data?.message)
                }
            } else {
                showErrorDialog("Failed to update profile")
            }
        }

        //error
        homeViewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage != null) {
                showErrorDialog(errorMessage)
            }
        }

        //loading
        homeViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                dialogLoading.startLoading()
            } else {
                dialogLoading.stopLoading()
            }
        }
    }


    //alert dialog error
    private fun showErrorDialog(message: String?) {
        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Failed!")
            .setContentText("$message")
            .setConfirmText("OK")
            .setConfirmClickListener { dialog ->
                dialog.dismissWithAnimation()
            }
            .show()
    }


    //alert dialog success
    private fun showSuccessDialog(message: String) {
        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText("Success!")
            .setContentText(message)
            .setConfirmClickListener { dialog ->
                dialog.dismissWithAnimation()
                //back to profile
                val profileFragment = ProfileFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, profileFragment)
                    .addToBackStack(null)
                    .commit()
            }
            .show()
    }

    private fun showToast(message: String) {
        val toastCustom = StyleableToast.makeText(applicationContext, message, R.style.StyleableToast)
        toastCustom.show()
    }
}