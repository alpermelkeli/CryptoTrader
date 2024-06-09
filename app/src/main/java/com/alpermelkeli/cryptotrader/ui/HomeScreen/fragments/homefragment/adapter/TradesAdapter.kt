package com.alpermelkeli.cryptotrader.ui.HomeScreen.fragments.homefragment.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alpermelkeli.cryptotrader.R
import com.alpermelkeli.cryptotrader.model.Trade

class TradesAdapter(private val tradeList: List<Trade>) : RecyclerView.Adapter<TradeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TradeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_trade, parent, false)
        return TradeViewHolder(view)
    }

    override fun onBindViewHolder(holder: TradeViewHolder, position: Int) {
        val trade = tradeList[position]
        holder.tradeTime.text = trade.time
        holder.tradePrice.text = trade.price.toString()
        holder.tradeAmount.text = trade.amount.toString()

        if (trade.isBuyer) {
            holder.tradePrice.setTextColor(Color.GREEN)
        } else {
            holder.tradePrice.setTextColor(Color.RED)
        }
    }

    override fun getItemCount(): Int {
        return tradeList.size
    }
}
