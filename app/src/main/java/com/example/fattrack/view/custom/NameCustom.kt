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

@SuppressLint("UseCompatLoadingForDrawables")
class NameCustom @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs), View.OnTouchListener {

    private var startIcon: Drawable =
        ContextCompat.getDrawable(context, R.drawable.ic_name) as Drawable // Default icon for name
    private var closeIcon: Drawable =
        ContextCompat.getDrawable(context, R.drawable.baseline_close_24) as Drawable // Icon for clear button

    // Initialization
    init {
        startIcon = ContextCompat.getDrawable(context, R.drawable.ic_name)
            ?: throw IllegalStateException("Drawable ic_name tidak ditemukan.")
        closeIcon = ContextCompat.getDrawable(context, R.drawable.baseline_close_24)
            ?: throw IllegalStateException("Drawable ic_close tidak ditemukan.")

        setButtonDrawables(startOfTheText = startIcon) // Set the start icon
        setBackgroundResource(R.drawable.rounded_border_inline) // Set the background

        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // When the text changes, update the right-side drawable
                updateCloseButton(s.isNotEmpty())
            }

            override fun afterTextChanged(s: Editable) {
                // Do nothing
            }
        })
    }

    private fun updateCloseButton(showClose: Boolean) {
        // Update the end drawable to show or hide the close button based on whether the text is empty
        setButtonDrawables(
            startOfTheText = startIcon,
            endOfTheText = if (showClose) closeIcon else null
        )
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
        // Check if the end drawable (the close button) is clicked
        if (compoundDrawables[2] != null) { // Check if there's an end drawable
            val drawableStart: Float
            val drawableEnd: Float
            val isDrawableClicked: Boolean

            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                drawableEnd = (closeIcon.intrinsicWidth + paddingStart).toFloat()
                isDrawableClicked = (event?.x ?: 0f) < drawableEnd
            } else {
                drawableStart = (width - paddingEnd - closeIcon.intrinsicWidth).toFloat()
                isDrawableClicked = (event?.x ?: 0f) > drawableStart
            }

            if (isDrawableClicked) {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        // Clear the text when the close button is clicked
                        setText("")
                        return true
                    }
                }
            }
        }
        return false
    }
}
