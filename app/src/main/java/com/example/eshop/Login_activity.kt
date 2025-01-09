package com.example.eshop

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.eshop.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class Login_activity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.loginButton.setOnClickListener {
            val login = binding.loginText.text.toString().trim()
            val password = binding.passwordText.text.toString().trim()

            binding.loginInputLayout.error = null
            binding.passwordInputLayout.error = null

            if (login.isEmpty()) {
                binding.loginInputLayout.error = "Enter Login"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                binding.passwordInputLayout.error = "Enter Password"
                return@setOnClickListener
            }

            firebaseAuth.signInWithEmailAndPassword(login, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    binding.loginInputLayout.error = "Invalid login or password"
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

    }
}
