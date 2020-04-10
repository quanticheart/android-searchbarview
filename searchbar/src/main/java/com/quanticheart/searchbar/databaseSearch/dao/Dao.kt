package com.quanticheart.searchbar.databaseSearch.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase

internal open class Dao(private val context: Context) {
    var db: SQLiteDatabase? = null

    fun open() {
        val helper = SQLiteHelper(
            context, DaoConstants.databaseName, null, DaoConstants.databaseVersion
        )
        db = helper.writableDatabase
    }

    fun close() {
        if (db != null) {
            db?.close()
        }
    }
}