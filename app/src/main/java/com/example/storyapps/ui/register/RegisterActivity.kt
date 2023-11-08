package com.example.storyapps.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.storyapps.R
import com.example.storyapps.databinding.ActivityRegisterBinding
import com.example.storyapps.ui.login.LoginActivity
import com.google.android.material.snackbar.Snackbar

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.isEnabled = false
        inputValidity()
        setupViewModel()
        actionClick()
        playAnimation()
    }
    private fun inputValidity(){
        binding.edRegisterEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkInputValidity()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.edRegisterPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkInputValidity()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.edRegisterName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkInputValidity()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun checkInputValidity() {
        val email = binding.edRegisterEmail.text.toString()
        val password = binding.edRegisterPassword.text.toString()
        val name = binding.edRegisterName.text.toString()
        val emailIsValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val passwordIsValid = password.length >= 8
        binding.btnRegister.isEnabled = emailIsValid && passwordIsValid && name.isNotEmpty()
    }
    private fun setupViewModel() {
        registerViewModel = ViewModelProvider(this)[RegisterViewModel::class.java]
        registerViewModel.messageText.observe(this) { text ->
            when {
                text.contains("taken") -> {
                    binding.emailLayout.error = getString(R.string.email_taken)
                }
                text.contains("created") ->{
                    AlertDialog.Builder(this).apply {
                        setTitle("Yeah!")
                        setMessage("Registrasi Berhasil!")
                        setPositiveButton("Lanjut") { _, _ ->
                            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        create()
                        show()
                    }
                }
                else -> Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
            }
        }
        registerViewModel.isLoading.observe(this){
            showLoading(it)
        }
    }

    private fun actionClick(){
        binding.toLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            when {
                name.isEmpty() -> {
                    binding.nameLayout.error = "Masukkan nama"
                }
                email.isEmpty() -> {
                    binding.emailLayout.error = "Masukkan email"
                }

                password.isEmpty() -> {
                    binding.passwordLayout.error = "Masukkan password"
                }
                else -> {
                    registerViewModel.getUserRegister(name, email, password)
                }
            }
        }
    }
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
    private fun playAnimation() {
        val loginImage = ObjectAnimator.ofFloat(binding.ivRegister, View.ALPHA,1F).setDuration(500)
        val title = ObjectAnimator.ofFloat(binding.tvRegister, View.ALPHA, 1F).setDuration(500)
        val nameTextInput = ObjectAnimator.ofFloat(binding.nameLayout, View.ALPHA, 1F).setDuration(500)
        val emailTextInput = ObjectAnimator.ofFloat(binding.emailLayout, View.ALPHA, 1F).setDuration(500)
        val passwordTextInput = ObjectAnimator.ofFloat(binding.passwordLayout, View.ALPHA, 1F).setDuration(500)
        val loginButton = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1F).setDuration(500)
        val haveAccount = ObjectAnimator.ofFloat(binding.hvAccount, View.ALPHA, 1F).setDuration(500)
        val toLogin = ObjectAnimator.ofFloat(binding.toLogin, View.ALPHA, 1F).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                loginImage,
                title,
                nameTextInput,
                emailTextInput,
                passwordTextInput,
                loginButton,
                haveAccount,
                toLogin
            )
        }.start()
    }

}