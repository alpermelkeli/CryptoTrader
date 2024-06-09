package com.alpermelkeli.cryptotrader.ui.HomeScreen.fragments.homefragment.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alpermelkeli.cryptotrader.R

class TradeViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val tradeTime: TextView = view.findViewById(R.id.trade_time)
    val tradePrice: TextView = view.findViewById(R.id.trade_price)
    val tradeAmount: TextView = view.findViewById(R.id.trade_amount)

}
