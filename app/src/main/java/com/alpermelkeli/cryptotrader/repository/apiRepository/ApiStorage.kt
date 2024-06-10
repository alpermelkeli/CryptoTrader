package com.alpermelkeli.cryptotrader.repository.apiRepository

import android.content.Context
import android.util.Log
import com.alpermelkeli.cryptotrader.repository.apiRepository.sqliteDatabase.ApiDatabaseHelper
import com.alpermelkeli.cryptotrader.repository.apiRepository.sqliteDatabase.ApiEntity

object ApiStorage {
    private const val TAG = "ApiStorage"
    private val apiItems: MutableList<ApiEntity> = mutableListOf()
    private lateinit var dbHelper: ApiDatabaseHelper

    fun initialize(context: Context) {
        dbHelper = ApiDatabaseHelper(context)
        loadApisFromDatabase()
        Log.d(TAG, "ApiStorage initialized")
    }

    fun addApiItem(apiEntity: ApiEntity) {
        if (::dbHelper.isInitialized) {
            apiItems.add(apiEntity)
            dbHelper.addApi(apiEntity)
            Log.d(TAG, "API item added: $apiEntity")
        } else {
            Log.e(TAG, "dbHelper is not initialized")
        }
    }

    fun removeApiItem(apiEntity: ApiEntity) {
        if (::dbHelper.isInitialized) {
            apiItems.remove(apiEntity)
            dbHelper.removeApi(apiEntity)
            Log.d(TAG, "API item removed: $apiEntity")
        } else {
            Log.e(TAG, "dbHelper is not initialized")
        }
    }

    fun getAllApiItems(): List<ApiEntity> {
        return apiItems
    }
    fun clearDatabase(){
        dbHelper.clearDatabase()
    }
    private fun loadApisFromDatabase() {
        if (::dbHelper.isInitialized) {
            val apis = dbHelper.getAllApis()
            apiItems.clear()
            apiItems.addAll(apis)
            Log.d(TAG, "APIs loaded from database")
        } else {
            Log.e(TAG, "dbHelper is not initialized")
        }
    }

    fun setSelectedApi(apiEntity: ApiEntity) {
        if (::dbHelper.isInitialized) {
            dbHelper.setSelectedApi(apiEntity)
            Log.d(TAG, "Selected API set: $apiEntity")
        } else {
            Log.e(TAG, "dbHelper is not initialized")
        }
    }

    fun getSelectedApi(): ApiEntity? {
        return if (::dbHelper.isInitialized) {
            val selectedApi = dbHelper.getSelectedApi()
            Log.d(TAG, "Selected API retrieved: $selectedApi")
            selectedApi
        } else {
            Log.e(TAG, "dbHelper is not initialized")
            null
        }
    }
}
