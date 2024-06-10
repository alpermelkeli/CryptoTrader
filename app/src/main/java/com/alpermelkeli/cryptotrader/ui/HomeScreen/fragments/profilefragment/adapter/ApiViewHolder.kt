package com.alpermelkeli.cryptotrader.ui.HomeScreen.fragments.profilefragment.adapter

import androidx.recyclerview.widget.RecyclerView
import com.alpermelkeli.cryptotrader.databinding.ItemApiBinding
import com.alpermelkeli.cryptotrader.repository.apiRepository.sqliteDatabase.ApiEntity

class ApiViewHolder(private val binding: ItemApiBinding,
    private val deleteClickListener:(Int)->Unit,
    private val selectClickListener:(Int)->Unit
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(api: ApiEntity) {
        binding.exchangeMarket.text = api.exchangeMarket
        binding.apiKey.text = api.apiKey
        binding.secretKey.text = api.secretKey
        // binding.secretKey.text = api.secretKey // Optional, if you want to display the secret key

        binding.deleteButton.setOnClickListener {
                deleteClickListener(adapterPosition)
        }

        binding.root.setOnClickListener {
            selectClickListener(adapterPosition)
        }
    }
}
