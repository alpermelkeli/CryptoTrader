package com.alpermelkeli.cryptotrader.ui.HomeScreen.fragments.profilefragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alpermelkeli.cryptotrader.databinding.ItemApiBinding
import com.alpermelkeli.cryptotrader.repository.apiRepository.sqliteDatabase.ApiEntity

class ApiAdapter(private val apiList: MutableList<ApiEntity>,
    private val deleteClickListener:(ApiEntity)->Unit,
    private val selectClickListener:(ApiEntity)->Unit
) :
    RecyclerView.Adapter<ApiViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApiViewHolder {
        val binding = ItemApiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ApiViewHolder(binding,
            deleteClickListener = { position ->
                deleteClickListener(apiList[position])
            },
            selectClickListener = { position ->
                selectClickListener(apiList[position])
            }
        )
    }


    override fun onBindViewHolder(holder: ApiViewHolder, position: Int) {
        val api = apiList[position]
        holder.bind(api)
    }

    override fun getItemCount(): Int = apiList.size

}
