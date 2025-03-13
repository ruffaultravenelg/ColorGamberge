package com.example.colorgamberge

import android.content.Context
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.core.content.edit

const val SHARED_PREFERENCE_SPACE = "gamedata"

fun storageReadString(context: Context, key: String, default: String = ""): String?{
    val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_SPACE, MODE_PRIVATE)
    return sharedPreferences.getString(key, default)
}

fun storageReadInt(context: Context, key: String, default: Int = 0): Int{
    val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_SPACE, MODE_PRIVATE)
    return sharedPreferences.getInt(key, default)
}

fun storageWriteString(context: Context, key: String, value: String){
    val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_SPACE, MODE_PRIVATE)
    sharedPreferences.edit() {
        putString(key, value)
    }
}

fun storageWriteInt(context: Context, key: String, value: Int){
    val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_SPACE, MODE_PRIVATE)
    sharedPreferences.edit() {
        putInt(key, value)
    }
}