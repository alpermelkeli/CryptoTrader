package com.alpermelkeli.cryptotrader.repository.botRepository

import BotManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.alpermelkeli.cryptotrader.R
import com.alpermelkeli.cryptotrader.ui.MainActivity

class BotService : Service() {
    private val botManagers: MutableMap<String, BotManager> = mutableMapOf()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        Log.d("BotService", "Service created")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val action = intent.action
        Log.d("BotService", "Received action: $action")
        when (action) {
            "START_BOT" -> {
                val pairName = intent.getStringExtra("pairName")!!
                val threshold = intent.getDoubleExtra("threshold", 0.0)
                val amount = intent.getDoubleExtra("amount", 0.0)
                Log.d("BotService", "Starting bot with pairName: $pairName, threshold: $threshold, amount: $amount")
                startBot(pairName, threshold, amount)
            }
            "STOP_BOT" -> {
                val pairName = intent.getStringExtra("pairName")!!
                Log.d("BotService", "Stopping bot with pairName: $pairName")
                stopBot(pairName)
            }
        }

        // Start the service in the foreground
        startForeground(1, createNotification())
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            println("Notification channel started.")
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Bot Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for Bot Service"
            }
            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        // Create the notification channel
        createNotificationChannel()

        // Initialize an empty StringBuilder to store active bot information
        val activeBotInfo = StringBuilder()

        // Append each active bot's information to the StringBuilder
        for ((pairName, _) in botManagers) {
            activeBotInfo.append(pairName).append("\n")
        }

        // Create an intent to launch the MainActivity when notification is clicked
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        // Build the notification with active bot information
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Bot Service")
            .setContentText("The bot service is running\nActive Bots:\n$activeBotInfo")
            .setSmallIcon(R.drawable.btc_vector)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun startBot(pairName: String, threshold: Double, amount: Double) {
        val botManager = BotManager(this, pairName, threshold, amount)
        botManagers[pairName] = botManager
        botManager.start()
        Log.d("BotService", "Bot started: $pairName")
    }

    private fun stopBot(pairName: String) {
        botManagers[pairName]?.stop()
        botManagers.remove(pairName)
        Log.d("BotService", "Bot stopped: $pairName")
    }

    companion object {
        const val CHANNEL_ID = "BotServiceChannel"
    }
}
