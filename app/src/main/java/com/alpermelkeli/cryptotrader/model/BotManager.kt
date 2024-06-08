package com.alpermelkeli.cryptotrader.model

import com.alpermelkeli.cryptotrader.repository.cryptoApi.Binance.BinanceExchangeOperations
import com.alpermelkeli.cryptotrader.repository.cryptoApi.Binance.BinanceWebSocketManager
import com.alpermelkeli.cryptotrader.repository.cryptoApi.Binance.BinanceWebSocketManager.BinanceWebSocketListener
import com.alpermelkeli.cryptotrader.repository.botRepository.ThresholdManager

class BotManager(
    val id: String,
    val firstPairName: String,
    val secondPairName: String,
    val pairName: String,
    var threshold: Double,
    var amount: Double,
    val exchangeMarket: String,
    var status: String
) {
    private val exchangeOperations: BinanceExchangeOperations = BinanceExchangeOperations()
    private val thresholdManager: ThresholdManager = ThresholdManager()
    private var openPosition: Boolean = false
    private var webSocketManager: BinanceWebSocketManager? = null
    private var lastBuyPrice: Double? = null

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
    fun update(amount: Double,threshold: Double){
        if(status=="Active"){
            stop()
        }
        this.amount = amount

        this.threshold = threshold

        if(openPosition){
            thresholdManager.setSellThreshold(pairName,threshold)
        }
        else{
            thresholdManager.setBuyThreshold(pairName,threshold)
        }
        /**
         * If there is websocket listener before don't create new.
         */
        if (webSocketManager == null) {
            val listener: BinanceWebSocketListener = object : BinanceWebSocketListener() {
                override fun onPriceUpdate(price: String) {
                    val currentPrice = price.toDouble()
                    handlePriceUpdate(currentPrice)
                }
            }
            webSocketManager = BinanceWebSocketManager(listener)
        }

        webSocketManager!!.connect(pairName)
    }
    fun stop() {
        webSocketManager?.disconnect()
    }

    private fun handlePriceUpdate(currentPrice: Double) {
        val buyThreshold = thresholdManager.getBuyThreshold(pairName)
        val sellThreshold = thresholdManager.getSellThreshold(pairName)
        println("AAAAAA")
        println("Current price of $pairName = $currentPrice")
        println("Buy threshold of $pairName = $buyThreshold")
        println("Sell threshold of $pairName = $sellThreshold")
        println("Open position of $pairName = $openPosition")

        if (!openPosition && buyThreshold != null && currentPrice > buyThreshold) {

            exchangeOperations.buyCoin(pairName, amount)
            openPosition = true
            lastBuyPrice = currentPrice
            thresholdManager.setSellThreshold(pairName, buyThreshold)
            thresholdManager.removeBuyThreshold(pairName)


        }

        if (openPosition && sellThreshold != null && currentPrice < sellThreshold) {

            exchangeOperations.sellCoin(pairName, amount)
            openPosition = false
            thresholdManager.removeSellThreshold(pairName)
            thresholdManager.setBuyThreshold(pairName, threshold)

        }


        if (!openPosition && lastBuyPrice != null && currentPrice > lastBuyPrice!!) {

            exchangeOperations.buyCoin(pairName, amount)
            openPosition = true
            lastBuyPrice = currentPrice
            thresholdManager.setSellThreshold(pairName, lastBuyPrice!!)

        }
    }
}
