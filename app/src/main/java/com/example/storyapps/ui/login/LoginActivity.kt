package com.example.storyapps.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.storyapps.R
import com.example.storyapps.databinding.ActivityLoginBinding
import com.example.storyapps.preferences.UserPreferences
import com.example.storyapps.ui.ViewModelFactory
import com.example.storyapps.ui.main.MainActivity
import com.example.storyapps.ui.register.RegisterActivity
import com.example.storyapps.utils.dataStore
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel : LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.isEnabled = false
        inputValidity()
        setupViewModel()
        actionClick()
        playAnimation()

    }

    private fun inputValidity(){
        binding.edLoginEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkInputValidity()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.edLoginPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkInputValidity()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun checkInputValidity() {
        val email = binding.edLoginEmail.text.toString()
        val password = binding.edLoginPassword.text.toString()
        val emailIsValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val passwordIsValid = password.length >= 8
        binding.btnLogin.isEnabled = emailIsValid && passwordIsValid
    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(this, ViewModelFactory(UserPreferences.getInstance(dataStore)))[LoginViewModel::class.java]
        loginViewModel.getUser().observe(this) { user ->
            if (user.isLogin) {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }
        loginViewModel.isLoading.observe(this){
            showLoading(it)
        }
        loginViewModel.messageText.observe(this) { text ->
            when {
                text.contains("Invalid password") -> {
                    binding.passwordLayout.error =
                        getString(R.string.invalid_password)
                }
                text.contains("User not found") -> Snackbar.make(binding.root, getString(R.string.user_not_found), Snackbar.LENGTH_SHORT).show()
                else -> Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
            }
        }
    }
    private fun actionClick(){
        binding.toRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            loginViewModel.getUserLogin(email, password)
        }
    }
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
    private fun playAnimation() {
        val loginImage = ObjectAnimator.ofFloat(binding.ivLogin, View.ALPHA,1F).setDuration(500)
        val title = ObjectAnimator.ofFloat(binding.tvLogin, View.ALPHA, 1F).setDuration(500)
        val emailTextInput = ObjectAnimator.ofFloat(binding.emailLayout, View.ALPHA, 1F).setDuration(500)
        val passwordTextInput = ObjectAnimator.ofFloat(binding.passwordLayout, View.ALPHA, 1F).setDuration(500)
        val loginButton = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1F).setDuration(500)
        val noAccount = ObjectAnimator.ofFloat(binding.noAccount, View.ALPHA, 1F).setDuration(500)
        val toRegister = ObjectAnimator.ofFloat(binding.toRegister, View.ALPHA, 1F).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                loginImage,
                title,
                emailTextInput,
                passwordTextInput,
                loginButton,
                noAccount,
                toRegister
            )
        }.start()
    }


}