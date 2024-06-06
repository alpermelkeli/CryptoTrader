package com.alpermelkeli.cryptotrader.ui.HomeScreen.fragments.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alpermelkeli.cryptotrader.R
import com.alpermelkeli.cryptotrader.model.TradingBot

class TradingBotsAdapter(private val tradingBots: List<TradingBot>) : RecyclerView.Adapter<TradingBotViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TradingBotViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.trading_bots_item, parent, false)
        return TradingBotViewHolder(view)
    }

    override fun onBindViewHolder(holder: TradingBotViewHolder, position: Int) {
        val tradingBot = tradingBots[position]
        // Verileri holder üzerinden bağlayın
        holder.coinPairName.text = tradingBot.pairName
        holder.coinImage.setImageResource(tradingBot.imageResId)
        holder.exchangeMarketText.text = tradingBot.exchangeMarket
        holder.activeText.text = tradingBot.status
    }

    override fun getItemCount(): Int {
        return tradingBots.size
    }
}
