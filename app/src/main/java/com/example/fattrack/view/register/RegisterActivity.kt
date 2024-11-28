package com.example.fattrack.view.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.fattrack.R
import com.example.fattrack.data.ViewModelFactory
import com.example.fattrack.data.viewmodel.RegisterViewModel
import com.example.fattrack.databinding.ActivityRegisterBinding
import com.example.fattrack.view.login.LoginActivity
import io.github.muddz.styleabletoast.StyleableToast

@Suppress("DEPRECATION")
class RegisterActivity : AppCompatActivity() {
    private val registerViewModel: RegisterViewModel by viewModels {
        ViewModelFactory.getInstance(application)
    }
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //init binding
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //run fun
        setupAction()
        observeViewModel()
    }


    //Validations and Actions
    private fun setupAction() {
        binding.btnRegister?.setOnClickListener() {
            //get data from edit text
            val email = binding.edEmail?.text.toString().trim()
            val nama = binding.edName?.text.toString().trim()
            val password = binding.edPassword?.text.toString().trim()

            Log.d("RegisterViewModelTest", "Email: $email, Nama: $nama, Password: $password")

            //validations
            if (email.isEmpty() || nama.isEmpty() || password.isEmpty()) {
                showToast("Please fill in all fields")
            } else if (password.length < 8) {
                binding.edPassword?.error = "Password must be at least 8 characters long"
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
                val email = binding.edEmail?.text.toString().trim()
                val code = it.code

                if (code == 201) {
                    showSuccessDialog(email)
                } else {
                    val errorMessage = it.data?.message
                    showErrorDialog(errorMessage)
                }
            }?.onFailure {
                val errorMessage = it.message
                showErrorDialog(errorMessage)
            }
        }

        //loading
        registerViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBar?.visibility = View.VISIBLE
            } else {
                binding.progressBar?.visibility = View.GONE

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
            .setContentText("Register Failed : $message")
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
            .setContentText("Account Created for $email.")
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