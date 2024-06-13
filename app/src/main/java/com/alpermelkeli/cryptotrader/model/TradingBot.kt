package com.alpermelkeli.cryptotrader.model

/**
 * This class represent recycler view just item not use to do something at backend except getting id from home fragment.
 */
data class TradingBot(
        val id:String,
        val imageResId: Int,
        val exchangeMarket: String,
        val status: String,
        val firstPairName: String,
        val secondPairName:String,
        val pairName: String
)
