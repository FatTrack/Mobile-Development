package com.example.fattrack.view.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.fattrack.data.ViewModelFactory
import com.example.fattrack.data.viewmodel.DetailViewModel
import com.example.fattrack.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    companion object {
        const val ARTICLE_ID = "article_id"
    }

    private lateinit var binding: ActivityDetailBinding
    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val articleId = intent.getStringExtra(ARTICLE_ID)

        if (articleId != null) {
            // load detail article
            loadArticleDetail(articleId)
        } else {
            Toast.makeText(this, "Article ID is missing!", Toast.LENGTH_SHORT).show()
            finish()
        }

    }

    private fun loadArticleDetail(articleId: String) {
        // fetch data
        viewModel.fetchArticleDetail(articleId)

        viewModel.articleDetail.observe(this) { article ->
            if (article != null) {
                // Update data in UI
                binding.tvArticleTitle.text = article.title
                binding.tvArticleName.text = article.author ?: "Unknown Author"
                binding.tvArticleDate.text = article.date ?: "Unknown Date"
                binding.tvEventDescription.text = article.description

                Glide.with(this@DetailActivity)
                    .load(article.image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.ivEventImage)
            } else {
                Toast.makeText(this, "Failed to load article details.", Toast.LENGTH_SHORT).show()
            }
        }

        // Amati status loading
        viewModel.isLoading.observe(this) { isLoading ->
            //loading
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // error message
        viewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}