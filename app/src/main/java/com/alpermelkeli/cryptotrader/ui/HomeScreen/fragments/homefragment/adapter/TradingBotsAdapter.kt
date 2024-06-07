package com.alpermelkeli.cryptotrader.ui.HomeScreen.fragments.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alpermelkeli.cryptotrader.R
import com.alpermelkeli.cryptotrader.model.TradingBot
import com.alpermelkeli.cryptotrader.ui.HomeScreen.fragments.homefragment.adapter.TradingBotViewHolder

class TradingBotsAdapter(
    private val tradingBots: List<TradingBot>,
    private val clickListener: (TradingBot) -> Unit
) : RecyclerView.Adapter<TradingBotViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TradingBotViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trading_bots_item, parent, false)
        return TradingBotViewHolder(view) { position ->
            clickListener(tradingBots[position])
        }
    }

    override fun onBindViewHolder(holder: TradingBotViewHolder, position: Int) {
        val bot = tradingBots[position]

        holder.coinPairName.text = bot.pairName
        holder.coinImage.setImageResource(R.drawable.btc_vector)
        holder.exchangeMarketText.text = bot.exchangeMarket
        holder.activeText.text = bot.status
    }

    override fun getItemCount(): Int = tradingBots.size
}