package com.alpermelkeli.cryptotrader.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alpermelkeli.cryptotrader.databinding.ActivityMainBinding
import com.alpermelkeli.cryptotrader.repository.cryptoApi.Binance.BinanceExchangeOperations
import com.alpermelkeli.cryptotrader.repository.cryptoApi.Binance.BinanceWebSocketManager
import com.alpermelkeli.cryptotrader.repository.cryptoApi.Binance.ThresholdManager

import com.alpermelkeli.cryptotrader.repository.cryptoApi.Binance.BinanceWebSocketManager.BinanceWebSocketListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val exchangeOperations = BinanceExchangeOperations()
    private val thresholdManager = ThresholdManager()
    private val symbol = "BTCUSDT"
    private val quantity = 0.0011
    private var openPosition = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val initialBuyThreshold = 100000.0
        thresholdManager.setBuyThreshold(symbol, initialBuyThreshold)

        val listener = object : BinanceWebSocketListener() {
            override fun onPriceUpdate(price: String) {
                val currentPrice = price.toDouble()
                println(currentPrice)
                handlePriceUpdate(currentPrice)
            }
        }

        val webSocketManager = BinanceWebSocketManager(listener)

        webSocketManager.connect(symbol)

        binding.btnUpdateThreshold.setOnClickListener {
            val newThreshold = binding.etThreshold.text.toString().toDoubleOrNull()
            if (newThreshold != null) {
                updateThreshold(newThreshold)
            }
        }
    }

    private fun handlePriceUpdate(currentPrice: Double) {
        val buyThreshold = thresholdManager.getBuyThreshold(symbol)
        val sellThreshold = thresholdManager.getSellThreshold(symbol)
        println(thresholdManager.toString())
        if (!openPosition && buyThreshold != null && currentPrice > buyThreshold) {
            exchangeOperations.buyCoin(symbol, quantity)
            openPosition = true
            thresholdManager.setSellThreshold(symbol, buyThreshold)
            thresholdManager.removeBuyThreshold(symbol)
        }

        if (openPosition && sellThreshold != null && currentPrice < sellThreshold) {
            exchangeOperations.sellCoin(symbol, quantity)
            openPosition = false
            thresholdManager.removeSellThreshold(symbol)
        }
    }

    private fun updateThreshold(newThreshold: Double) {
        val sellThreshold = thresholdManager.getSellThreshold(symbol)

        if (openPosition) {
            thresholdManager.setSellThreshold(symbol, newThreshold)
        } else {
            thresholdManager.setBuyThreshold(symbol, newThreshold)
        }
    }
}
