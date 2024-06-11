package com.alpermelkeli.cryptotrader.model

import com.alpermelkeli.cryptotrader.repository.cryptoApi.Binance.BinanceWebSocketManager
import com.alpermelkeli.cryptotrader.repository.cryptoApi.Binance.BinanceWebSocketManager.BinanceWebSocketListener
import com.alpermelkeli.cryptotrader.repository.botRepository.ThresholdManager
import com.alpermelkeli.cryptotrader.repository.cryptoApi.Binance.BinanceAccountOperations

class BotManager(
    val id: String,
    val firstPairName: String,
    val secondPairName: String,
    val pairName: String,
    var threshold: Double,
    var amount: Double,
    val exchangeMarket: String,
    var status: String,
    val apiKey: String,
    val secretKey: String
) {
    private val binanceAccountOperations = BinanceAccountOperations(apiKey,secretKey)
    private val thresholdManager: ThresholdManager = ThresholdManager()
    private var openPosition: Boolean = false
    private var webSocketManager: BinanceWebSocketManager? = null

    fun start() {
        thresholdManager.setBuyThreshold(pairName, threshold)

        val listener: BinanceWebSocketListener = object : BinanceWebSocketListener() {
            override fun onPriceUpdate(price: String) {
                val currentPrice = price.toDouble()
                handlePriceUpdate(currentPrice)
            }
        }
        webSocketManager = BinanceWebSocketManager(listener)
        webSocketManager!!.connect(pairName)
    }

    fun update(amount: Double, threshold: Double) {
        stop()
        this.amount = amount
        this.threshold = threshold

        if (openPosition) {
            thresholdManager.setSellThreshold(pairName, threshold)
        } else {
            thresholdManager.setBuyThreshold(pairName, threshold)
        }

        val listener: BinanceWebSocketListener = object : BinanceWebSocketListener() {
            override fun onPriceUpdate(price: String) {
                val currentPrice = price.toDouble()
                handlePriceUpdate(currentPrice)
            }
        }
        webSocketManager = BinanceWebSocketManager(listener)
        webSocketManager!!.connect(pairName)
    }

    fun stop() {
        webSocketManager?.disconnect()
        webSocketManager = null
    }

    private fun handlePriceUpdate(currentPrice: Double) {
        val buyThreshold = thresholdManager.getBuyThreshold(pairName)
        val sellThreshold = thresholdManager.getSellThreshold(pairName)

        println("Current price of $pairName = $currentPrice")
        println("Buy threshold of $pairName = $buyThreshold")
        println("Sell threshold of $pairName = $sellThreshold")
        println("Open position of $pairName = $openPosition")

        if (!openPosition && buyThreshold != null && currentPrice > buyThreshold) {
            binanceAccountOperations.buyCoin(pairName, amount)
            openPosition = true
            thresholdManager.setSellThreshold(pairName, buyThreshold)
            thresholdManager.removeBuyThreshold(pairName)
        }

        if (openPosition && sellThreshold != null && currentPrice < sellThreshold) {
            binanceAccountOperations.sellCoin(pairName, amount)
            openPosition = false
            thresholdManager.removeSellThreshold(pairName)
            thresholdManager.setBuyThreshold(pairName, threshold)
        }
    }
}
