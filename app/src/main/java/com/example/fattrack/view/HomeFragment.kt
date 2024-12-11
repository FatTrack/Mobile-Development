package com.example.fattrack.view

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.fattrack.R
import com.example.fattrack.data.ViewModelFactory
import com.example.fattrack.data.adapter.ArticleAdapter
import com.example.fattrack.data.viewmodel.ArticlesViewModel
import com.example.fattrack.data.viewmodel.HomeViewModel
import com.example.fattrack.databinding.FragmentHomeBinding
import com.example.fattrack.view.loadingDialog.DialogLoading
import com.example.fattrack.view.notifications.NotificationsActivity
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import io.github.muddz.styleabletoast.StyleableToast

class HomeFragment : Fragment() {

    // Declare the ViewBinding variable
    private lateinit var viewModelArticle: ArticlesViewModel
    private lateinit var adapter: ArticleAdapter
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var dialogLoading: DialogLoading
    private val homeViewModel by viewModels<HomeViewModel> {
        context?.let { ViewModelFactory.getInstance(it) }!!
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //loading setup
        dialogLoading = DialogLoading(requireContext())
        dialogLoading.startLoading()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using ViewBinding
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Initialize ViewModel with ViewModelFactory
        val factory = ViewModelFactory.getInstance(this.requireContext())
        viewModelArticle = ViewModelProvider(this, factory)[ArticlesViewModel::class.java]

        setupRecyclerView()
        observeViewModel()
        homeViewModel.getUserById()
        viewModelArticle.fetchArticles()
        homeViewModel.fetchHomeData()

        // Return the root view of the fragment
        return binding.root
    }


    private fun setupRecyclerView() {
        // Initialize the RecyclerView and adapter
        adapter = ArticleAdapter()
        binding.recyclerHome.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@HomeFragment.adapter
        }
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel.targetKalori.observe(viewLifecycleOwner) { targetKalori ->
            binding.targetValue.text = "/$targetKalori Kcal"
        }

        binding.btnNotifications.setOnClickListener {
            // Intent to move to NotificationsActivity
            val intent = Intent(requireContext(), NotificationsActivity::class.java)
            startActivity(intent)
        }

        homeViewModel.totalKalori.observe(viewLifecycleOwner) { totalKalori ->
            val persentase = (totalKalori?.toDouble() ?: 0.0) / 2100.0 * 100

            val color = when {
                persentase >= 90 -> ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark)
                persentase >= 70 -> ContextCompat.getColor(requireContext(), android.R.color.holo_orange_light)
                else -> ContextCompat.getColor(requireContext(), R.color.Primary)
            }

            if (persentase >= 100) {
                val colorWarning = ContextCompat.getColor(requireContext(), android.R.color.holo_red_light)
                binding.cardContainer.backgroundTintList = ColorStateList.valueOf(colorWarning)
                binding.totalKalori.setTextColor(colorWarning)
                binding.targetValue.setTextColor(colorWarning)
            }

            updatePieChart(totalKalori, color)
        }
    }


    @SuppressLint("DefaultLocale")
    private fun updatePieChart(totalKalori: Int?, color: Int) {
        val persentase = (totalKalori?.toFloat() ?: 0f) / 2100f * 100

        binding.targetPieChart.apply {
            data = PieData(PieDataSet(listOf(
                PieEntry(persentase),
                PieEntry(100 - persentase)
            ), "").apply {
                colors = listOf(color, Color.LTGRAY)
                setDrawValues(false)
            })

            description.isEnabled = false
            legend.isEnabled = false
            setHoleColor(Color.TRANSPARENT)
            setUsePercentValues(true)
            centerText = String.format("%.0f%%", persentase)
            setCenterTextColor(color)
            setCenterTextSize(12f)
            invalidate() // Refresh chart
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clean up the binding reference to avoid memory leaks
    }



    //OBSERVE
    @SuppressLint("SetTextI18n")
    private fun observeViewModel() {
        //home response
        homeViewModel.homeResponse.observe(viewLifecycleOwner) { response ->
            binding.totalKalori.text = response?.totalKalori.toString()
            binding.totalProtein.text = response?.totalProtein.toString().toFloat().toString()
            binding.totalKarbohidrat.text = response?.totalKarbohidrat.toString().toFloat().toString()
            binding.totalLemak.text = response?.totalLemak.toString().toFloat().toString()
            binding.textDate.text = response?.date.toString()
        }

        //user response
        homeViewModel.userResponse.observe(viewLifecycleOwner) { response ->
             binding.userName.text = "Halo, ${response?.nama ?: ""}"

            //image
            val photoUrl = response?.fotoProfile
            if (photoUrl != null) {
                Glide.with(this)
                    .load(photoUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.circle_background)
                    .error(R.drawable.circle_background)
                    .into(binding.ivProfile)
            } else {
                Glide.with(this)
                    .load(R.drawable.default_pp)
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.circle_background)
                    .error(R.drawable.circle_background)
                    .into(binding.ivProfile)
            }
        }


        // Observe articles LiveData
        viewModelArticle.articles.observe(viewLifecycleOwner) { articles ->
            val limitedArticles = articles.take(5)
            adapter.setDataArticles(limitedArticles)
        }

        // Observe error messages LiveData
        viewModelArticle.errorMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        //loading
        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                dialogLoading.startLoading()
            } else {
                dialogLoading.stopLoading()
            }
        }

        //error message
        homeViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                showToast(it)
            }
        }
    }

    private fun showToast(message: String) {
        val toastCustom = StyleableToast.makeText(requireContext(), message, R.style.StyleableToast)
        toastCustom.show()
    }

}
