package com.alpermelkeli.cryptotrader.model

import com.alpermelkeli.cryptotrader.repository.botRepository.BotService
import com.alpermelkeli.cryptotrader.repository.botRepository.ThresholdManager
import com.alpermelkeli.cryptotrader.repository.cryptoApi.Binance.BinanceAccountOperations
import com.alpermelkeli.cryptotrader.repository.cryptoApi.Binance.BinanceWebSocketManager
import com.alpermelkeli.cryptotrader.repository.cryptoApi.Binance.BinanceWebSocketManager.BinanceWebSocketListener

class BotManager(
    val id: String,
    var firstPairName: String,
    var secondPairName: String,
    var pairName: String,
    var threshold: Double,
    var amount: Double,
    var exchangeMarket: String,
    var status: String,
    var apiKey: String,
    var secretKey: String,
    var openPosition:Boolean
) {
    private val binanceAccountOperations = BinanceAccountOperations(apiKey,secretKey)
    private val thresholdManager: ThresholdManager = ThresholdManager()
    private var webSocketManager: BinanceWebSocketManager? = null

    fun start() {
        if(openPosition){
            thresholdManager.setSellThreshold(pairName, threshold)
        }
        else{
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
    fun update(amount: Double, threshold: Double) {
        this.amount = amount
        this.threshold = threshold

        if (openPosition) {
            thresholdManager.setSellThreshold(pairName, threshold)
            thresholdManager.removeBuyThreshold(pairName)
        }
        else {
            thresholdManager.setBuyThreshold(pairName, threshold)
            thresholdManager.removeSellThreshold(pairName)
        }
    }

    fun stop() {
        webSocketManager?.disconnect()
        webSocketManager = null
    }

    private fun handlePriceUpdate(currentPrice: Double) {
        println("Bot id at botManager: $id")
        val buyThreshold = thresholdManager.getBuyThreshold(pairName)
        val sellThreshold = thresholdManager.getSellThreshold(pairName)

        println("Current price of $pairName = $currentPrice")
        println("Buy threshold of $pairName = $buyThreshold")
        println("Sell threshold of $pairName = $sellThreshold")
        println("Open position of $pairName = $openPosition")
        if (!openPosition && buyThreshold != null && currentPrice > buyThreshold) {
            binanceAccountOperations.buyCoin(pairName, amount)
                .thenAccept { success: Boolean ->
                    if (success) {
                        openPosition = true
                        thresholdManager.setSellThreshold(pairName, buyThreshold)
                        thresholdManager.removeBuyThreshold(pairName)
                        println("Buy order executed successfully.")
                        BotService.sendNotification("Buy Order", "Buy order for $pairName executed successfully.")

                    } else {
                        println("Buy order failed. Please check the logs for more details.")
                        BotService.sendNotification("Buy Order Failed", "Buy order for $pairName failed. Please check the logs for more details.")


                    }
                }
                .exceptionally { ex: Throwable ->
                    println("An error occurred during buy operation: " + ex.message)
                    BotService.sendNotification("Buy Order Error", "An error occurred during buy operation for $pairName: ${ex.message}")
                    ex.printStackTrace()
                    null
                }
        }

        if (openPosition && sellThreshold != null && currentPrice < sellThreshold) {
            binanceAccountOperations.sellCoin(pairName, amount)
                .thenAccept { success: Boolean ->
                    if (success) {
                        openPosition = false
                        thresholdManager.removeSellThreshold(pairName)
                        thresholdManager.setBuyThreshold(pairName, threshold)
                        println("Sell order executed successfully.")
                        BotService.sendNotification("Sell Order", "Sell order for $pairName executed successfully.")
                    } else {
                        println("Sell order failed. Please check the logs for more details.")
                        BotService.sendNotification("Sell Order Failed", "Sell order for $pairName failed. Please check the logs for more details.")
                    }
                }
                .exceptionally { ex: Throwable ->
                    println("An error occurred during sell operation: " + ex.message)
                    BotService.sendNotification("Sell Order Error", "An error occurred during sell operation for $pairName: ${ex.message}")
                    ex.printStackTrace()
                    null
                }
        }

    }


}
