package com.alpermelkeli.cryptotrader.ui.HomeScreen.fragments.homefragment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alpermelkeli.cryptotrader.R
import com.alpermelkeli.cryptotrader.databinding.ActivityBotDetailsBinding

class BotDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBotDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBotDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pairName = intent.getStringExtra("pairName")

        // Bot'un durumunu ve eşik değerini gösteren UI bileşenlerini burada ayarlayın
        binding.pairNameTextView.text = pairName
        // Diğer bileşenleri de ayarlayın ve güncelleyin
    }
}
