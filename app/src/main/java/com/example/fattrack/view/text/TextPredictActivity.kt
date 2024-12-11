package com.example.fattrack.view.text

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.fattrack.R
import com.example.fattrack.data.ViewModelFactory
import com.example.fattrack.data.data.NutritionData
import com.example.fattrack.data.services.responses.FoodDataItem
import com.example.fattrack.data.viewmodel.PredictViewModel
import com.example.fattrack.databinding.ActivityTextPredictBinding
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TextPredictActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTextPredictBinding
    private val predictViewModel: PredictViewModel by viewModels {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTextPredictBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //container chat
        val chatContainer: LinearLayout = binding.chatContainer

        observeViewModel(chatContainer)
        setupUI(chatContainer)
    }

    @SuppressLint("SetTextI18n")
    private fun observeViewModel(chatContainer: LinearLayout) {
        predictViewModel.searchResponse.observe(this) { response ->
            if (response != null) {
                val foodData = response.data?.mapNotNull { foodItem ->
                    foodItem?.let { mapToParcelable(it) }
                }

                if (!foodData.isNullOrEmpty()) {
                    //get data
                    val imageUrl = foodData[0].image
                    val desc = foodData[0].deskripsi
                    val nameFood = foodData[0].nama
                    val calorie = foodData[0].kalori
                    val carb = foodData[0].karbohidrat
                    val fat = foodData[0].lemak
                    val protein = foodData[0].protein

                    //message
                    val message1 = """
                        Tentu, yang kamu cari adalah <b>${nameFood?.let { capitalizeWords(it) }}</b>.
                        <br> $desc
                        """.trimIndent()
                    val message2  = """
                            <b>${nameFood?.let { capitalizeWords(it) }}</b> memiliki Nutrisi sebagai berikut:
                            <br>- <b>Protein</b>: $protein g
                            <br>- <b>Kalori</b>: $calorie kcal
                            <br>- <b>Karbohidrat</b>: $carb g
                            <br>- <b>Lemak</b>: $fat g
                        """.trimIndent()

                    //send response chat
                    binding.chatbotStatus.text = "Online"
                    if (imageUrl != null) {
                        addBubbleImage(chatContainer, imageUrl)
                    }
                    lifecycleScope.launch {
                        delay(300)
                        binding.chatbotStatus.text = "Typing..."
                        delay(800)
                        binding.chatbotStatus.text = "Online"
                        addBubbleChat(chatContainer, message1, false)
                        delay(300)
                        binding.chatbotStatus.text = "Typing..."
                        delay(800)
                        addBubbleChat(chatContainer, message2, false)
                        binding.chatbotStatus.text = "Online"
                    }
                } else {
                    addBubbleChat(chatContainer, "Maaf, kami tidak dapat menemukan makanan yang sesuai.", false)
                }
            } else {
                Log.d("TAG", "Response is null.")
            }
        }

        //loading
        predictViewModel.isLoading.observe(this) { isLoading ->
            binding.chatbotStatus.text = if (isLoading) "Typing..." else "Online"
        }

        //error message
        predictViewModel.errorMessage.observe(this) { errorMessage ->
            addBubbleChat(chatContainer, "Maaf, kami gagal mencari data makanan yang anda inginkan", false)
        }
    }


    //setup ui
    @SuppressLint("SetTextI18n")
    private fun setupUI(chatContainer: LinearLayout) {
        binding.sendButton.setOnClickListener {
            //get message
            val message = binding.inputMessage.text.toString().trim()

            if (message.isNotEmpty()) {
                //send message
                addBubbleChat(chatContainer, message, true)
                //clear text
                binding.inputMessage.text.clear()
                //response with delay
                binding.chatbotStatus.text = "Typing..."
                lifecycleScope.launch {
                    delay(1000)
                    predictViewModel.searchFood(message)
                }
            } else {
                showToast("Please enter a food name.")
            }
        }
    }


    //setup bubble chat
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun addBubbleChat(container: LinearLayout, message: String, isUser: Boolean) {
        // create bubble chat
        val bubble = TextView(this).apply {
            text = message.toHtmlSpanned()
            textSize = 16f
            setTextColor(if (isUser) android.graphics.Color.WHITE else android.graphics.Color.BLACK)
            setPadding(24, 20, 24, 20)
            background = getDrawable(if (isUser) R.drawable.chat_bubble_user else R.drawable.chat_bubble_other)
            minWidth = 400
            maxWidth = 800
            maxLines = 10
        }

        // layout property
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = if (isUser) Gravity.END else Gravity.START
            setMargins(8, 8, 8, 8)
        }

        // add bubble to container
        bubble.layoutParams = params
        container.addView(bubble)

        // auto scroll down
        val scrollView = container.parent as? ScrollView
        scrollView?.post { scrollView.fullScroll(View.FOCUS_DOWN) }
    }


    //bubble image
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun addBubbleImage(container: LinearLayout, imageUrl: String) {
        Log.d("PredictImageTest", "image : $imageUrl")
        // image view in Linear
        val imageBubble = ImageView(this).apply {
            background = getDrawable(R.drawable.chat_bubble_other)

            // Set layout params for ImageView
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                //set padding
                setPadding(12,12,12,12)
                setMargins(8,8,8,8)
                gravity = Gravity.START
            }
            // image set
            scaleType = ImageView.ScaleType.CENTER_CROP
            adjustViewBounds = true
            maxWidth = 900
        }


        // glide image url
        Glide.with(this)
            .load(imageUrl)
            .apply(RequestOptions().transform(RoundedCorners(70)))
            .into(imageBubble)

        // add to container
        container.addView(imageBubble)
        // auto scroll down
        val scrollView = container.parent as? ScrollView
        scrollView?.post {
            scrollView.smoothScrollTo(0, container.height)
        }
    }



    //Mapping
    private fun mapToParcelable(response: FoodDataItem?): NutritionData {
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

    //toast
    private fun showToast(message: String) {
        val toastCustom = StyleableToast.makeText(applicationContext, message, R.style.StyleableToast)
        toastCustom.show()
    }


    //html parse
    private fun String.toHtmlSpanned(): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(this)
        }
    }


    //capitalize format
    private fun capitalizeWords(input: String): String {
        return input.split(" ")
            .joinToString(" ") { word ->
                word.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase() else it.toString()
                }
            }
    }

}
