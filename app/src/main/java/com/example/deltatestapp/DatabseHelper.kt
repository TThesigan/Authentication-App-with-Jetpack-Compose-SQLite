package com.example.deltatestapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "user_auth.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_USERS = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE $TABLE_USERS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_USERNAME TEXT," +
                "$COLUMN_EMAIL TEXT," +
                "$COLUMN_PASSWORD TEXT)"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun addUser(username: String, email: String, password: String) {
        val db = this.writableDatabase
        val insertQuery = "INSERT INTO $TABLE_USERS ($COLUMN_USERNAME, $COLUMN_EMAIL, $COLUMN_PASSWORD) VALUES ('$username', '$email', '$password')"
        db.execSQL(insertQuery)
        db.close()
    }

    fun isUserExist(email: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USERS WHERE $COLUMN_EMAIL=? AND $COLUMN_PASSWORD=?", arrayOf(email, password))
        val userExists = cursor.count > 0
        cursor.close()
        db.close()
        return userExists
    }

    fun getUser(email: String, password: String): String? {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_USERNAME FROM $TABLE_USERS WHERE $COLUMN_EMAIL=? AND $COLUMN_PASSWORD=?",
            arrayOf(email, password)
        )
        var username: String? = null
        if (cursor.moveToFirst()) {
            // Get the username from the cursor
            username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME))
        }
        cursor.close()
        db.close()
        return username
    }

    fun userExists(email: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USERS WHERE $COLUMN_EMAIL=?", arrayOf(email))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }
}