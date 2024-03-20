package com.example.leaguemastery

import android.os.Bundle
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.leaguemastery.API.Update
import com.example.leaguemastery.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isBackButtonDisabled = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)
    }

    private fun updateSummoner(puuid:String) {
        isBackButtonDisabled = true
        val progressBar:ProgressBar = binding.progressBarMain
        progressBar.visibility = View.GONE
        Update.updateSummoner(puuid)
        progressBar.visibility = View.VISIBLE
        isBackButtonDisabled = false

    }
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (!isBackButtonDisabled) {
            super.onBackPressed()
        }
    }
}