package com.example.fattrack.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.example.fattrack.data.ViewModelFactory
import com.example.fattrack.data.viewmodel.MainViewModel
import com.example.fattrack.databinding.FragmentHomeBinding // Import ViewBinding untuk fragment_home.xml
import com.example.fattrack.view.login.LoginActivity
import com.example.fattrack.view.notifications.NotificationsActivity

class HomeFragment : Fragment() {

    // Declare the ViewBinding variable
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<MainViewModel> {
        context?.let { ViewModelFactory.getInstance(it) }!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using ViewBinding
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Return the root view of the fragment
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNotifications.setOnClickListener {
            // Intent to move to NotificationsActivity
            val intent = Intent(requireContext(), NotificationsActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clean up the binding reference to avoid memory leaks
    }
}
