package com.alpermelkeli.cryptotrader.ui.HomeScreen.fragments.profilefragment

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.alpermelkeli.cryptotrader.databinding.FragmentApiSettingsBinding
import com.alpermelkeli.cryptotrader.repository.apiRepository.ApiStorage
import com.alpermelkeli.cryptotrader.repository.apiRepository.sqliteDatabase.ApiEntity
import com.alpermelkeli.cryptotrader.ui.HomeScreen.fragments.profilefragment.adapter.ApiAdapter
import com.alpermelkeli.cryptotrader.R

class ApiSettingsFragment : Fragment() {
    private lateinit var binding: FragmentApiSettingsBinding
    lateinit var apiAdapter: ApiAdapter
    val apiList: MutableList<ApiEntity> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentApiSettingsBinding.inflate(inflater, container, false)

        ApiStorage.initialize(requireContext())

        setUpRecyclerView()

        binding.addApiButton.setOnClickListener {
            showAddApiDialog()
        }

        return binding.root
    }

    private fun showAddApiDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_api, null)
        val editTextExchangeMarket = dialogView.findViewById<EditText>(R.id.exchangeMarketEdittext)
        val editTextApiKey = dialogView.findViewById<EditText>(R.id.apiKeyEdittext)
        val editTextSecretKey = dialogView.findViewById<EditText>(R.id.secretKeyEdittext)
        val buttonAddApi = dialogView.findViewById<Button>(R.id.addApiButtonEdittext)

        val dialog = AlertDialog.Builder(requireContext(),R.style.TransparentBackgroundDialog)
            .setView(dialogView)
            .create()

        buttonAddApi.setOnClickListener {
            val exchangeMarket = editTextExchangeMarket.text.toString()
            val apiKey = editTextApiKey.text.toString()
            val secretKey = editTextSecretKey.text.toString()

            if (exchangeMarket.isNotBlank() && apiKey.isNotBlank() && secretKey.isNotBlank()) {
                addNewApi(ApiEntity(exchangeMarket, apiKey, secretKey))
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Lütfen doğru giriniz.",Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun setUpRecyclerView() {
        apiList.addAll(ApiStorage.getAllApiItems())
        apiAdapter = ApiAdapter(apiList,
            deleteClickListener ={apiEntity -> deleteSelectedApi(apiEntity)},
            selectClickListener = {apiEntity -> selectApi(apiEntity) }
        )
        binding.apiRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.apiRecyclerView.adapter = apiAdapter
    }

    private fun addNewApi(newApi: ApiEntity) {
        ApiStorage.addApiItem(newApi)
        apiList.add(newApi)
        apiAdapter.notifyItemInserted(apiList.size - 1)
    }
    private fun deleteSelectedApi(apiEntity: ApiEntity){
        ApiStorage.removeApiItem(apiEntity)
        apiList.remove(apiEntity)
        apiAdapter.notifyDataSetChanged()
    }
    private fun selectApi(apiEntity: ApiEntity){
        ApiStorage.setSelectedApi(apiEntity)
        Toast.makeText(context, apiEntity.apiKey.get(0) + "selected" , Toast.LENGTH_LONG).show()
    }
}
