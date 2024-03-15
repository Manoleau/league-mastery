package com.example.leaguemastery

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.leaguemastery.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val summonerId = intent.getStringExtra("summonerId")
        val accountId = intent.getStringExtra("accountId")
        val puuid = intent.getStringExtra("puuid")
        val server = intent.getStringExtra("server")
        val summonerName = intent.getStringExtra("summonerName")
        val riotName = intent.getStringExtra("riotName")
        val tag = intent.getStringExtra("tag")
        val profileIconId = intent.getStringExtra("profileIconId")
        val summonerLevel = intent.getStringExtra("summonerLevel")

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}