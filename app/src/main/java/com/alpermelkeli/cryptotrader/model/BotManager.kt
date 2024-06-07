package com.alpermelkeli.cryptotrader.model

import com.alpermelkeli.cryptotrader.repository.cryptoApi.Binance.BinanceExchangeOperations
import com.alpermelkeli.cryptotrader.repository.cryptoApi.Binance.BinanceWebSocketManager
import com.alpermelkeli.cryptotrader.repository.cryptoApi.Binance.BinanceWebSocketManager.BinanceWebSocketListener
import com.alpermelkeli.cryptotrader.repository.cryptoApi.Binance.ThresholdManager

class BotManager(
    val id:String,
    val firstPairName: String,
    val secondPairName:String,
    val pairName: String,
    val threshold: Double,
    val amount: Double,
    val exchangeMarket: String,
    var status: String
) {
    private val exchangeOperations: BinanceExchangeOperations = BinanceExchangeOperations()
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

    fun stop() {
        webSocketManager?.disconnect()
    }

    private fun handlePriceUpdate(currentPrice: Double) {
        val buyThreshold = thresholdManager.getBuyThreshold(pairName)
        val sellThreshold = thresholdManager.getSellThreshold(pairName)

        println("Current price of $pairName = $currentPrice")
        println("Buy threshold of $pairName = $buyThreshold")
        println("Sell threshold of $pairName = $sellThreshold")
        println("Open position of $pairName = $openPosition")

        if (!openPosition && buyThreshold != null && currentPrice > buyThreshold) {
            try {
                exchangeOperations.buyCoin(pairName, amount)
                openPosition = true
                thresholdManager.setSellThreshold(pairName, buyThreshold)
                thresholdManager.removeBuyThreshold(pairName)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (openPosition && sellThreshold != null && currentPrice < sellThreshold) {
            exchangeOperations.sellCoin(pairName, amount)
            openPosition = false
            thresholdManager.removeSellThreshold(pairName)
        }
    }
}
