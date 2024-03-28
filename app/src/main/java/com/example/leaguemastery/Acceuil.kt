package com.example.leaguemastery

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Acceuil : AppCompatActivity() {
    private var currentUser: FirebaseUser? = null

    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(Cache.isOnline(this@Acceuil)){
            mAuth = FirebaseAuth.getInstance()
            currentUser = mAuth.currentUser
            setContentView(R.layout.activity_acceuil)
            findViewById<Button>(R.id.goToLogin).setOnClickListener{
                startActivity(Intent(this@Acceuil, LoginActivity::class.java))
            }
            findViewById<Button>(R.id.goToRegister).setOnClickListener{
                startActivity(Intent(this@Acceuil, RegisterActivity::class.java))
            }
            findViewById<Button>(R.id.ano).setOnClickListener{
                startActivity(Intent(this@Acceuil, AcceuilRecherche::class.java))
            }
        } else {
            startActivity(Intent(this@Acceuil, AcceuilRecherche::class.java))
            Toast.makeText(this@Acceuil, "Aucune connexion à internet", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onStart() {
        super.onStart()
        if(currentUser != null){
            Toast.makeText(this@Acceuil, "Connecté en tant que "+ currentUser!!.displayName, Toast.LENGTH_SHORT)
                .show()
             startActivity(Intent(this@Acceuil, AcceuilRecherche::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}