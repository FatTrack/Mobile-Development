package com.example.fattrack.data.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import androidx.core.util.Pair
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.fattrack.R
import com.example.fattrack.data.services.responses.ArticlesItem
import com.example.fattrack.data.services.responses.DataItem
import com.example.fattrack.view.detail.DetailActivity

class ArticleAdapter : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    private val articles = mutableListOf<DataItem>()
    private val filteredArticles = mutableListOf<ArticlesItem>()

    // ViewHolder for the article
    class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.tv_item_name)
        private val author: TextView = itemView.findViewById(R.id.tv_name)
        private val date: TextView = itemView.findViewById(R.id.tv_date)
        private val description: TextView = itemView.findViewById(R.id.tv_description)
        private val image: ImageView = itemView.findViewById(R.id.img_item)

        // Bind data to UI elements
        fun bind(article: DataItem) {
            title.text = article.title
            // Combine author and date with separator "|"
            val authorAndDate = "${article.author ?: "Unknown Author"} | ${article.date ?: "Unknown Date"}"
            author.text = authorAndDate
            description.text = article.description


            Glide.with(itemView.context)
                .load(article.image) // URL image
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(image)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java).apply {
                    putExtra(DetailActivity.ARTICLE_ID, article.id.toString())
                }
                // Set up transition animation
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(image, "image"),
                        Pair(title, "title"),
                        Pair(author, "author"),
                        Pair(description, "deskripsi"),
                        Pair(date, "date")
                    )

                // Start activity with animation
                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_article, parent, false)
        return ArticleViewHolder(view)
    }

    override fun getItemCount(): Int = articles.size

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(articles[position])
    }

    // Set data artikel
    @SuppressLint("NotifyDataSetChanged")
    fun setDataArticles(data: List<DataItem>) {
        articles.clear()
        articles.addAll(data)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSearchResults(data: List<ArticlesItem>) {
        filteredArticles.clear()
        filteredArticles.addAll(data)
        notifyDataSetChanged()
    }
}
