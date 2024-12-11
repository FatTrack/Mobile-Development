package com.example.fattrack.view.login

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
import com.example.fattrack.MainActivity
import com.example.fattrack.R
import com.example.fattrack.data.ViewModelFactory
import com.example.fattrack.data.pref.AuthPreferences
import com.example.fattrack.data.pref.authSession
import com.example.fattrack.data.repositories.SessionModel
import com.example.fattrack.data.viewmodel.LoginViewModel
import com.example.fattrack.data.viewmodel.MainViewModel
import com.example.fattrack.databinding.ActivityLoginBinding
import com.example.fattrack.view.forgotpassword.ForgotActivity
import com.example.fattrack.view.loadingDialog.DialogLoading
import com.example.fattrack.view.register.RegisterActivity
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {
    private var backPressedTime: Long = 0
    private lateinit var backToast: StyleableToast
    private  lateinit var binding: ActivityLoginBinding
    private lateinit var authPreferences: AuthPreferences
    private val loginViewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(application)
    }
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var dialogLoading: DialogLoading

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //init binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init preferences
        authPreferences = AuthPreferences.getInstance(this.authSession)

        //init dialog loading
        dialogLoading = DialogLoading(this)

        //run fun
        observeSession()
        setupAction()
        observeViewModel()
        setupView()
        playAnimation()
    }


    private fun observeSession() {
        viewModel.getSession().observe(this) { user ->
            if (user.isLogin && user.token.isNotEmpty()) {
                navigateToMainActivity()
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun playAnimation() {
        val title = ObjectAnimator.ofFloat(binding.title, View.ALPHA, 1f).setDuration(250)
        val logo =
            ObjectAnimator.ofFloat(binding.ivLogo, View.ALPHA, 1f).setDuration(250)
        val edLogin =
            ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(250)
        val edPassword =
            ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f).setDuration(250)
        val forgot =
            ObjectAnimator.ofFloat(binding.forgot, View.ALPHA, 1f).setDuration(250)
        val login =
            ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(250)
        val noAccount = ObjectAnimator.ofFloat(binding.noAccount, View.ALPHA, 1f).setDuration(250)
        val register = ObjectAnimator.ofFloat(binding.tvRegister, View.ALPHA, 1f).setDuration(250)

        val together = AnimatorSet().apply {
            playTogether(noAccount, register)
        }

        AnimatorSet().apply {
            playSequentially(
                title,
                logo,
                edLogin,
                edPassword,
                forgot,
                login,
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


    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            //get data from edit text
            val email = binding.edLoginEmail.text.toString().trim()
            val password = binding.edLoginPassword.text.toString().trim()

            //validations
            if (email.isEmpty() || password.isEmpty()) {
                showToast("Please fill in all fields")
            } else if (password.length < 8) {
                binding.edLoginPassword.error = "Password must be at least 8 characters long"
                showToast("Password must be at least 8 characters long")
            } else {
                //login with viewmodel (coroutine)
                lifecycleScope.launchWhenStarted {
                    loginViewModel.login(email, password)
                }
            }
        }
    }


    private fun observeViewModel() {
        //response
        loginViewModel.loginResponse.observe(this) { response ->
            response?.onSuccess {
                val email = binding.edLoginEmail.text.toString().trim()
                val code = it.code
                val idUser = it.data?.id
                val token = it.data?.token

                if (code == 200) {
                    //set session
                    val user = idUser?.let { it1 ->
                        token?.let { it2 ->
                            SessionModel(
                                idUser = it1,
                                token = it2,
                                email = email,
                                isLogin = true
                            )
                        }
                    }
                    if (user != null) {
                        loginViewModel.saveSession(user)
                        lifecycleScope.launch {
                            //check user session
                            delay(100)
                            checkUserSession()
                        }
                    }
                } else {
                    val errorMessage = it.data?.message
                    showErrorDialog(errorMessage)
                }
            }?.onFailure {
                val errorMessage = "Invalid email or password"
                showErrorDialog(errorMessage)
            }
        }

        //loading
        loginViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                dialogLoading.startLoading()
            } else {
                dialogLoading.stopLoading()
            }
        }

        //error
        loginViewModel.errorMessages.observe(this) { errorMessage ->
            if (errorMessage != null) {
                showErrorDialog(errorMessage)
            } else {
                showErrorDialog("Unknown error occurred")
            }
        }
    }


    //cek user session
    private fun checkUserSession() {
        lifecycleScope.launch {
            val sessionUser = authPreferences.getSession().first()
            if (sessionUser.idUser.isNotEmpty()) {
                if (sessionUser.isLogin) {
                    showToast("Login Success")
                } else {
                    showErrorDialog("User session not found.")
                }
            } else {
                showErrorDialog("Token not found.")
            }
        }
    }


    //back behaviour
    @Deprecated("Use OnBackPressedDispatcher instead for newer implementations.")
    override fun onBackPressed() {
        // Handle double back press to exit
        if (System.currentTimeMillis() - backPressedTime < 2000) {
            backToast.cancel()
            finishAffinity()
            super.onBackPressed()
        } else {
            backToast = StyleableToast.makeText(applicationContext, "Press again to exit", R.style.StyleableToast)
            backToast.show()
            backPressedTime = System.currentTimeMillis()
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


    private fun showToast(message: String) {
        val toastCustom = StyleableToast.makeText(applicationContext, message, R.style.StyleableToast)
        toastCustom.show()
    }


    // to register
    fun onRegisterClick(view: View) {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    // to forgot pass
    fun onForgotClick(view: View) {
        val intent = Intent(this, ForgotActivity::class.java)
        startActivity(intent)
    }
}