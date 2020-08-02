package com.locationTraker.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.locationTraker.R
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    companion object {
        val TAG = "RegisterActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
    }

    fun navigateToHome(view: View) {

        val email = registerEmailText.text.toString().trim()
        val password = registerPasswordText.text.toString().trim()
        val name = registerNameEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            AlertDialog.Builder(this@RegisterActivity).setTitle("Alert")
                .setMessage("Looks like some fields are empty. Please enter all the fields and continue")
                .setPositiveButton("OK", null).create().show()
            return
        }

        loadingView.visibility = View.VISIBLE

        var mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this,
                OnCompleteListener<AuthResult?> { task ->
                    loadingView.visibility = View.GONE
                    if (task.isSuccessful) {

                        val user: FirebaseUser = mAuth.currentUser!!

                        val profileUpdates =
                            UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
//                                .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                                .build()

                        user.updateProfile(profileUpdates)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d(
                                        TAG,
                                        "User profile updated."
                                    )


                                    val db = FirebaseFirestore.getInstance()


                                    val user: MutableMap<String, Any> =
                                        HashMap()
                                    user["name"] = name


                                    db.collection("users")
                                        .add(user)
                                        .addOnSuccessListener { documentReference ->
                                            Log.d(
                                                TAG,
                                                "DocumentSnapshot added with ID: " + documentReference.id
                                            )

                                            startActivity(
                                                Intent(
                                                    this@RegisterActivity,
                                                    MainActivity::class.java
                                                )
                                            )
                                            finish()
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w(
                                                TAG,
                                                "Error adding document",
                                                e
                                            )
                                        }


                                    navigateToMainActivity()
                                }
                            }

                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")


                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(
                            TAG,
                            "createUserWithEmail:failure",
                            task.exception
                        )
                        Toast.makeText(
                            this@RegisterActivity, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    // ...
                })

    }

    fun navigateToLogin(view: View) {
        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        finish()
    }


    fun navigateToMainActivity() {
        startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
        finish()
    }
}
