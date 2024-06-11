package com.alpermelkeli.cryptotrader.repository.botRepository

import com.alpermelkeli.cryptotrader.repository.botRepository.ram.BotManagerStorage
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.alpermelkeli.cryptotrader.R
import com.alpermelkeli.cryptotrader.ui.HomeScreen.HomeScreen
import kotlinx.coroutines.*
/**
 * This class provides foreground service for the android app. It is manage bots by fetching data with using
 * BotManagerStorage(RAM) that has BotManager objects that has management features inside.
 */
class BotService : Service() {
    private val botManagers = BotManagerStorage.getBotManagers()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
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

    private fun startBot(id: String) {
        val botManager = botManagers[id]!!
        botManager.start()
        botManager.status = "Active"
        BotManagerStorage.updateBotManager(id, botManager)
        Toast.makeText(applicationContext, "Bot started", Toast.LENGTH_LONG).show()
            Log.d("BotService", "Bot $id started")
    }


    private fun updateBot(id: String, amount: Double, threshold: Double) {
        println(botManagers.toString())
        val botManager = botManagers[id]!!
        botManager.update(amount, threshold)
        botManager.status = "Active"
        BotManagerStorage.updateBotManager(id, botManager)
        Toast.makeText(application, "Bot updated", Toast.LENGTH_LONG).show()
        Log.d("BotService", "Bot $id updated")
    }


    private fun stopBot(id: String) {
        val botManager = botManagers[id]!!
        botManager.stop()
        botManager.status = "Passive"
        BotManagerStorage.updateBotManager(id, botManager)
        Toast.makeText(applicationContext, "Bot stopped", Toast.LENGTH_LONG).show()
        Log.d("BotService", "Bot $id stopped")
    }

    companion object {
        const val CHANNEL_ID = "BotServiceChannel"
    }
}
