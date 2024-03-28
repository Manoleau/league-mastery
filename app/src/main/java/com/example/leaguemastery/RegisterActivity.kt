package com.example.leaguemastery

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest


class RegisterActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var loadingBar: ProgressDialog
    private lateinit var email:EditText
    private lateinit var userName:EditText
    private lateinit var password:EditText
    private lateinit var password1:EditText
    private lateinit var accountExists:Button
    private lateinit var register:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        initializeUI()
    }
    private fun initializeUI() {
        mAuth = FirebaseAuth.getInstance()
        email = findViewById<View>(R.id.username2) as EditText
        userName = findViewById<View>(R.id.username3) as EditText
        password = findViewById<View>(R.id.Password2) as EditText
        password1 = findViewById<View>(R.id.pass2) as EditText
        register = findViewById<View>(R.id.submit_btn) as Button
        accountExists = findViewById<View>(R.id.Already_link) as Button
        loadingBar = ProgressDialog(this)
        //When user has  an account already he should be sent to login activity.
        accountExists.setOnClickListener { sendUserToLoginActivity() }
        //When user clicks on register create a new account for user
        register.setOnClickListener { createNewAccount() }
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }
    }

    /*
        This method creates new account for new users.
     */
    private fun createNewAccount() {
        val email: String = email.text.toString().trim()
        val pwd: String = password.text.toString()
        val cmp: String = password1.text.toString()
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this@RegisterActivity, "Please enter email id", Toast.LENGTH_SHORT)
                .show()
        }
        if (TextUtils.isEmpty(pwd)) {
            Toast.makeText(this@RegisterActivity, "Please enter password", Toast.LENGTH_SHORT)
                .show()
        }
        if (pwd != cmp) {
            Toast.makeText(
                this@RegisterActivity,
                "Please Check the Conform Password!",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            //When both email and password are available create a new accountToast.makeText(RegisterActivity.this,"Please enter password",Toast.LENGTH_SHORT).show();
            //Show the progress on Progress Dialog
            loadingBar.setTitle("Creating New Account")
            loadingBar.setMessage("Please wait, we are creating new Account")
            loadingBar.setCanceledOnTouchOutside(true)
            loadingBar.show()
            mAuth.createUserWithEmailAndPassword(email, pwd)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) //If account creation successful print message and send user to Login Activity
                    {
                        val currentUser = mAuth.currentUser
                        val updateProfile = UserProfileChangeRequest.Builder()
                            .setDisplayName(userName.text.toString()).build()
                        currentUser?.updateProfile(updateProfile)
                        sendUserToLoginActivity()
                        Toast.makeText(
                            this@RegisterActivity,
                            "Account created successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        loadingBar.dismiss()
                    } else  //Print the error message incase of failure
                    {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Error: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        loadingBar.dismiss()
                    }
                }
        }
    }

    /*
        After successfull registration send user to Login page.
     */
    private fun sendUserToLoginActivity() {
        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
    }
}