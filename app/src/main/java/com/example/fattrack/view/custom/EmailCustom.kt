package com.example.fattrack.view.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.fattrack.R
import java.util.regex.Pattern

@SuppressLint("UseCompatLoadingForDrawables")
class EmailCustom @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs), View.OnTouchListener {

    private var clearEditTextImage: Drawable =
        ContextCompat.getDrawable(context, R.drawable.baseline_close_24) as Drawable
    private val startIcon: Drawable =
        ContextCompat.getDrawable(context, R.drawable.baseline_alternate_email_24) as Drawable
    private val emailPattern: Pattern =
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$") // Regex sederhana untuk email

    // Initialization
    init {
        setButtonDrawables(startOfTheText = startIcon) // Set ikon permanen di awal
        setBackgroundResource(R.drawable.rounded_border_inline) // Set default background

        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    showClearButton()
                    validateEmail(s.toString())
                } else {
                    hideClearButton()
                    resetErrorState()
                }
            }

            override fun afterTextChanged(s: Editable) {
                // Do nothing.
            }
        })
    }

    // Show the clear button
    private fun showClearButton() {
        setButtonDrawables(startOfTheText = startIcon, endOfTheText = clearEditTextImage)
    }

    // Hide the clear button
    private fun hideClearButton() {
        setButtonDrawables(startOfTheText = startIcon)
    }

    // Set button drawables
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

    // If clear button is touched
    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        if (compoundDrawables[2] != null) {
            val clearButtonStart: Float
            val clearButtonEnd: Float
            var isClearButtonClicked = false
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                clearButtonEnd = (clearEditTextImage.intrinsicWidth + paddingStart).toFloat()
                if (event != null && event.x < clearButtonEnd) {
                    isClearButtonClicked = true
                }
            } else {
                clearButtonStart = (width - paddingEnd - clearEditTextImage.intrinsicWidth).toFloat()
                if (event != null && event.x > clearButtonStart) {
                    isClearButtonClicked = true
                }
            }
            if (isClearButtonClicked) {
                if (event != null) {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            showClearButton()
                            return true
                        }
                        MotionEvent.ACTION_UP -> {
                            text?.clear()
                            hideClearButton()
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    // Validate email
    private fun validateEmail(email: String) {
        if (!emailPattern.matcher(email).matches()) {
            setErrorState(true)
            error = "Invalid email format"
        } else {
            setErrorState(false)
            resetErrorState()
        }
    }

    // Reset error state
    private fun resetErrorState() {
        error = null
        setErrorState(false)
    }

    // Set background based on error state
    private fun setErrorState(isError: Boolean) {
        setBackgroundResource(
            if (isError) R.drawable.rounded_border_red  
            else R.drawable.rounded_border_inline
        )
    }
}

