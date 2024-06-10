package com.alpermelkeli.cryptotrader.repository.apiRepository.sqliteDatabase

data class ApiEntity(
    val exchangeMarket:String,
    val apiKey:String,
    val secretKey:String
)