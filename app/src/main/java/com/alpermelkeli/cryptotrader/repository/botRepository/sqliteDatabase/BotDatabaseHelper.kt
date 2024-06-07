package com.alpermelkeli.cryptotrader.repository.botRepository.sqliteDatabase

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BotDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "bots.db"
        private const val DATABASE_VERSION = 2  // Versiyon güncellendi

        private const val TABLE_NAME = "bots"
        private const val COLUMN_ID = "id"
        private const val COLUMN_FIRST_PAIR_NAME = "firstPairName"
        private const val COLUMN_SECOND_PAIR_NAME = "secondPairName"
        private const val COLUMN_PAIR_NAME = "pairName"
        private const val COLUMN_THRESHOLD = "threshold"
        private const val COLUMN_AMOUNT = "amount"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID TEXT PRIMARY KEY,
                $COLUMN_FIRST_PAIR_NAME TEXT,
                $COLUMN_SECOND_PAIR_NAME TEXT,
                $COLUMN_PAIR_NAME TEXT,
                $COLUMN_THRESHOLD REAL,
                $COLUMN_AMOUNT REAL
            )
        """
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {  // Version control to add new columns
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_FIRST_PAIR_NAME TEXT")
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_SECOND_PAIR_NAME TEXT")
        }
    }

    fun insertBot(bot: BotEntity) {
        val db = writableDatabase
        val insertQuery = """
            INSERT OR REPLACE INTO $TABLE_NAME ($COLUMN_ID, $COLUMN_FIRST_PAIR_NAME, $COLUMN_SECOND_PAIR_NAME, $COLUMN_PAIR_NAME, $COLUMN_THRESHOLD, $COLUMN_AMOUNT)
            VALUES ('${bot.id}', '${bot.firstPairName}', '${bot.secondPairName}', '${bot.pairName}', ${bot.threshold}, ${bot.amount})
        """
        db.execSQL(insertQuery)
        db.close()
    }

    fun getAllBots(): List<BotEntity> {
        val bots = mutableListOf<BotEntity>()
        val db = readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val firstPairName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_PAIR_NAME))
                val secondPairName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SECOND_PAIR_NAME))
                val pairName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PAIR_NAME))
                val threshold = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_THRESHOLD))
                val amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT))
                val bot = BotEntity(id, firstPairName, secondPairName, pairName, threshold, amount)
                bots.add(bot)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return bots
    }

    fun getBotById(id: String): BotEntity? {
        val db = readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = '$id'"
        val cursor = db.rawQuery(selectQuery, null)
        var bot: BotEntity? = null
        if (cursor.moveToFirst()) {
            val firstPairName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_PAIR_NAME))
            val secondPairName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SECOND_PAIR_NAME))
            val pairName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PAIR_NAME))
            val threshold = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_THRESHOLD))
            val amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT))
            bot = BotEntity(id, firstPairName, secondPairName, pairName, threshold, amount)
        }
        cursor.close()
        db.close()
        return bot
    }

    fun removeBotById(id: String) {
        val db = writableDatabase
        val deleteQuery = "DELETE FROM $TABLE_NAME WHERE $COLUMN_ID = ?"
        val statement = db.compileStatement(deleteQuery)
        statement.bindString(1, id)
        statement.executeUpdateDelete()
        db.close()
    }
}
