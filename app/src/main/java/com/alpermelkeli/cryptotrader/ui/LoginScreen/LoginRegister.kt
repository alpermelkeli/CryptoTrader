package com.alpermelkeli.cryptotrader.ui.LoginScreen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alpermelkeli.cryptotrader.R
import com.alpermelkeli.cryptotrader.databinding.ActivityLoginRegisterBinding

class LoginRegister : AppCompatActivity() {
    lateinit var binding: ActivityLoginRegisterBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}