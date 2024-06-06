package com.alpermelkeli.cryptotrader.ui.HomeScreen.fragments

import com.alpermelkeli.cryptotrader.repository.cryptoApi.Binance.BinanceExchangeOperations
import com.alpermelkeli.cryptotrader.repository.cryptoApi.Binance.BinanceWebSocketManager
import com.alpermelkeli.cryptotrader.repository.cryptoApi.Binance.ThresholdManager

class BotManager(
    private val pairName: String,
    private val threshold: Double,
    private val amount: Double
) {
    private val exchangeOperations = BinanceExchangeOperations()
    private val thresholdManager = ThresholdManager()
    private var openPosition = false

    fun start() {
        thresholdManager.setBuyThreshold(pairName, threshold)

        val listener = object : BinanceWebSocketManager.BinanceWebSocketListener() {
            override fun onPriceUpdate(price: String) {
                val currentPrice = price.toDouble()
                handlePriceUpdate(currentPrice)
            }
        }

        val webSocketManager = BinanceWebSocketManager(listener)
        webSocketManager.connect(pairName)
    }

    private fun handlePriceUpdate(currentPrice: Double) {
        val buyThreshold = thresholdManager.getBuyThreshold(pairName)
        val sellThreshold = thresholdManager.getSellThreshold(pairName)

        // Print the current thresholds
        println("Current price of $pairName = $currentPrice")
        println("Buy threshold of $pairName = $buyThreshold")
        println("Sell threshold of $pairName = $sellThreshold")
        println("Open position of $pairName = $openPosition")

        if (!openPosition && buyThreshold != null && currentPrice > buyThreshold) {
            exchangeOperations.buyCoin(pairName, amount)
            openPosition = true
            thresholdManager.setSellThreshold(pairName, buyThreshold)
            thresholdManager.removeBuyThreshold(pairName)
        }

        if (openPosition && sellThreshold != null && currentPrice < sellThreshold) {
            exchangeOperations.sellCoin(pairName, amount)
            openPosition = false
            thresholdManager.removeSellThreshold(pairName)
        }
    }
}
