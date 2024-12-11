package com.example.fattrack.view.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.fattrack.R

@SuppressLint("UseCompatLoadingForDrawables")
class PasswordCustom @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs), View.OnTouchListener {

    private var hiddenPasswordDrawable: Drawable =
        ContextCompat.getDrawable(context, R.drawable.baseline_off_visibility) as Drawable
    private var visiblePasswordDrawable: Drawable =
        ContextCompat.getDrawable(context, R.drawable.baseline_visibility) as Drawable
    private var startIcon: Drawable =
        ContextCompat.getDrawable(context, R.drawable.ic_password) as Drawable

    private var isPasswordVisible: Boolean = false

    // Initialization
    init {
        // Set default font
        typeface = ResourcesCompat.getFont(context, R.font.mulish)

        hiddenPasswordDrawable = ContextCompat.getDrawable(context, R.drawable.baseline_off_visibility)
            ?: throw IllegalStateException("Drawable baseline_off_visibility tidak ditemukan.")
        visiblePasswordDrawable = ContextCompat.getDrawable(context, R.drawable.baseline_visibility)
            ?: throw IllegalStateException("Drawable baseline_visibility tidak ditemukan.")
        startIcon = ContextCompat.getDrawable(context, R.drawable.ic_password)
            ?: throw IllegalStateException("Drawable ic_password tidak ditemukan.")

        setButtonDrawables(startOfTheText = startIcon) // Set default icon
        setBackgroundResource(R.drawable.rounded_border_inline) // Set default background
        setupTogglePassword() // Initialize toggle functionality
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                validatePassword(s.toString())
            }

            override fun afterTextChanged(s: Editable) {
                // Do nothing.
            }
        })
    }
    private fun setupTogglePassword() {
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        updateToggleDrawable()
    }

    private fun updateToggleDrawable() {
        setButtonDrawables(
            startOfTheText = startIcon,
            endOfTheText = if (isPasswordVisible) visiblePasswordDrawable else hiddenPasswordDrawable
        )
        invalidate()
    }

    private fun validatePassword(password: String) {
        if (password.length < 8) {
            setErrorState(true)
            error = "Password must be at least 8 characters"
        } else {
            setErrorState(false)
            resetErrorState()
        }
    }

    private fun setErrorState(isError: Boolean) {
        setBackgroundResource(
            if (isError) R.drawable.rounded_border_red
            else R.drawable.rounded_border_inline
        )
    }

    private fun resetErrorState() {
        error = null
        setBackgroundResource(R.drawable.rounded_border_inline)
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        if (compoundDrawables[2] != null) { // Check if there's an end drawable
            val drawableStart: Float
            val drawableEnd: Float
            val isDrawableClicked: Boolean

            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                drawableEnd = (hiddenPasswordDrawable.intrinsicWidth + paddingStart).toFloat()
                isDrawableClicked = (event?.x ?: 0f) < drawableEnd
            } else {
                drawableStart = (width - paddingEnd - hiddenPasswordDrawable.intrinsicWidth).toFloat()
                isDrawableClicked = (event?.x ?: 0f) > drawableStart
            }

            if (isDrawableClicked) {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        togglePasswordVisibility() // Toggle password visibility
                        updateToggleDrawable()
                        return true
                    }
                }
            }
        }
        return false
    }


    private fun togglePasswordVisibility() {
        // Toggle the password visibility status
        isPasswordVisible = !isPasswordVisible

        // Update the input type based on the visibility status
        inputType = if (isPasswordVisible) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        // Maintain cursor position at the end
        setSelection(text?.length ?: 0)
    }


}
