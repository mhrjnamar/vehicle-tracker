package com.locationTraker.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.locationTraker.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {
    companion object {
        val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        theme.applyStyle(R.style.BaseTheme_FullScreen, true)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (FirebaseAuth.getInstance().currentUser != null) {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }
    }

    fun navigateToHome(view: View) {
        val email = loginEmailEditText.text.toString().trim()
        val password = loginPasswordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            AlertDialog.Builder(this@LoginActivity).setTitle("Alert")
                .setMessage("Looks like some fields are empty. Please enter all the fields and continue")
                .setPositiveButton("OK", null).create().show()
            return
        }

        loadingView.visibility = View.VISIBLE

        var mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(
                this
            ) { task ->
                loadingView.visibility = View.GONE
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user: FirebaseUser = mAuth.currentUser!!
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        this@LoginActivity, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

    }

    fun navigateToRegister(view: View) {
        startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        finish()
    }
}