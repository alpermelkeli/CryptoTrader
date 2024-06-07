package com.alpermelkeli.cryptotrader.repository.botRepository.ram

import android.content.Context
import com.alpermelkeli.cryptotrader.model.BotManager
import com.alpermelkeli.cryptotrader.repository.botRepository.sqliteDatabase.BotDatabaseHelper
import com.alpermelkeli.cryptotrader.repository.botRepository.sqliteDatabase.BotEntity

object BotManagerStorage {

    private val botManagers: MutableMap<String, BotManager> = mutableMapOf()
    private lateinit var dbHelper: BotDatabaseHelper

    fun initialize(context: Context) {
        dbHelper = BotDatabaseHelper(context)
        loadBotsFromDatabase()
    }

    fun addBotManager(botManager: BotManager) {
        botManagers[botManager.id] = botManager
        dbHelper.insertBot(BotEntity(botManager.id, botManager.firstPairName,botManager.secondPairName,botManager.pairName, botManager.threshold, botManager.amount))
    }

    fun getBotManager(id: String): BotManager? {
        return botManagers[id]
    }
    fun getBotManagers(): MutableMap<String, BotManager> {
        return botManagers
    }
    fun removeBotManager(id:String){
        botManagers.remove(id)
        dbHelper.removeBotById(id)
    }

    private fun loadBotsFromDatabase() {
        val bots = dbHelper.getAllBots()
        for (bot in bots) {
            val botManager = BotManager(bot.id, bot.firstPairName,bot.secondPairName,bot.pairName, bot.threshold, bot.amount)
            botManagers[bot.id] = botManager
        }
    }
}

