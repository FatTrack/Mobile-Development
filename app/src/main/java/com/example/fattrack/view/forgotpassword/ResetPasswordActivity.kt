package com.example.fattrack.view.forgotpassword

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.fattrack.R
import com.example.fattrack.data.ViewModelFactory
import com.example.fattrack.data.viewmodel.ForgotPassViewModel
import com.example.fattrack.databinding.ActivityResetPasswordBinding
import com.example.fattrack.view.loadingDialog.DialogLoading
import com.example.fattrack.view.login.LoginActivity
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding // Declare binding
    private lateinit var dialogLoading: DialogLoading
    private val forgotViewModel: ForgotPassViewModel by viewModels {
        ViewModelFactory.getInstance(application)
    }
    private var isTimerRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize View Binding
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init dialog
        dialogLoading = DialogLoading(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //receive email
        val email = intent.getStringExtra("email_field")
        startTimer(3 * 60 * 1000) //ms
        setupView()
        playAnimation()
        handleClickButtons(email)
        observeViewModel()
    }


    private fun playAnimation() {
        val logo =
            ObjectAnimator.ofFloat(binding.ivResetPassword, View.ALPHA, 1f).setDuration(250)
        val descReset =
            ObjectAnimator.ofFloat(binding.descResetPassword, View.ALPHA, 1f).setDuration(250)
        val saveButton =
            ObjectAnimator.ofFloat(binding.btnSendAgainReset, View.ALPHA, 1f).setDuration(250)
        val cancelButton =
            ObjectAnimator.ofFloat(binding.btnSelesaiReset, View.ALPHA, 1f).setDuration(250)

        AnimatorSet().apply {
            playSequentially(
                logo,
                descReset,
                saveButton,
                cancelButton
            )
            startDelay = 250
        }.start()
    }


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


    private fun handleClickButtons(email: String?) {
        //send again
        binding.btnSendAgainReset.setOnClickListener {
            if (email != null) {
                lifecycleScope.launch {
                    forgotViewModel.forgotPassword(email)
                }
            }
        }

        //finish
        binding.btnSelesaiReset.setOnClickListener {
            // Navigate to VerifyActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }



    //observe
    private fun observeViewModel() {
        forgotViewModel.forgotPassword.observe(this) {
            //start timer again
            startTimer(3 * 60 * 1000) //ms
            showSuccessDialog()
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


    private fun startTimer(durationInMillis: Long) {
        if (isTimerRunning) return // don't start if timer running

        val grayColor = ContextCompat.getColor(this, R.color.gray)
        val primaryColor = ContextCompat.getColor(this, R.color.Primary)

        isTimerRunning = true
        binding.btnSendAgainReset.isEnabled = false
        binding.btnSendAgainReset.setBackgroundColor(grayColor)
        binding.tvTimer.visibility = View.VISIBLE

        object : CountDownTimer(durationInMillis, 1000) {
            @SuppressLint("DefaultLocale")
            override fun onTick(millisUntilFinished: Long) {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
                binding.tvTimer.text = String.format("Please wait %02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                isTimerRunning = false
                binding.btnSendAgainReset.isEnabled = true
                binding.btnSendAgainReset.setBackgroundColor(primaryColor)
                binding.tvTimer.visibility = View.GONE
            }
        }.start()
    }


    //alert dialog success
    private fun showSuccessDialog() {
        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText("Success!")
            .setContentText("Email successfully sent back!")
            .setConfirmClickListener { dialog ->
                dialog.dismissWithAnimation()
            }
            .show()
    }

    //alert dialog error
    private fun showErrorDialog(message: String?) {
        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Failed!")
            .setContentText("$message")
            .setConfirmText("OK")
            .setConfirmClickListener { dialog ->
                dialog.dismissWithAnimation()
                //back to forgot
                val intent = Intent(this, ForgotActivity::class.java)
                startActivity(intent)
                finish()
            }
            .show()
    }

}