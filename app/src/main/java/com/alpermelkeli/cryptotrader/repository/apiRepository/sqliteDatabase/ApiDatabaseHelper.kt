package com.alpermelkeli.cryptotrader.repository.apiRepository.sqliteDatabase

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class ApiDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val TAG = "ApiDatabaseHelper"
        private const val DATABASE_NAME = "apiDatabase.db"
        private const val DATABASE_VERSION = 2
        private const val TABLE_API = "apiTable"
        private const val TABLE_SELECTED_API = "selectedApiTable"
        private const val COLUMN_EXCHANGE_MARKET = "exchangeMarket"
        private const val COLUMN_API_KEY = "apiKey"
        private const val COLUMN_SECRET_KEY = "secretKey"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createApiTable = ("CREATE TABLE $TABLE_API ("
                + "$COLUMN_EXCHANGE_MARKET TEXT,"
                + "$COLUMN_API_KEY TEXT,"
                + "$COLUMN_SECRET_KEY TEXT)")

        val createSelectedApiTable = ("CREATE TABLE $TABLE_SELECTED_API ("
                + "$COLUMN_EXCHANGE_MARKET TEXT,"
                + "$COLUMN_API_KEY TEXT,"
                + "$COLUMN_SECRET_KEY TEXT)")

        db.execSQL(createApiTable)
        db.execSQL(createSelectedApiTable)
        Log.d(TAG, "Tables created")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_API")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SELECTED_API")
        onCreate(db)
        Log.d(TAG, "Tables upgraded")
    }
    fun clearDatabase() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_API")
        db.execSQL("DELETE FROM $TABLE_SELECTED_API")
        db.close()
    }

    fun addApi(apiEntity: ApiEntity) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_EXCHANGE_MARKET, apiEntity.exchangeMarket)
        values.put(COLUMN_API_KEY, apiEntity.apiKey)
        values.put(COLUMN_SECRET_KEY, apiEntity.secretKey)
        val result = db.insert(TABLE_API, null, values)
        Log.d(TAG, "API inserted with result: $result")
        db.close()
    }

    fun removeApi(apiEntity: ApiEntity) {
        val db = this.writableDatabase
        db.delete(
            TABLE_API,
            "$COLUMN_EXCHANGE_MARKET=? AND $COLUMN_API_KEY=?",
            arrayOf(apiEntity.exchangeMarket, apiEntity.apiKey)
        )
        db.close()
    }

    fun getAllApis(): MutableList<ApiEntity> {
        val apiList: MutableList<ApiEntity> = ArrayList()
        val selectQuery = "SELECT * FROM $TABLE_API"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val exchangeMarket = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXCHANGE_MARKET))
                val apiKey = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_API_KEY))
                val secretKey = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SECRET_KEY))
                val apiEntity = ApiEntity(exchangeMarket, apiKey, secretKey)
                apiList.add(apiEntity)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return apiList
    }

    fun setSelectedApi(apiEntity: ApiEntity) {
        val db = this.writableDatabase
        db.delete(TABLE_SELECTED_API, null, null) // Eski seçili API'yi temizle
        val values = ContentValues()
        values.put(COLUMN_EXCHANGE_MARKET, apiEntity.exchangeMarket)
        values.put(COLUMN_API_KEY, apiEntity.apiKey)
        values.put(COLUMN_SECRET_KEY, apiEntity.secretKey)
        db.insert(TABLE_SELECTED_API, null, values)
        db.close()
    }

    fun getSelectedApi(): ApiEntity? {
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_SELECTED_API LIMIT 1"
        val cursor = db.rawQuery(selectQuery, null)

        var apiEntity: ApiEntity? = null
        if (cursor.moveToFirst()) {
            val exchangeMarket = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXCHANGE_MARKET))
            val apiKey = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_API_KEY))
            val secretKey = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SECRET_KEY))
            apiEntity = ApiEntity(exchangeMarket, apiKey, secretKey)
        }
        cursor.close()
        db.close()
        return apiEntity
    }
}
