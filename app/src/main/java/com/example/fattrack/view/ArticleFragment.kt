package com.example.fattrack.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fattrack.R
import com.example.fattrack.data.ViewModelFactory
import com.example.fattrack.data.adapter.ArticleAdapter
import com.example.fattrack.data.services.responses.DataItem
import com.example.fattrack.data.viewmodel.ArticlesViewModel
import com.example.fattrack.databinding.FragmentArticleBinding
import com.example.fattrack.view.loadingDialog.DialogLoading
import io.github.muddz.styleabletoast.StyleableToast

class ArticleFragment : Fragment() {
    private lateinit var viewModel: ArticlesViewModel
    private lateinit var adapter: ArticleAdapter
    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!
    private lateinit var dialogLoading: DialogLoading


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //loading setup
        dialogLoading = DialogLoading(requireContext())
        dialogLoading.startLoading()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticleBinding.inflate(inflater, container, false)

        // Initialize ViewModel with ViewModelFactory
        val factory = ViewModelFactory.getInstance(this.requireContext())
        viewModel = ViewModelProvider(this, factory)[ArticlesViewModel::class.java]

        setupRecyclerView()
        setupObserver()
        setupSearch()

        // Fetch data from ViewModel
        viewModel.fetchArticles()


        return binding.root
    }

    private fun setupRecyclerView() {
        // Initialize the RecyclerView and adapter
        adapter = ArticleAdapter()
        binding.rvArticle.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@ArticleFragment.adapter
        }
    }

    private fun setupObserver() {

        // Observe articles LiveData
        viewModel.articles.observe(viewLifecycleOwner) { articles ->
            adapter.setDataArticles(articles) // Update adapter with the articles list
        }

        // Observe loading state LiveData
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                dialogLoading.startLoading()
            } else {
                dialogLoading.stopLoading()
            }
        }

        // Observe error messages LiveData
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Log.d("ArticleFragment", "Error message: $message")
        }

        viewModel.searchResults.observe(viewLifecycleOwner) { searchResults ->
            val convertedResults = searchResults.map {
                DataItem(
                    title = it.title,
                    author = it.author,
                    date = it.date,
                    description = it.description,
                    image = it.image
                )
            }
            adapter.setDataArticles(convertedResults)
        }
    }



    //search binding
    private fun setupSearch() {
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    viewModel.searchArticles(newText)
                } else {
                    viewModel.fetchArticles() //if query is empty, fetch all articles
                }
                return true
            }
        })
    }

    private fun showToast(message: String) {
        val toastCustom = StyleableToast.makeText(requireContext(), message, R.style.StyleableToast)
        toastCustom.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding when the view is destroyed to avoid memory leaks
    }
}
