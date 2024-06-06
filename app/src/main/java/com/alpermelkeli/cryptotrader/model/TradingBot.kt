package com.alpermelkeli.cryptotrader.model

data class TradingBot(
    val imageResId: Int,
    val exchangeMarket: String,
    val status: String,
    val pairName:String
)
