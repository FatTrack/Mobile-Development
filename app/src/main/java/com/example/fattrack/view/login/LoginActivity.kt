package com.example.fattrack.view.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.fattrack.MainActivity
import com.example.fattrack.R
import com.example.fattrack.data.ViewModelFactory
import com.example.fattrack.data.pref.AuthPreferences
import com.example.fattrack.data.pref.authSession
import com.example.fattrack.data.repositories.SessionModel
import com.example.fattrack.data.viewmodel.LoginViewModel
import com.example.fattrack.databinding.ActivityLoginBinding
import com.example.fattrack.view.forgotpassword.ForgotActivity
import com.example.fattrack.view.register.RegisterActivity
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {
    private  lateinit var binding: ActivityLoginBinding
    private lateinit var authPreferences: AuthPreferences
    private val loginViewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //init binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init preferences
        authPreferences = AuthPreferences.getInstance(this.authSession)

        //run fun
        setupAction()
        observeViewModel()
    }


    private fun setupAction() {
        binding.btnLogin.setOnClickListener() {
            //get data from edit text
            val email = binding.edLoginEmail?.text.toString().trim()
            val password = binding.edLoginPassword?.text.toString().trim()
            Log.d("RegisterViewModelTest", "Email: $email, Password: $password")

            //validations
            if (email.isEmpty() || password.isEmpty()) {
                showToast("Please fill in all fields")
            } else if (password.length < 8) {
                binding.edLoginPassword?.error = "Password must be at least 8 characters long"
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
                val email = binding.edLoginEmail?.text.toString().trim()
                val code = it.code
                val idUser = it.data?.user?.id

                if (code == 200) {
                    //set session
                    val user = idUser?.let { it1 ->
                        SessionModel(
                            idUser = it1,
                            email = email,
                            isLogin = true
                        )
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
                binding.progressBar?.visibility = View.VISIBLE
            } else {
                binding.progressBar?.visibility = View.GONE
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
            Log.d("RegisterViewModelTest", "Session User: $sessionUser")
            if (sessionUser.idUser.isNotEmpty()) {
                if (sessionUser.isLogin) {
                    showSuccessDialog(sessionUser.email)
                } else {
                    showErrorDialog("User session not found.")
                }
            } else {
                showErrorDialog("Token not found.")
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
    private fun showSuccessDialog(email: String) {
        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText("Login Success!")
            .setContentText("Welcome, $email.")
            .setConfirmClickListener { dialog ->
                dialog.dismissWithAnimation()
                // goto ke MainActivity
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            .show()
    }


    private fun showToast(message: String) {
        val toastCustom = StyleableToast.makeText(applicationContext, message, R.style.StyleableToast)
        toastCustom.show()
    }


    // to register
    fun onRegisterClick(view: View) {
        // Contoh: Navigasi ke activity registrasi
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    // to forgot pass
    fun onForgotClick(view: View) {
        // Contoh: Navigasi ke activity lupa password
        val intent = Intent(this, ForgotActivity::class.java)
        startActivity(intent)
    }
}