package com.stho.isee

import android.hardware.biometrics.BiometricManager
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.stho.isee.authentication.AuthenticationHandler
import com.stho.isee.databinding.ActivityMainBinding
import com.stho.isee.encryption.ui.SystemServices

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var systemServices: SystemServices
    private lateinit var viewModel: MainViewModel
    private lateinit var authenticationHandler: AuthenticationHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        systemServices = SystemServices(this)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { _ ->
            createNewEntry()
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController()
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        // Mind: top level destinations use the burger icon, while other use the back arrow icon.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
             ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        onActivityCreated()
    }

    private fun onActivityCreated() {
        viewModel.showFabLD.observe(this) { show -> onObserveShowFab(show) }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onStart() {
        super.onStart()
        confirmDeviceIsSecure()
    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun confirmDeviceIsSecure() {
        if (systemServices.isDeviceNotSecure) {
            systemServices.showDeviceSecurityAlert()
        }
    }

    private fun onObserveShowFab(show: Boolean) {
        binding.appBarMain.fab.apply {
            if (show) show() else hide()
        }
    }

    private fun createNewEntry() {
        findNavController().navigate(R.id.action_global_nav_details)
    }

    private fun findNavController(): NavController {
        // Note, when the nav_host_fragment fragment is replaced by FragmentContainerView in activity_main.xml
        // as suggested by lint, then
        //          Navigation.findNavController(this, R.id.nav_host_fragment);
        // doesn't work during onCreate() of the activity.
        // The fragment manager is still initializing.
        // See also: https://issuetracker.google.com/issues/142847973?pli=1
        val  navHostFragment: NavHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        return navHostFragment.navController
    }


}

