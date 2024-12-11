package com.example.fattrack.view.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.fattrack.R
import com.example.fattrack.data.ViewModelFactory
import com.example.fattrack.data.viewmodel.RegisterViewModel
import com.example.fattrack.databinding.ActivityRegisterBinding
import com.example.fattrack.view.loadingDialog.DialogLoading
import com.example.fattrack.view.login.LoginActivity
import io.github.muddz.styleabletoast.StyleableToast

@Suppress("DEPRECATION")
class RegisterActivity : AppCompatActivity() {
    private val registerViewModel: RegisterViewModel by viewModels {
        ViewModelFactory.getInstance(application)
    }
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var dialogLoading: DialogLoading

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //init binding
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init dialog loading
        dialogLoading = DialogLoading(this)

        //run fun
        setupAction()
        observeViewModel()
        setupView()
        playAnimation()
    }

    private fun playAnimation() {

        val title = ObjectAnimator.ofFloat(binding.titleRegister, View.ALPHA, 1f).setDuration(250)
        val logo =
            ObjectAnimator.ofFloat(binding.ivLogoRegister, View.ALPHA, 1f).setDuration(250)
        val edName =
            ObjectAnimator.ofFloat(binding.edName, View.ALPHA, 1f).setDuration(250)
        val edEmail =
            ObjectAnimator.ofFloat(binding.edEmail, View.ALPHA, 1f).setDuration(250)
        val edPassword =
            ObjectAnimator.ofFloat(binding.edPassword, View.ALPHA, 1f).setDuration(250)
        val register =
            ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(250)
        val haveAccount = ObjectAnimator.ofFloat(binding.haveAcoount, View.ALPHA, 1f).setDuration(250)
        val login = ObjectAnimator.ofFloat(binding.toLogin, View.ALPHA, 1f).setDuration(250)

        val together = AnimatorSet().apply {
            playTogether(haveAccount, login)
        }

        AnimatorSet().apply {
            playSequentially(
                title,
                logo,
                edName,
                edEmail,
                edPassword,
                register,
                together
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


    //Validations and Actions
    private fun setupAction() {
        binding.btnRegister?.setOnClickListener {
            //get data from edit text
            val email = binding.edEmail.text.toString().trim()
            val nama = binding.edName.text.toString().trim()
            val password = binding.edPassword.text.toString().trim()

            //validations
            if (email.isEmpty() || nama.isEmpty() || password.isEmpty()) {
                showToast("Please fill in all fields")
            } else if (password.length < 8) {
                binding.edPassword.error = "Password must be at least 8 characters long"
                showToast("Password must be at least 8 characters long")
            } else {
                //register with viewmodel (coroutine)
                lifecycleScope.launchWhenStarted {
                    registerViewModel.register(email, nama, password)
                }
            }
        }
    }


    //observe viewmodel
    private fun observeViewModel() {
        //response
        registerViewModel.registerResponse.observe(this) { response ->
            response?.onSuccess {
                val email = binding.edEmail.text.toString().trim()
                val code = it.code

                if (code == 201) {
                    showSuccessDialog(email)
                } else {
                    val errorMessage = it.data?.message
                    showErrorDialog(errorMessage)
                }
            }?.onFailure {
                val errorMessage = "Email already exists. Please try again with other Email!"
                showErrorDialog(errorMessage)
            }
        }

        //loading
        registerViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                dialogLoading.startLoading()
            } else {
                dialogLoading.stopLoading()

            }
        }

        //error
        registerViewModel.errorMessages.observe(this) { errorMessage ->
            if (errorMessage != null) {
                showErrorDialog(errorMessage)
            } else {
                showErrorDialog("Unknown error occurred")
            }
        }
    }


    //alert dialog error
    private fun showErrorDialog(message: String?) {
        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Failed!")
            .setContentText(message)
            .setConfirmText("OK")
            .setConfirmClickListener { dialog ->
                dialog.dismissWithAnimation()
            }
            .show()
    }


    //alert dialog success
    private fun showSuccessDialog(email: String) {
        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText("Success!")
            .setContentText("Account Created for $email. Please check Your email for verification!")
            .setConfirmClickListener { dialog ->
                dialog.dismissWithAnimation()
                finish() // Kembali ke layar sebelumnya
            }
            .show()
    }


    private fun showToast(message: String) {
        val toastCustom = StyleableToast.makeText(applicationContext, message, R.style.StyleableToast)
        toastCustom.show()
    }


    //to login
    fun onLoginClick(view: View) {
        // Contoh: Navigasi ke activity registrasi
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}