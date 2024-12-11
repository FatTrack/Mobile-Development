package com.example.fattrack.view.forgotpassword

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.fattrack.data.ViewModelFactory
import com.example.fattrack.data.viewmodel.ForgotPassViewModel
import com.example.fattrack.databinding.ActivityForgotBinding
import com.example.fattrack.view.loadingDialog.DialogLoading
import com.example.fattrack.view.login.LoginActivity
import kotlinx.coroutines.launch

class ForgotActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotBinding // Declare binding
    private val forgotViewModel: ForgotPassViewModel by viewModels {
        ViewModelFactory.getInstance(application)
    }
    private lateinit var dialogLoading: DialogLoading


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize View Binding
        binding = ActivityForgotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init dialog loading
        dialogLoading = DialogLoading(this)

        // Handle window insets for edge-to-edge design
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupView()
        playAnimation()
        handleClicksButtons()
        observeViewModel()
    }


    private fun handleClicksButtons() {
        // Set click listener for the Continue button
        binding.btnSend.setOnClickListener {
            //get data
            val email = binding.emailForgot?.text.toString().trim()

            if (email.isNotEmpty()) {
                lifecycleScope.launch {
                    forgotViewModel.forgotPassword(email)
                }
            } else {
                showErrorDialog("Please enter your email!")
            }
        }

        binding.btnCancel.setOnClickListener {
            // Navigate to VerifyActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    //animation
    private fun playAnimation() {
        val title = ObjectAnimator.ofFloat(binding.titleForgot, View.ALPHA, 1f).setDuration(250)
        val logo =
            ObjectAnimator.ofFloat(binding.ivLogoForgot, View.ALPHA, 1f).setDuration(250)
        val desc =
            ObjectAnimator.ofFloat(binding.descForgot, View.ALPHA, 1f).setDuration(250)
        val edEmail =
            ObjectAnimator.ofFloat(binding.emailForgot, View.ALPHA, 1f).setDuration(250)
        val btnContinue =
            ObjectAnimator.ofFloat(binding.btnSend, View.ALPHA, 1f).setDuration(250)
        val btnCancel =
            ObjectAnimator.ofFloat(binding.btnCancel, View.ALPHA, 1f).setDuration(250)

        AnimatorSet().apply {
            playSequentially(
                logo,
                title,
                desc,
                edEmail,
                btnContinue,
                btnCancel
            )
            startDelay = 250
        }.start()
    }

    //setup View
    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }


    //Observe View Model
    private fun observeViewModel() {
        forgotViewModel.forgotPassword.observe(this) {
            val email = binding.emailForgot?.text.toString().trim()
            //go to reset password
            val intent = Intent(this, ResetPasswordActivity::class.java)
            intent.putExtra("email_field", email)
            startActivity(intent)
            finish()
        }

        forgotViewModel.isLoading.observe(this) { isLoading->
            if (isLoading) {
                dialogLoading.startLoading()
            } else {
                dialogLoading.stopLoading()
            }
        }

        forgotViewModel.errorMessages.observe(this) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                showErrorDialog(errorMessage)
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
}
