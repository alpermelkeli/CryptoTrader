package com.alpermelkeli.cryptotrader.ui.HomeScreen.fragments.homefragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.alpermelkeli.cryptotrader.databinding.ActivityBotDetailsBinding
import com.alpermelkeli.cryptotrader.model.Trade
import com.alpermelkeli.cryptotrader.repository.apiRepository.ApiStorage
import com.alpermelkeli.cryptotrader.repository.botRepository.BotService
import com.alpermelkeli.cryptotrader.repository.botRepository.ram.BotManagerStorage
import com.alpermelkeli.cryptotrader.repository.cryptoApi.Binance.BinanceAccountOperations
import com.alpermelkeli.cryptotrader.ui.HomeScreen.fragments.homefragment.adapter.TradesAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CompletableFuture

class BotDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBotDetailsBinding
    private lateinit var binanceAccountOperations : BinanceAccountOperations
    private lateinit var adapter: TradesAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState)

        binding = ActivityBotDetailsBinding.inflate(layoutInflater)

        val botManagerID = intent.getStringExtra("id")

        val API_KEY = intent.getStringExtra("API_KEY")!!

        val SECRET_KEY = intent.getStringExtra("SECRET_KEY")!!

        initializeAccountOperations(API_KEY,SECRET_KEY)

        setContentView(binding.root)

        binding.tradeHistoryRecyclerView.layoutManager = LinearLayoutManager(this)


        setUpWebView()

        setUpView(botManagerID!!)

        binding.backButton.setOnClickListener { finish() }

        binding.passiveButton.setOnClickListener { botManagerID?.let { stopTradingBot(it) } }

        binding.updateButton.setOnClickListener {  botManagerID?.let { updateTradingBot(it,binding.amountEditText.text.toString().toDouble(), binding.thresholdEditText.text.toString().toDouble()) }}
    }

    private fun setUpView(botManagerID:String){
        val botManager = botManagerID.let { BotManagerStorage.getBotManager(it) }

        botManager?.let {
            val id = botManager.id
            val firstPairName = botManager.firstPairName
            val secondPairName = botManager.secondPairName
            val pairName = botManager.pairName
            val amount = botManager.amount
            val threshold = botManager.threshold
            getPairsQuantities(firstPairName,secondPairName)
            setUpTradeHistoryRecycler(pairName)
            binding.botIdText.text = id
            binding.pairText.text = pairName
            binding.firstPairText.text = firstPairName
            binding.secondPairText.text = secondPairName
            binding.amountEditText.setText(amount.toString())
            binding.thresholdEditText.setText(threshold.toString())
        }
    }
    private fun setUpTradeHistoryRecycler(pairName:String){
        CoroutineScope(Dispatchers.IO).launch {
            val tradeHistoryFuture: CompletableFuture<List<Trade>> = binanceAccountOperations.getTradeHistorySelectedCoin(pairName)
            val tradeHistory = tradeHistoryFuture.get()

            withContext(Dispatchers.Main) {
                adapter = TradesAdapter(tradeHistory)
                binding.tradeHistoryRecyclerView.adapter = adapter
            }
        }
    }
    private fun setUpWebView() {
        val webview = binding.tradingViewWebView
        webview.settings.javaScriptEnabled = true
        webview.webViewClient = WebViewClient()
        val url = "file:///android_asset/tradingview.html"
        webview.loadUrl(url)
    }
    private fun getPairsQuantities(firstPair: String, secondPair: String) {
        lifecycleScope.launch {
            val firstQuantity = withContext(Dispatchers.IO) {
                binanceAccountOperations.getSelectedCoinQuantity(firstPair)
            }
            val secondQuantity = withContext(Dispatchers.IO) {
                binanceAccountOperations.getSelectedCoinQuantity(secondPair)
            }

            binding.firstPairQuantityText.text = firstQuantity.toString()
            binding.secondPairQuantityText.text = secondQuantity.toString()
        }

    }
    private fun stopTradingBot(id: String) {
        val intent = Intent(this, BotService::class.java).apply {
            action = "STOP_BOT"
            putExtra("id", id)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }
    private fun updateTradingBot(id:String,amount:Double,threshold:Double){
        val intent = Intent(this, BotService::class.java).apply {
            action = "UPDATE_BOT"
            putExtra("id", id)
            putExtra("amount",amount)
            putExtra("threshold",threshold)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }
    private fun initializeAccountOperations(API_KEY:String,SECRET_KEY:String) {
        binanceAccountOperations = BinanceAccountOperations(API_KEY,SECRET_KEY)
        Toast.makeText(applicationContext, "INITIALIZED $API_KEY", Toast.LENGTH_LONG).show()
    }
}
