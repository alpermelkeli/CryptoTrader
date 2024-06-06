package com.alpermelkeli.cryptotrader.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alpermelkeli.cryptotrader.databinding.ActivityMainBinding

import com.alpermelkeli.cryptotrader.ui.LoginScreen.LoginRegister

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navigateToLoginScreen()
    }
    private fun navigateToLoginScreen(){
        var intent = Intent(this, LoginRegister::class.java)
        startActivity(intent)
        finish()
    }
}
