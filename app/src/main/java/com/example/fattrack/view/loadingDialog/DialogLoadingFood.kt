package com.example.fattrack.view.loadingDialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import com.example.fattrack.R

class DialogLoadingFood(context: Context) : Dialog(context) {
    init {
        // Set layout dialog
        val view = LayoutInflater.from(context).inflate(R.layout.loading_dialog_food, null)
        setContentView(view)

        // set property dialog window
        window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val params: WindowManager.LayoutParams = attributes
            params.gravity = Gravity.CENTER
            attributes = params
        }

        // Non-active cancelable (optional)
        setCancelable(false)
    }

    fun startLoading() {
        // show dialog
        if (!isShowing) show()
    }

    fun stopLoading() {
        // hidden dialog
        if (isShowing) dismiss()
    }
}