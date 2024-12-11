package com.example.fattrack

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.fattrack.data.NotificationReceiver
import com.example.fattrack.data.ViewModelFactory
import com.example.fattrack.data.pref.AuthPreferences
import com.example.fattrack.data.pref.ProfilePreferences
import com.example.fattrack.data.pref.authSession
import com.example.fattrack.data.pref.profileDataStore
import com.example.fattrack.data.viewmodel.MainViewModel
import com.example.fattrack.data.viewmodel.ProfileViewModel
import com.example.fattrack.databinding.ActivityMainBinding
import com.example.fattrack.databinding.PredictBottomSheetBinding
import com.example.fattrack.view.ArticleFragment
import com.example.fattrack.view.DashboardFragment
import com.example.fattrack.view.HomeFragment
import com.example.fattrack.view.ProfileFragment
import com.example.fattrack.view.login.LoginActivity
import com.example.fattrack.view.scan.CameraActivity
import com.example.fattrack.view.text.TextPredictActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.github.muddz.styleabletoast.StyleableToast

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private var backPressedTime: Long = 0
    private lateinit var backToast: StyleableToast
    private lateinit var binding: ActivityMainBinding
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var authPreferences: AuthPreferences
    private val replacedFragmentTags = mutableSetOf<String>()
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(this, REQUIRED_PERMISSION) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NotificationReceiver.createNotificationChannel(this)

        // Initialize ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize AuthPreferences
        authPreferences = AuthPreferences.getInstance(this.authSession)

        // Check and request permissions if needed
        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        // Observe session changes
        observeSession()

        // Set up UI components
        setupBottomNavigation()
        setupInsets()
        setupFab()

        // Load HomeFragment by default
        if (savedInstanceState == null) {
            binding.navView.selectedItemId = R.id.navigation_home
        }

        // Initialize ProfilePreferences and ProfileViewModel
        ProfilePreferences.getInstance(application.profileDataStore)
        profileViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(this))[ProfileViewModel::class.java]

        // Observe theme settings and apply
        profileViewModel.getThemeApp().observe(this) { isDarkModeActive ->
            AppCompatDelegate.setDefaultNightMode(
                if (isDarkModeActive) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }

    private fun observeSession() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin || user.idUser.isEmpty()) {
                // Redirect to LoginActivity if user is not logged in
                navigateToWelcomeActivity()
            }
        }
    }

    private fun navigateToWelcomeActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Close current activity to prevent returning back
    }

    // Set up BottomNavigationView behavior
    private fun setupBottomNavigation() {
        binding.navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> replaceFragment(HomeFragment())
                R.id.navigation_article -> replaceFragment(ArticleFragment())
                R.id.navigation_dashboard -> replaceFragment(DashboardFragment())
                R.id.navigation_profile -> replaceFragment(ProfileFragment())
                else -> false
            }
        }
    }

    // Apply system insets to UI elements
    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    }

    // Set up FloatingActionButton behavior
    private fun setupFab() {
        binding.fab.setOnClickListener {
            showBottomSheet()
        }
    }

    private fun showBottomSheet() {

        // Create BottomSheetDialog with a custom theme
        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)

        // Create binding for the bottom sheet layout
        val bottomSheetBinding = PredictBottomSheetBinding.inflate(layoutInflater)

        // Set up click listeners for options
        bottomSheetBinding.camera.setOnClickListener {
            bottomSheetDialog.dismiss() // Close the dialog
            startActivity(Intent(this, CameraActivity::class.java)) // Open CameraActivity
        }

        bottomSheetBinding.searchByText.setOnClickListener {
            bottomSheetDialog.dismiss() // Close the dialog
            startActivity(Intent(this, TextPredictActivity::class.java)) // Open TextPredictActivity
        }

        // Set the view using binding and show the bottom sheet
        bottomSheetDialog.setContentView(bottomSheetBinding.root)
        bottomSheetDialog.show()
    }

    // Replace current fragment and optionally add to back stack
    private fun replaceFragment(fragment: Fragment, addToBackStack: Boolean = false): Boolean {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val tag = fragment.javaClass.simpleName // Use unique class name as tag
        fragmentTransaction.replace(R.id.nav_host, fragment, tag)
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(tag) // Add to back stack if true
        }
        replacedFragmentTags.add(tag) // Save tag
        fragmentTransaction.commit()
        return true
    }

    // Handle back button presses
    @Deprecated("Use OnBackPressedDispatcher instead for newer implementations.")
    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host)
        val currentTag = currentFragment?.tag

        if (currentTag != null && replacedFragmentTags.contains(currentTag)) {
            // Handle fragment back navigation
            if (System.currentTimeMillis() - backPressedTime < 2000) {
                backToast.cancel()
                replacedFragmentTags.remove(currentTag) // Remove fragment tag
                finishAffinity()
                super.onBackPressed() // Proceed with back navigation
            } else {
                // Show toast message on first back press
                backToast = StyleableToast.makeText(applicationContext, "Press again to exit", R.style.StyleableToast)
                backToast.show()
                backPressedTime = System.currentTimeMillis() // Update back press time
            }
        } else {
            // Default back behavior
            super.onBackPressed()
        }
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}
