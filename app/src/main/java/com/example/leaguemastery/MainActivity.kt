package com.example.leaguemastery

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.leaguemastery.API.Update
import com.example.leaguemastery.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navView:BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)
    }

    private fun updateSummoner(puuid:String) {
        val progressBar = binding.progressBaMain
        Update.updateSummoner(puuid, binding.root.context, progressBar)
    }
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (!Update.isBackButtonDisabled) {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Update.listViewUpdate.remove(navView)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_update -> {
                Cache.actualSummoner?.let { updateSummoner(it.puuid) }
                return true
            }


            else -> return super.onOptionsItemSelected(item)
        }
    }
}