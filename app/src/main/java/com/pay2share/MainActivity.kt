package com.pay2share

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.pay2share.databinding.ActivityMainBinding
import com.pay2share.ui.group.CreateGroupActivity
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
        val userName = sharedPreferences.getString("user_name", "Usuario")
        binding.appBarMain.toolbar.title = "Bienvenido, $userName"

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_change_language -> {
                    showLanguageChangeDialog()
                    true
                }
                else -> {
                    menuItem.onNavDestinationSelected(navController) || super.onOptionsItemSelected(menuItem)
                }
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.nav_home -> {
                    binding.appBarMain.toolbar.title = getString(R.string.menu_home)
                    binding.appBarMain.fab.show()
                    binding.appBarMain.fab.setOnClickListener {
                        val intent = Intent(this, CreateGroupActivity::class.java)
                        startActivity(intent)
                    }
                }
                R.id.nav_gallery -> {
                    binding.appBarMain.toolbar.title = getString(R.string.menu_gallery)
                    binding.appBarMain.fab.hide()
                }
                R.id.nav_slideshow -> {
                    binding.appBarMain.toolbar.title = getString(R.string.menu_slideshow)
                    binding.appBarMain.fab.hide()
                }
                else -> {
                    binding.appBarMain.fab.hide()
                }
            }
        }
    }

    private fun showLanguageChangeDialog() {
        val languages = arrayOf("English", "Español")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose Language")
        builder.setItems(languages) { _, which ->
            when (which) {
                0 -> setLocale("en")
                1 -> setLocale("es")
            }
        }
        builder.show()
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}