package com.alpermelkeli.cryptotrader.ui

import android.Manifest
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alpermelkeli.cryptotrader.databinding.ActivityMainBinding
import com.alpermelkeli.cryptotrader.repository.botRepository.BotService
import com.alpermelkeli.cryptotrader.ui.LoginScreen.LoginRegister

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val REQUEST_FOREGROUND_PERMISSION = 1
    private val REQUEST_DATA_SYNC_PERMISSION = 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkAndRequestForegroundServicePermission()
        startBotServiceIfNotRunning()
        navigateToLoginScreen()
    }

    private fun navigateToLoginScreen() {
        val intent = Intent(this, LoginRegister::class.java)
        startActivity(intent)
        finish()
    }

    private fun startBotServiceIfNotRunning() {
        if (!isServiceRunning(BotService::class.java)) {
            val intent = Intent(this, BotService::class.java).apply {
                action = "START_SERVICE"
            }
            startService(intent)
        }
        Toast.makeText(applicationContext, "Servis başlatıldı botları kurmaya başlayabilirsiniz.", Toast.LENGTH_LONG).show()
    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
    private fun checkAndRequestForegroundServicePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (applicationContext?.checkSelfPermission(Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.FOREGROUND_SERVICE), REQUEST_FOREGROUND_PERMISSION)
            } else {
                checkAndRequestDataSyncPermission()
            }
        } else {
            checkAndRequestDataSyncPermission()
        }
    }

    private fun checkAndRequestDataSyncPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (applicationContext?.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_DATA_SYNC_PERMISSION)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_FOREGROUND_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkAndRequestDataSyncPermission()
                } else {
                    AlertDialog.Builder(applicationContext)
                        .setMessage("Foreground service permission is required to start the trading bot.")
                        .setPositiveButton("OK", null)
                        .show()
                }
            }
            REQUEST_DATA_SYNC_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    AlertDialog.Builder(applicationContext)
                        .setMessage("Data sync permission is required to start the trading bot.")
                        .setPositiveButton("OK", null)
                        .show()
                }
            }
        }
    }

}
