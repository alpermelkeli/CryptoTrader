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
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.alpermelkeli.cryptotrader.R
import com.alpermelkeli.cryptotrader.ui.MainActivity

class BotService : Service() {
    private val botManagers =  BotManagerStorage.getBotManagers()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val action = intent.action
        val botID = intent.getStringExtra("id")!!
        val amount = intent.getDoubleExtra("amount",0.0)
        val threshold = intent.getDoubleExtra("threshold",0.0)
        when (action) {

            "START_BOT" -> {
                startBot(botID)
            }
            "UPDATE_BOT"->{
                updateBot(botID,amount,threshold)
            }
            "STOP_BOT" -> {
                stopBot(botID)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1, createNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        }
        else{
            startForeground(1, createNotification())
        }
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

        // Append each active bots information to the StringBuilder

        for ((id, botManager) in botManagers) {
            if(botManager.status.equals("Active")){
                activeBotInfo.append(botManager.pairName).append(" Thresold: ${botManager.threshold}\n")
            }
        }

        // Create an intent to launch the MainActivity when notification is clicked
        val notificationIntent = Intent(this, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        // Build the notification with active bot information
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Bot Service")
            .setContentText("The bot running.\nActive Bots:\n$activeBotInfo")
            .setSmallIcon(R.drawable.btc_vector)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun startBot(id:String) {
        val botManager = botManagers[id]!!
        botManager.start()
        botManager.status ="Active"
        BotManagerStorage.updateBotManager(id,botManager)
    }
    private fun updateBot(id: String, amount:Double, threshold: Double){
        val botManager = botManagers[id]!!
        botManager.update(amount,threshold)
        BotManagerStorage.updateBotManager(id,botManager)
    }
    private fun stopBot(id: String) {
        val botManager = botManagers[id]!!
        botManager.status="Passive"
        botManager.stop()
        BotManagerStorage.updateBotManager(id,botManager)
        Toast.makeText(applicationContext,"Bot durduruldu",Toast.LENGTH_LONG).show()
    }

    companion object {
        const val CHANNEL_ID = "BotServiceChannel"
    }
}
