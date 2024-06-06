package com.alpermelkeli.cryptotrader.ui.HomeScreen.fragments.homefragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import com.alpermelkeli.cryptotrader.R
import com.alpermelkeli.cryptotrader.databinding.FragmentHomeBinding
import com.alpermelkeli.cryptotrader.model.TradingBot
import com.alpermelkeli.cryptotrader.repository.cryptoApi.Binance.BinanceAccountOperations
import com.alpermelkeli.cryptotrader.ui.HomeScreen.fragments.adapter.TradingBotsAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var tradingBots: MutableList<TradingBot>
    private lateinit var adapter: TradingBotsAdapter
    private val botManagers = mutableMapOf<String, BotManager>()
    private val binanceAccountOperations = BinanceAccountOperations()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        setupRecyclerView()
        setupButtonListeners()
        updateAccountBalance()
        return binding.root
    }

    private fun setupRecyclerView() {
        tradingBots = mutableListOf()
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
        val pairNameEditText = dialogView.findViewById<EditText>(R.id.pairNameEditText)
        val thresholdEditText = dialogView.findViewById<EditText>(R.id.thresholdEditText)
        val amountEditText = dialogView.findViewById<EditText>(R.id.amountEditText)

        val dialog = AlertDialog.Builder(context)
            .setTitle("Yeni Bot Ekle")
            .setView(dialogView)
            .setPositiveButton("Ekle") { _, _ ->
                val pairName = pairNameEditText.text.toString()
                val threshold = thresholdEditText.text.toString().toDoubleOrNull()
                val amount = amountEditText.text.toString().toDoubleOrNull()

                if (pairName.isNotEmpty() && threshold != null && amount != null) {
                    val newBot = TradingBot(R.drawable.btc_vector, "BINANCE", "Aktif", pairName)
                    tradingBots.add(newBot)
                    adapter.notifyItemInserted(tradingBots.size - 1)
                    startTradingBot(pairName, threshold, amount)
                }
            }
            .setNegativeButton("İptal", null)
            .create()

        dialog.show()
    }

    private fun startTradingBot(pairName: String, threshold: Double, amount: Double) {
        val botManager = BotManager(requireContext(), pairName, threshold, amount)
        botManagers[pairName] = botManager
        botManager.start()
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
        intent.putExtra("pairName", tradingBot.pairName)
        startActivity(intent)
    }
}
