package com.quanticheart.searchbar.databaseSearch

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.quanticheart.searchbar.databaseSearch.dao.Dao
import com.quanticheart.searchbar.databaseSearch.dao.DaoConstants
import java.util.*

internal class DataBaseSearchBar(context: Context) : Dao(context) {

    private var tableName: String? = null

    init {
        tableName = context.javaClass.name.replace(".", "")
    }

    fun insertInHistory(searchText: String) {
        open()
        createTable()
        insertTextInDB(searchText)
        close()
    }

    private fun insertTextInDB(searchText: String) {
        db?.beginTransaction()
        try {
            val cv = ContentValues()
            cv.clear()
            cv.put(DaoConstants.databaseSearchText, searchText)
            db?.insert(tableName, null, cv)
            db?.setTransactionSuccessful()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db?.endTransaction()
        }
    }

    private fun createTable() {
        try {
            val sb = StringBuilder()
            sb.append(
                "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                        "  [${DaoConstants.databaseID}] INTEGER," +
                        "  [${DaoConstants.databaseSearchText}] INT NOT NULL," +
                        "  CONSTRAINT [] PRIMARY KEY([${DaoConstants.databaseID}]));"
            )
            val commands = sb.toString().split(";")
            for (command in commands) {
                if (command.isNotEmpty())
                    db?.execSQL(command.toLowerCase(Locale.getDefault()))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun deleteTable() {
        open()
        try {
            val sb = StringBuilder()
            sb.append("DROP TABLE IF EXISTS $tableName;")
            val commands = sb.toString().split(";").toTypedArray()
            for (command in commands) {
                if (command.isNotEmpty())
                    db?.execSQL(command.toLowerCase(Locale.getDefault()))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        close()
    }

    fun getHistoryList(): ArrayList<String> {
        val list = ArrayList<String>()
        open()
        val cursor: Cursor?
        try {
            val commands = " select * from $tableName"
            cursor = db?.rawQuery(commands.toLowerCase(Locale.getDefault()), null)
            cursor?.let {
                while (cursor.moveToNext()) {
                    list.add(cursor.getString(cursor.getColumnIndex(DaoConstants.databaseSearchText)))
                }
                cursor.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        close()
        return list
    }
}