package com.alpermelkeli.cryptotrader.ui.HomeScreen.fragments

import android.app.AlertDialog
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
import com.alpermelkeli.cryptotrader.ui.HomeScreen.fragments.adapter.TradingBotsAdapter
/**
 * A Fragment representing the home screen of the CryptoTrader app.
 * This Fragment displays a list of trading bots and allows users to add new bots.
 */
class HomeFragment : Fragment() {
    // Private properties
    private lateinit var binding: FragmentHomeBinding
    private lateinit var tradingBots: MutableList<TradingBot>
    private lateinit var adapter: TradingBotsAdapter
    private val botManagers = mutableMapOf<String, BotManager>() // Bot yönetim sınıflarını saklamak için

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        setupRecyclerView()
        setupButtonListeners()
        return binding.root
    }

    private fun setupRecyclerView() {
        tradingBots = mutableListOf()
        adapter = TradingBotsAdapter(tradingBots)
        binding.manuelBotsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.manuelBotsRecyclerView.adapter = adapter
    }

    private fun setupButtonListeners() {
        binding.newTradeBotButton.setOnClickListener {
            showAddBotDialog()
        }
    }
    /**
     * Displays an AlertDialog for adding a new trading bot.
     */
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
    /**
     * Starts a new trading bot with the specified parameters.
     *
     * @param pairName  The name of the trading pair for the bot.
     * @param threshold The trading threshold for the bot.
     * @param amount    The trading amount for the bot.
     */
    private fun startTradingBot(pairName: String, threshold: Double, amount: Double) {
        val botManager = BotManager(pairName, threshold, amount)
        botManagers[pairName] = botManager
        botManager.start()
    }
}
