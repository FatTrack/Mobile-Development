package com.example.fattrack.view.loadingDialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import com.example.fattrack.R

class DialogSuccess(context: Context) : Dialog(context) {
    init {
        // Set layout dialog
        val view = LayoutInflater.from(context).inflate(R.layout.success_dialog, null)
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

    fun startSuccess(duration: Long = 1000) {
        // show dialog
        if (!isShowing) {
            show()
            // Dismiss the dialog after the specified duration
            Handler(Looper.getMainLooper()).postDelayed({
                stopLoading()
            }, duration)
        }
    }

    fun stopLoading() {
        // hidden dialog
        if (isShowing) dismiss()
    }
}