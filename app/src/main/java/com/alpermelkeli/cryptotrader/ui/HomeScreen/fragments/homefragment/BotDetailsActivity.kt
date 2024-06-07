package com.alpermelkeli.cryptotrader.ui.HomeScreen.fragments.homefragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.alpermelkeli.cryptotrader.databinding.ActivityBotDetailsBinding

import com.alpermelkeli.cryptotrader.repository.botRepository.BotService

import com.alpermelkeli.cryptotrader.repository.botRepository.ram.BotManagerStorage
class BotDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBotDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState)

        binding = ActivityBotDetailsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val botManagerID = intent.getStringExtra("id")

        val botManager = botManagerID?.let { BotManagerStorage.getBotManager(it) }

        botManager?.let {
            val id = botManager.id
            val firstPairName = botManager.firstPairName
            val secondPairName = botManager.secondPairName
            val pairName = botManager.pairName
            val amount = botManager.amount
            val threshold = botManager.threshold
            binding.botIdText.text = id
            binding.pairText.text = pairName
            binding.firstPairText.text = firstPairName
            binding.secondPairText.text = secondPairName
            binding.amountEditText.setText(amount.toString())
            binding.thresholdEditText.setText(threshold.toString())
        }

        binding.passiveButton.setOnClickListener { botManagerID?.let { stopTradingBot(it) } }
        binding.updateButton.setOnClickListener {  botManagerID?.let { updateTradingBot(it,binding.amountEditText.text.toString().toDouble(), binding.thresholdEditText.text.toString().toDouble()) }}
    }
    private fun stopTradingBot(id: String) {
        val intent = Intent(this, BotService::class.java).apply {
            action = "STOP_BOT"
            putExtra("id", id)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }
    private fun updateTradingBot(id:String,amount:Double,threshold:Double){
        val intent = Intent(this, BotService::class.java).apply {
            action = "UPDATE_BOT"
            putExtra("id", id)
            putExtra("amount",amount)
            putExtra("threshold",threshold)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }
}
