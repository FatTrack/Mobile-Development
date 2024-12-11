package com.example.fattrack.view.scan

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.fattrack.MainActivity
import com.example.fattrack.R
import com.example.fattrack.data.ViewModelFactory
import com.example.fattrack.data.data.NutritionData
import com.example.fattrack.data.services.responses.NutritionalInfo
import com.example.fattrack.data.viewmodel.PredictViewModel
import com.example.fattrack.databinding.ActivityResultBinding
import com.example.fattrack.view.MyBottomSheetFragment
import com.example.fattrack.view.loadingDialog.DialogLoadingFood
import com.example.fattrack.view.loadingDialog.DialogSuccess
import com.example.fattrack.view.text.TextPredictActivity
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class ResultActivity : AppCompatActivity() {
    private val predictViewModel: PredictViewModel by viewModels {
        ViewModelFactory.getInstance(application)
    }
    private lateinit var binding: ActivityResultBinding
    private lateinit var dialogLoadingFood: DialogLoadingFood
    private lateinit var dialogSuccess: DialogSuccess

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // init binding
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get URI from Intent
        val imageUri = intent.getStringExtra("image_uri")?.let { Uri.parse(it) }

        //init
        dialogLoadingFood = DialogLoadingFood(this)
        dialogSuccess = DialogSuccess(this)

        //set URI
        if (imageUri != null) {
            binding.previewImageView.setImageURI(imageUri)
        } else {
            showToast("image not found.")
        }

        //buttons click & send file
        val imageFile = imageUri?.let { uriToFile(it, this).reduceFileImage() }
        if (imageFile != null) {
            buttonsClick(imageFile)
        }

        //observe
        observeViewModel()
    }

    //buttons click setup
    private fun buttonsClick(imageUri: File) {
        binding.btnScan.setOnClickListener {
            predictViewModel.predictImage(imageUri)
        }
        binding.btnCancel.setOnClickListener {
            finish()
        }
        binding.btnHome.setOnClickListener {
            //finish and open home
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.btnRecent.setOnClickListener {
            //to predict text
            val intent = Intent(this, TextPredictActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    @SuppressLint("SetTextI18n")
    private fun observeViewModel() {
        //observe predict
        predictViewModel.predictResponse.observe(this) { response ->
            response?.let {
                if (it.code == 200) {
                    //disabled button scan
                    binding.btnScan.isEnabled = false
                    binding.btnScan.text = "Success"

                    //open bottom and send data
                    val nutrition = mapToParcelable(it.data?.nutritionalInfo)

                    lifecycleScope.launch {
                        dialogSuccess.startSuccess(1500)
                        delay(1600)
                        displayBottomSheet(nutrition)
                    }
                } else {
                    it.data?.message?.let { it1 -> showToast(it1) }
                }
            }
        }

        //observe loading
        predictViewModel.isLoading.observe(this) { isLoading ->
            if(isLoading) {
                dialogLoadingFood.startLoading()
                binding.btnScan.isEnabled = false
            } else {
                dialogLoadingFood.stopLoading()
            }
        }

        //observe error
        predictViewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage != null) {
                binding.btnScan.text = "Failed"
                showErrorDialog(errorMessage)
            }
        }
    }


    // display bottom sheet
    private fun displayBottomSheet(nutritionalInfo: NutritionData) {
        //send data and open bottom sheet
        val bottomSheetFragment = MyBottomSheetFragment.newInstance(nutritionalInfo)
        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
    }


    //toast custom
    private fun showToast(message: String) {
        val toastCustom = StyleableToast.makeText(applicationContext, message, R.style.StyleableToast)
        toastCustom.show()
    }


    //alert dialog error
    private fun showErrorDialog(message: String?) {
        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Failed!")
            .setContentText(message)
            .setConfirmText("OK")
            .setConfirmClickListener { dialog ->
                dialog.dismissWithAnimation()
                // back to camera
                finish()
            }
            .show()
    }


    //map to parcelable
    private fun mapToParcelable(response: NutritionalInfo?): NutritionData {
        return NutritionData(
            deskripsi = response?.deskripsi,
            kalori = response?.kalori,
            karbohidrat = response?.karbohidrat.toString().toDouble(),
            lemak = response?.lemak.toString().toDouble(),
            nama = response?.nama,
            protein = response?.protein.toString().toDouble(),
            image = response?.image.toString()
        )
    }


}
