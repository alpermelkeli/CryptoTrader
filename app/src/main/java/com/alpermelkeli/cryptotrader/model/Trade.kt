package com.alpermelkeli.cryptotrader.model

data class Trade(
    val time:Long,
    val price:Double,
    val amount:Double,
    val isBuyer:Boolean
)
