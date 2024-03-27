package com.example.leaguemastery

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser





class LoginActivity : AppCompatActivity() {
    lateinit var userName: EditText
    lateinit var password:EditText
    lateinit var login: Button
    lateinit var register: TextView
    lateinit var forgotPassword:TextView
    var currentUser: FirebaseUser? = null

    lateinit var mAuth: FirebaseAuth

    lateinit var loadingBar: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initializeUI();
    }
    private fun initializeUI() {
        userName = findViewById<View>(R.id.userName) as EditText
        password = findViewById<View>(R.id.pwd) as EditText
        login = findViewById<View>(R.id.login_btn) as Button
        register = findViewById<View>(R.id.registerLink) as TextView
        forgotPassword = findViewById<View>(R.id.ForgetPassword) as TextView
        mAuth = FirebaseAuth.getInstance()
        loadingBar = ProgressDialog(this)
        currentUser = mAuth.currentUser
        login.setOnClickListener{ AllowUserToLogin() }
        register.setOnClickListener { sendUserToRegister() }
        forgotPassword.setOnClickListener { resetPasswordUser() }
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }
    }

    /*
        Handles the password reset operation.
     */
    private fun resetPasswordUser() {
        val email: String = userName.text.toString().trim()
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this@LoginActivity, "Entrez votre email svp", Toast.LENGTH_SHORT)
                .show()
        } else {
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this@LoginActivity, "Mail envoyé", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }
    }

    /*
        To send user to the registration page.
     */
    private fun sendUserToRegister() {
        //When user wants to create a new account send user to Register Activity
        val registerIntent = Intent(
            this@LoginActivity,
            RegisterActivity::class.java
        )
        startActivity(registerIntent)
    }

    /*
        Handle the login process.
     */
    private fun AllowUserToLogin() {
        val email: String = userName.text.toString().trim()
        val pwd: String = password.text.toString()
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this@LoginActivity, "Entrez votre email svp", Toast.LENGTH_SHORT).show()
        }
        if (TextUtils.isEmpty(pwd)) {
            Toast.makeText(this@LoginActivity, "Entrez votre mot de passe svp", Toast.LENGTH_SHORT).show()
        } else {
            //When both email and password are available log in to the account
            //Show the progress on Progress Dialog
            loadingBar.setTitle("Connection en cours...")
            loadingBar.setMessage("Veillez patienter")
            loadingBar.show()
            mAuth.signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) //If account login successful print message and send user to main Activity
                    {
                        currentUser = mAuth.currentUser
                        sendToAcceuil()
                        Toast.makeText(this@LoginActivity, "Connecté en tant que "+currentUser!!.displayName, Toast.LENGTH_SHORT).show()
                        loadingBar.dismiss()
                    } else  //Print the error message incase of failure
                    {
                        val msg = task.exception.toString()
                        Toast.makeText(this@LoginActivity, "Error: $msg", Toast.LENGTH_SHORT).show()
                        loadingBar.dismiss()
                    }
                }
        }
    }

    override fun onStart() {
        //Check if user has already signed in if yes send to mainActivity
        //This to avoid signing in everytime you open the app.
        super.onStart()
        if(currentUser != null){
            Toast.makeText(this@LoginActivity, "Connecté en tant que "+ currentUser!!.displayName, Toast.LENGTH_SHORT)
                .show()
            sendToAcceuil()
        }
    }

    /*
        After successfull validation of username and password send user to the main activity.
     */
    private fun sendToAcceuil() {
        //This is to send user to MainActivity
        startActivity(Intent(this@LoginActivity, AcceuilRecherche::class.java))
    }
}