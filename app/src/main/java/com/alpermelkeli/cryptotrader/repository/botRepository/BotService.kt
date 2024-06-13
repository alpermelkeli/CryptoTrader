package com.alpermelkeli.cryptotrader.repository.botRepository

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.alpermelkeli.cryptotrader.R
import com.alpermelkeli.cryptotrader.model.BotManager
import com.alpermelkeli.cryptotrader.repository.botRepository.ram.BotManagerStorage
import com.alpermelkeli.cryptotrader.ui.HomeScreen.HomeScreen
import kotlinx.coroutines.*


/**
 * This class provides foreground service for the android app. It is manage bots by fetching data with using
 * BotManagerStorage(RAM) that has BotManager objects that has management features inside.
 */
class BotService : Service() {
    private lateinit var botManagers : MutableMap<String,BotManager>

    override fun onCreate() {
        super.onCreate()
        BotManagerStorage.initialize(applicationContext)
        botManagers = BotManagerStorage.getBotManagers()
        createNotificationChannel()
        instance = this
        Log.d("BotService", "Service created")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val action = intent.action
        val botID = intent.getStringExtra("id")!!
        val amount = intent.getDoubleExtra("amount", 0.0)
        val threshold = intent.getDoubleExtra("threshold", 0.0)

        when (action) {
            "START_BOT" -> startBot(botID)
            "UPDATE_BOT" -> updateBot(botID, amount, threshold)
            "STOP_BOT" -> stopBot(botID)
            "STOP_ALL_BOTS" -> stopAllBots()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1, createNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            startForeground(1, createNotification())
        }

        return START_STICKY
    }


    override fun onBind(intent: Intent): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Bot Service Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Channel for Bot Service" }

            val notificationManager: NotificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        createNotificationChannel()

        val activeBotInfo = StringBuilder()
        for ((id, botManager) in botManagers) {
            if (botManager.status == "Active") {
                activeBotInfo.append("${botManager.pairName}>${botManager.threshold}\n")
            }
        }

        val notificationIntent = Intent(this, HomeScreen::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("CryptoTrader")
            .setContentText("Active Bots: $activeBotInfo")
            .setSmallIcon(R.drawable.market_icon)
            .setContentIntent(pendingIntent)
            .build()
    }
    private fun sendNotificationInternal(title: String, message: String) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.btc_vector)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }

    private fun startBot(id: String) {
        val botManager = botManagers[id]!!
        botManager.start()
        botManager.status = "Active"
        BotManagerStorage.updateBotManager(id, botManager)
        Toast.makeText(applicationContext, "Bot started", Toast.LENGTH_LONG).show()
            Log.d("BotService", "Bot $id started")
        botManagers = BotManagerStorage.getBotManagers()
    }


    private fun updateBot(id: String, amount: Double, threshold: Double) {
        val botManager = botManagers[id]!!
        if(botManager.status=="Active"){
            botManager.update(amount, threshold)
            Toast.makeText(application, "Bot updated", Toast.LENGTH_LONG).show()
            Log.d("BotService", "Bot $id updated")
        }
        else{
            botManager.amount = amount
            botManager.threshold = threshold
            botManager.openPosition = false
            botManager.start()
            botManager.status = "Active"
            Toast.makeText(application, "Bot started again", Toast.LENGTH_LONG).show()
        }
        BotManagerStorage.updateBotManager(id, botManager)
        botManagers = BotManagerStorage.getBotManagers()
    }


    private fun stopBot(id: String) {
        val botManager = botManagers[id]!!
        botManager.stop()
        botManager.status = "Passive"
        BotManagerStorage.updateBotManager(id, botManager)
        Toast.makeText(applicationContext, "Bot stopped", Toast.LENGTH_LONG).show()
        Log.d("BotService", "Bot $id stopped")
        botManagers = BotManagerStorage.getBotManagers()
    }
    private fun stopAllBots() {
        for ((id, botManager) in botManagers) {
            botManager.stop()
            botManager.status = "Passive"
            BotManagerStorage.updateBotManager(id, botManager)
        }
        Log.d("BotService", "All bots stopped")
        botManagers = BotManagerStorage.getBotManagers()
        stopSelf()
    }

    companion object {
        private const val CHANNEL_ID = "BotServiceChannel"
        private lateinit var instance: BotService

        fun sendNotification(title: String, message: String) {
            instance.sendNotificationInternal(title, message)
        }
        fun stopService() {
            instance.stopAllBots()
        }

    }

}
