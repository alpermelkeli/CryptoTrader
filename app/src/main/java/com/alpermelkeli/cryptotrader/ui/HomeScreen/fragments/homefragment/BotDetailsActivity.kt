package com.alpermelkeli.cryptotrader.ui.HomeScreen.fragments.homefragment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alpermelkeli.cryptotrader.databinding.ActivityBotDetailsBinding
import com.alpermelkeli.cryptotrader.repository.botRepository.ram.BotManagerStorage

class BotDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBotDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityBotDetailsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val botManagerID = intent.getStringExtra("id")

        val botManager = botManagerID?.let { BotManagerStorage.getBotManager(it) }

        botManager?.let {
            binding.pairNameTextView.text = it.firstPairName


        }
    }
}
