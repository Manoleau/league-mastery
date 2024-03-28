package com.example.leaguemastery

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.leaguemastery.API.Update
import com.example.leaguemastery.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navView:BottomNavigationView
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navView = binding.navView
        navView.findViewById<TextView>(R.id.riotNameTextView)
        firebaseAuth = FirebaseAuth.getInstance()
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)
    }

    /**
     * Met à jour les informations de l'invocateur actuel.
     *
     * @param puuid L'identifiant unique de l'invocateur à mettre à jour.
     */
    private fun updateSummoner(puuid:String) {
        Update.updateSummoner(puuid, binding.root.context)
    }

    /**
     * Intercepte le bouton de retour pour empêcher le retour en arrière si le processus de mise à jour est actif.
     */
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (!Update.isBackButtonDisabled) {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_update -> {
                Cache.actualSummoner?.let { updateSummoner(it.puuid) }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}