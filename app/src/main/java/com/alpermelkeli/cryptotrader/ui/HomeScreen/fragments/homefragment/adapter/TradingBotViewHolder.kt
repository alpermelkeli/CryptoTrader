package com.alpermelkeli.cryptotrader.ui.HomeScreen.fragments.homefragment.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alpermelkeli.cryptotrader.R

class TradingBotViewHolder(itemView: View,
                           private val clickListener: (Int) -> Unit,
                           private val longClickListener: (Int) ->Unit)
    : RecyclerView.ViewHolder(itemView) {
    val coinPairName: TextView = itemView.findViewById(R.id.nameText)
    val coinImage: ImageView = itemView.findViewById(R.id.coinImage)
    val exchangeMarketText: TextView = itemView.findViewById(R.id.exchangeMarketText)
    val activeText: TextView = itemView.findViewById(R.id.activeText)
    val botTypeText: TextView = itemView.findViewById(R.id.botTypeText)

    init {
        itemView.setOnClickListener {
            clickListener(adapterPosition)
        }
        itemView.setOnLongClickListener{
            longClickListener(adapterPosition)
            true
        }

    }
}
