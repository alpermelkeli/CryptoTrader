package com.alpermelkeli.cryptotrader.ui.HomeScreen.fragments.homefragment

import com.alpermelkeli.cryptotrader.model.BotManager
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.alpermelkeli.cryptotrader.R
import com.alpermelkeli.cryptotrader.databinding.FragmentHomeBinding
import com.alpermelkeli.cryptotrader.model.TradingBot
import com.alpermelkeli.cryptotrader.repository.botRepository.BotService
import com.alpermelkeli.cryptotrader.repository.cryptoApi.Binance.BinanceAccountOperations
import com.alpermelkeli.cryptotrader.ui.HomeScreen.fragments.adapter.TradingBotsAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import com.alpermelkeli.cryptotrader.repository.botRepository.ram.BotManagerStorage

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    /**
     * It's just for the view.
     */
    private lateinit var tradingBots: MutableList<TradingBot>
    private lateinit var adapter: TradingBotsAdapter
    private lateinit var binanceAccountOperations: BinanceAccountOperations
    private val REQUEST_FOREGROUND_PERMISSION = 1
    private val REQUEST_DATA_SYNC_PERMISSION = 2
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binanceAccountOperations = BinanceAccountOperations()
        binding = FragmentHomeBinding.inflate(layoutInflater)
        BotManagerStorage.initialize(requireContext())
        setupRecyclerView()
        setupButtonListeners()
        updateAccountBalance()
        return binding.root
    }

    private fun setupRecyclerView() {
        /**
         * Firstly get data from database and insert it.
         */
        tradingBots = mutableListOf()

        for((id,botManager) in BotManagerStorage.getBotManagers()){
            tradingBots.add(TradingBot(id,R.drawable.btc_vector,"BINANCE","Active",botManager.firstPairName,botManager.secondPairName,botManager.pairName))
        }

        adapter = TradingBotsAdapter(tradingBots) { tradingBot ->
            openBotDetailsActivity(tradingBot)
        }
        binding.manuelBotsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.manuelBotsRecyclerView.adapter = adapter
    }

    private fun setupButtonListeners() {
        binding.newTradeBotButton.setOnClickListener {
            showAddBotDialog()
        }
    }

    private fun showAddBotDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_bot, null)
        val firstPairNameEditText = dialogView.findViewById<EditText>(R.id.firstPairEditText)
        val secondPairNameEditText = dialogView.findViewById<EditText>(R.id.secondPairEditText)
        val thresholdEditText = dialogView.findViewById<EditText>(R.id.thresholdEditText)
        val amountEditText = dialogView.findViewById<EditText>(R.id.amountEditText)

        val dialog = AlertDialog.Builder(context)
            .setTitle("Yeni Bot Ekle")
            .setView(dialogView)
            .setPositiveButton("Ekle") { _, _ ->
                checkAndRequestForegroundServicePermission()
                val firstPairName = firstPairNameEditText.text.toString()
                val secondPairName = secondPairNameEditText.text.toString()
                val pairName =  firstPairName + secondPairName
                val threshold = thresholdEditText.text.toString().toDoubleOrNull()
                val amount = amountEditText.text.toString().toDoubleOrNull()

                if (pairName.isNotEmpty() && threshold != null && amount != null) {
                    val id = generateBotId()
                    val newBot = TradingBot(id,R.drawable.btc_vector, "BINANCE", "Aktif", firstPairName,secondPairName,pairName)

                    tradingBots.add(newBot)

                    adapter.notifyItemInserted(tradingBots.size - 1)

                    val botManager = BotManager(id, firstPairName, secondPairName, pairName, threshold, amount)

                    BotManagerStorage.addBotManager(botManager)

                    startTradingBot(id,firstPairName,secondPairName,pairName, threshold, amount)
                }
            }
            .setNegativeButton("İptal", null)
            .create()

        dialog.show()
    }
    private fun generateBotId(): String {
        return "BOT_" + System.currentTimeMillis().toString()
    }
    private fun checkAndRequestForegroundServicePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (context?.checkSelfPermission(Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {
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
            if (context?.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_DATA_SYNC_PERMISSION)
            } else {
            }
        } else {
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
                    AlertDialog.Builder(context)
                        .setMessage("Foreground service permission is required to start the trading bot.")
                        .setPositiveButton("OK", null)
                        .show()
                }
            }
            REQUEST_DATA_SYNC_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    AlertDialog.Builder(context)
                        .setMessage("Data sync permission is required to start the trading bot.")
                        .setPositiveButton("OK", null)
                        .show()
                }
            }
        }
    }
    private fun startTradingBot(id:String,firstPairName: String,secondPairName: String,pairName: String, threshold: Double, amount: Double) {
        val intent = Intent(context, BotService::class.java).apply {
            action = "START_BOT"
            putExtra("id", id)
            putExtra("firstPairName",firstPairName)
            putExtra("secondPairName",secondPairName)
            putExtra("pairName", pairName)
            putExtra("threshold", threshold)
            putExtra("amount", amount)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().startForegroundService(intent)
        }
    }
    private fun updateAccountBalance() {
        CoroutineScope(Dispatchers.IO).launch {
            val balance = binanceAccountOperations.accountBalance
            withContext(Dispatchers.Main) {
                binding.accountBalanceUsdtText.text = "%.2f USDT".format(balance)
            }
        }
    }
    private fun openBotDetailsActivity(tradingBot: TradingBot) {
        val intent = Intent(context, BotDetailsActivity::class.java)
        intent.putExtra("id", tradingBot.id)
        intent.putExtra("firstPairName",tradingBot.firstPairName)
        intent.putExtra("secondPairName",tradingBot.secondPairName)
        startActivity(intent)
    }



}
