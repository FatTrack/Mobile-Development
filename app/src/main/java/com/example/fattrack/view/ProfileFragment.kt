package com.example.fattrack.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.fattrack.data.ViewModelFactory
import com.example.fattrack.data.viewmodel.ProfileViewModel
import com.example.fattrack.databinding.FragmentProfileBinding
import com.example.fattrack.view.profile.EditProfileActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private var _bindingProfile: FragmentProfileBinding? = null
    private val bindingProfile get() = _bindingProfile!!

    private val profileViewModel: ProfileViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        //init binding
        _bindingProfile = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = bindingProfile.root

        clickAllButtons()

        return root
    }


    private fun clickAllButtons() {
        //button edit profile
        bindingProfile.btnEditProfile.setOnClickListener {
            // Intent to move to NotificationsActivity
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            startActivity(intent)
        }

        //button logout
        bindingProfile.logoutButton.setOnClickListener {
            lifecycleScope.launch {
                bindingProfile.progressBar.visibility = View.VISIBLE
                delay(500) //delay 0,5s
                profileViewModel.logout()
            }
        }
    }


    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val switchTheme = bindingProfile.themeSwitch

        // Observe LiveData
        profileViewModel.getThemeApp().observe(viewLifecycleOwner) { isDarkModeActive ->
            switchTheme.isChecked = isDarkModeActive
        }

        // Save theme setting on switch change
        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            profileViewModel.saveThemeApp(isChecked)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bindingProfile = null
    }

}