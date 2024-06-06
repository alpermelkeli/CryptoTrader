package com.alpermelkeli.cryptotrader.ui.LoginScreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.alpermelkeli.cryptotrader.R
import com.alpermelkeli.cryptotrader.databinding.ActivityLoginRegisterBinding
import com.alpermelkeli.cryptotrader.ui.HomeScreen.HomeScreen

class LoginRegister : AppCompatActivity() {
    lateinit var binding: ActivityLoginRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            navigateToHomeScreen()
        }


    }

    private fun navigateToHomeScreen(){
        val intent = Intent(this, HomeScreen::class.java)
        startActivity(intent)
        finish()
    }
}
